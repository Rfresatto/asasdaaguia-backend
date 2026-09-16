package br.com.aguiabranca.asasdaaguia.ideia;

import br.com.aguiabranca.asasdaaguia.common.exception.AcessoNegadoException;
import br.com.aguiabranca.asasdaaguia.common.exception.RecursoNaoEncontradoException;
import br.com.aguiabranca.asasdaaguia.estrategia.EstrategiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IdeiaService {

    private final IdeiaRepository ideiaRepository;
    private final EstrategiaService estrategiaService;

    public IdeiaModel criar(IdeiaDTO dto, String operadorId) {
        String estrategiaVigenteId = estrategiaService.buscarVigente().getId();

        IdeiaModel nova = IdeiaModel.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .operadorId(operadorId)
                .estrategiaId(estrategiaVigenteId)
                .status(IdeiaModel.StatusIdeia.PENDENTE)
                .dataCriacao(LocalDate.now())
                .build();

        return ideiaRepository.save(nova);
    }

    public List<IdeiaModel> listarMinhas(String operadorId) {
        return ideiaRepository.findByOperadorId(operadorId);
    }

    public List<IdeiaModel> listarTodas() {
        return ideiaRepository.findAll();
    }

    public IdeiaModel buscarPorId(String id) {
        return ideiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ideia nao encontrada: " + id));
    }

    public IdeiaModel atualizar(String id, IdeiaDTO dto, String operadorId) {
        IdeiaModel existente = buscarPorId(id);
        validarDono(existente, operadorId);

        existente.setTitulo(dto.titulo());
        existente.setDescricao(dto.descricao());

        return ideiaRepository.save(existente);
    }

    public void excluir(String id, String operadorId) {
        IdeiaModel existente = buscarPorId(id);
        validarDono(existente, operadorId);
        ideiaRepository.delete(existente);
    }

    public IdeiaModel priorizar(String id, PriorizacaoDTO dto) {
        IdeiaModel existente = buscarPorId(id);
        existente.setPrioridade(dto.prioridade());
        return ideiaRepository.save(existente);
    }

    public IdeiaModel aprovar(String id) {
        IdeiaModel existente = buscarPorId(id);
        existente.setStatus(IdeiaModel.StatusIdeia.APROVADA);
        return ideiaRepository.save(existente);
    }

    public IdeiaModel reprovar(String id) {
        IdeiaModel existente = buscarPorId(id);
        existente.setStatus(IdeiaModel.StatusIdeia.REPROVADA);
        return ideiaRepository.save(existente);
    }

    private void validarDono(IdeiaModel ideia, String operadorId) {
        if (!ideia.getOperadorId().equals(operadorId)) {
            throw new AcessoNegadoException("Voce so pode alterar suas proprias ideias");
        }
    }

    public IdeiaModel atualizarCompleto(String id, IdeiaDTO dto, String operadorId) {
        IdeiaModel existente = buscarPorId(id);
        validarDono(existente, operadorId);

        existente.setTitulo(dto.titulo());
        existente.setDescricao(dto.descricao());

        return ideiaRepository.save(existente);
    }

}