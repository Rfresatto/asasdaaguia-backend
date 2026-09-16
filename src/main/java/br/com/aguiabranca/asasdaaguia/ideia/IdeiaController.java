package br.com.aguiabranca.asasdaaguia.ideia;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Tag(name = "Ideia")
@RestController
@RequestMapping("/ideias")
@RequiredArgsConstructor
public class IdeiaController {

    private final IdeiaService ideiaService;

    @Operation(summary = "Cadastrar ideia", description = "Operador cadastra uma nova ideia, vinculada automaticamente a estrategia vigente. Somente OPERADOR.")
    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<IdeiaModel> criar(@Valid @RequestBody IdeiaDTO dto, Authentication authentication) {
        IdeiaModel criada = ideiaService.criar(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @Operation(summary = "Listar ideias", description = "Operador visualiza apenas suas proprias ideias. Gestor e Lideranca visualizam todas as ideias cadastradas.")
    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<IdeiaModel>> listar(Authentication authentication) {
        boolean isOperador = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_OPERADOR"));

        List<IdeiaModel> resultado = isOperador
                ? ideiaService.listarMinhas(authentication.getName())
                : ideiaService.listarTodas();

        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Buscar ideia por ID", description = "Retorna os dados de uma ideia especifica pelo seu identificador.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<IdeiaModel> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(ideiaService.buscarPorId(id));
    }

    @Operation(summary = "Editar ideia", description = "Atualiza titulo e descricao de uma ideia. Somente o operador dono da ideia pode edita-la.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<IdeiaModel> atualizar(@PathVariable String id, @Valid @RequestBody IdeiaDTO dto, Authentication authentication) {
        return ResponseEntity.ok(ideiaService.atualizar(id, dto, authentication.getName()));
    }

    @Operation(summary = "Excluir ideia", description = "Remove permanentemente uma ideia. Somente o operador dono da ideia pode exclui-la.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> excluir(@PathVariable String id, Authentication authentication) {
        ideiaService.excluir(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Priorizar ideia", description = "Define um nivel de prioridade numerico para a ideia. Somente GESTOR.")
    @PatchMapping("/{id}/priorizar")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<IdeiaModel> priorizar(@PathVariable String id, @Valid @RequestBody PriorizacaoDTO dto) {
        return ResponseEntity.ok(ideiaService.priorizar(id, dto));
    }

    @Operation(summary = "Aprovar ideia", description = "Marca a ideia como aprovada, sinalizando que ela pode originar um projeto. Somente GESTOR.")
    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<IdeiaModel> aprovar(@PathVariable String id) {
        return ResponseEntity.ok(ideiaService.aprovar(id));
    }

    @Operation(summary = "Reprovar ideia", description = "Marca a ideia como reprovada. Somente GESTOR.")
    @PatchMapping("/{id}/reprovar")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<IdeiaModel> reprovar(@PathVariable String id) {
        return ResponseEntity.ok(ideiaService.reprovar(id));
    }
}