package br.com.aguiabranca.asasdaaguia.projeto;

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

@Tag(name = "Projeto")
@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;

    @Operation(summary = "Cadastrar projeto", description = "Gestor cadastra um novo projeto, vinculado automaticamente a estrategia vigente. Somente GESTOR.")
    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<ProjetoModel> criar(@Valid @RequestBody ProjetoDTO dto, Authentication authentication) {
        ProjetoModel criado = projetoService.criar(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(summary = "Listar projetos", description = "Retorna todos os projetos cadastrados. Acessivel a GESTOR e LIDERANCA.")
    @GetMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<List<ProjetoModel>> listarTodos() {
        return ResponseEntity.ok(projetoService.listarTodos());
    }

    @Operation(summary = "Buscar projeto por ID", description = "Retorna os dados de um projeto especifico pelo seu identificador. Acessivel a GESTOR e LIDERANCA.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<ProjetoModel> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(projetoService.buscarPorId(id));
    }



    @Operation(summary = "Atualizar progresso do projeto", description = "Atualiza etapa, retorno financeiro e produtividade ganha do projeto, para acompanhamento de resultados. Somente GESTOR.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<ProjetoModel> atualizarProgresso(@PathVariable String id, @Valid @RequestBody ProjetoAtualizacaoDTO dto) {
        return ResponseEntity.ok(projetoService.atualizarProgresso(id, dto));
    }

    @Operation(summary = "Excluir projeto", description = "Remove permanentemente um projeto. Somente GESTOR.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        projetoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Editar cadastro do projeto", description = "Atualiza dados de cadastro do projeto (nome, descricao, investimento, prazo, ideia de origem). Somente GESTOR.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<ProjetoModel> atualizarCadastro(@PathVariable String id, @Valid @RequestBody ProjetoDTO dto) {
        return ResponseEntity.ok(projetoService.atualizarCadastro(id, dto));
    }
}