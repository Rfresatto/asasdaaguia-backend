package br.com.aguiabranca.asasdaaguia.estrategia;

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

@Tag(name = "Estratégia")
@RestController
@RequestMapping("/estrategias")
@RequiredArgsConstructor
public class EstrategiaController {

    private final EstrategiaService estrategiaService;

    @Operation(summary = "Listar todas as estrategias", description = "Retorna o historico completo de orientacoes estrategicas cadastradas. Acessivel a todos os perfis autenticados.")
    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<EstrategiaModel>> listarTodas() {
        return ResponseEntity.ok(estrategiaService.listarTodas());
    }

    @Operation(summary = "Buscar estrategia vigente", description = "Retorna a unica estrategia marcada como vigente no momento.")
    @GetMapping("/vigente")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<EstrategiaModel> buscarVigente() {
        return ResponseEntity.ok(estrategiaService.buscarVigente());
    }

    @Operation(summary = "Buscar estrategia por ID", description = "Retorna os dados de uma estrategia especifica pelo seu identificador.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<EstrategiaModel> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(estrategiaService.buscarPorId(id));
    }

    @Operation(summary = "Criar nova estrategia", description = "Cria uma nova estrategia e a marca como vigente, desativando automaticamente a estrategia vigente anterior. Somente LIDERANCA.")
    @PostMapping
    @PreAuthorize("hasRole('LIDERANCA')")
    public ResponseEntity<EstrategiaModel> criar(@Valid @RequestBody EstrategiaDTO dto, Authentication authentication) {
        String liderancaId = authentication.getName(); // email do usuario logado (subject do JWT)
        EstrategiaModel criada = estrategiaService.criar(dto, liderancaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @Operation(summary = "Editar estrategia", description = "Atualiza os dados de uma estrategia existente (titulo, descricao, categoria, campanha). Somente LIDERANCA.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIDERANCA')")
    public ResponseEntity<EstrategiaModel> atualizar(@PathVariable String id, @Valid @RequestBody EstrategiaDTO dto) {
        return ResponseEntity.ok(estrategiaService.atualizar(id, dto));
    }

    @Operation(summary = "Excluir estrategia", description = "Remove permanentemente uma estrategia. Somente LIDERANCA.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIDERANCA')")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        estrategiaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}