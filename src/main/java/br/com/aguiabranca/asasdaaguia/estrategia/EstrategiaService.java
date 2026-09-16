package br.com.aguiabranca.asasdaaguia.estrategia;

import br.com.aguiabranca.asasdaaguia.common.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstrategiaService {

    private final EstrategiaRepository estrategiaRepository;

    public List<EstrategiaModel> listarTodas() {
        return estrategiaRepository.findAll();
    }

    public EstrategiaModel buscarPorId(String id) {
        return estrategiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estrategia nao encontrada: " + id));
    }

    public EstrategiaModel buscarVigente() {
        return estrategiaRepository.findByVigenteTrue()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma estrategia vigente definida"));
    }

    public EstrategiaModel criar(EstrategiaDTO dto, String liderancaId) {
        desativarVigenteAtual();

        EstrategiaModel nova = EstrategiaModel.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .categoria(dto.categoria())
                .campanha(dto.campanha())
                .dataCriacao(LocalDate.now())
                .vigente(true)
                .criadoPorId(liderancaId)
                .build();

        return estrategiaRepository.save(nova);
    }

    public EstrategiaModel atualizar(String id, EstrategiaDTO dto) {
        EstrategiaModel existente = buscarPorId(id);

        existente.setTitulo(dto.titulo());
        existente.setDescricao(dto.descricao());
        existente.setCategoria(dto.categoria());
        existente.setCampanha(dto.campanha());

        return estrategiaRepository.save(existente);
    }

    public void excluir(String id) {
        EstrategiaModel existente = buscarPorId(id);
        estrategiaRepository.delete(existente);
    }

    private void desativarVigenteAtual() {
        estrategiaRepository.findByVigenteTrue().ifPresent(atual -> {
            atual.setVigente(false);
            estrategiaRepository.save(atual);
        });
    }
}