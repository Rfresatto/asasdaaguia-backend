package br.com.aguiabranca.asasdaaguia.projeto;

import br.com.aguiabranca.asasdaaguia.common.exception.RecursoNaoEncontradoException;
import br.com.aguiabranca.asasdaaguia.estrategia.EstrategiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final EstrategiaService estrategiaService;

    public ProjetoModel criar(ProjetoDTO dto, String gestorId) {
        String estrategiaVigenteId = estrategiaService.buscarVigente().getId();

        ProjetoModel novo = ProjetoModel.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .estrategiaId(estrategiaVigenteId)
                .ideiaOrigemId(dto.ideiaOrigemId())
                .etapa(ProjetoModel.EtapaProjeto.PLANEJAMENTO)
                .investimento(dto.investimento())
                .prazo(dto.prazo())
                .gestorResponsavelId(gestorId)
                .build();

        return projetoRepository.save(novo);
    }

    public List<ProjetoModel> listarTodos() {
        return projetoRepository.findAll();
    }

    public ProjetoModel buscarPorId(String id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto nao encontrado: " + id));
    }

    public ProjetoModel atualizarProgresso(String id, ProjetoAtualizacaoDTO dto) {
        ProjetoModel existente = buscarPorId(id);

        existente.setEtapa(dto.etapa());

        if (dto.retornoFinanceiro() != null) {
            existente.setRetornoFinanceiro(dto.retornoFinanceiro());
        }
        if (dto.prazo() != null) {
            existente.setPrazo(dto.prazo());
        }

        if (dto.produtividadeGanhaPercentual() != null) {
            existente.setProdutividadeGanhaPercentual(dto.produtividadeGanhaPercentual());
        }

        return projetoRepository.save(existente);
    }

    public void excluir(String id) {
        ProjetoModel existente = buscarPorId(id);
        projetoRepository.delete(existente);
    }

    public ProjetoModel atualizarCadastro(String id, ProjetoDTO dto) {
        ProjetoModel existente = buscarPorId(id);

        existente.setNome(dto.nome());
        existente.setDescricao(dto.descricao());
        existente.setIdeiaOrigemId(dto.ideiaOrigemId());
        existente.setInvestimento(dto.investimento());
        existente.setPrazo(dto.prazo());

        return projetoRepository.save(existente);
    }

}