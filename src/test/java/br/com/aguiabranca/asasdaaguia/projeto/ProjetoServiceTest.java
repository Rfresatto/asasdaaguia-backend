package br.com.aguiabranca.asasdaaguia.projeto;

import br.com.aguiabranca.asasdaaguia.common.exception.RecursoNaoEncontradoException;
import br.com.aguiabranca.asasdaaguia.estrategia.EstrategiaModel;
import br.com.aguiabranca.asasdaaguia.estrategia.EstrategiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private EstrategiaService estrategiaService;

    @InjectMocks
    private ProjetoService projetoService;

    private ProjetoDTO dto;
    private EstrategiaModel estrategiaVigente;

    @BeforeEach
    void setUp() {
        dto = new ProjetoDTO(
                "App de vistoria digital",
                "Checklist digital para vistoria de onibus",
                "ideia-1",
                new BigDecimal("45000.00"),
                LocalDate.of(2026, 12, 15)
        );
        estrategiaVigente = EstrategiaModel.builder().id("estrategia-1").titulo("Digitalizacao").build();
    }

    @Test
    void deveCriarProjetoVinculadoAEstrategiaVigente() {
        when(estrategiaService.buscarVigente()).thenReturn(estrategiaVigente);
        when(projetoRepository.save(any(ProjetoModel.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjetoModel resultado = projetoService.criar(dto, "carla@aguiabranca.com");

        assertEquals("estrategia-1", resultado.getEstrategiaId());
        assertEquals("carla@aguiabranca.com", resultado.getGestorResponsavelId());
        assertEquals(ProjetoModel.EtapaProjeto.PLANEJAMENTO, resultado.getEtapa());
        assertEquals(dto.investimento(), resultado.getInvestimento());
    }

    @Test
    void deveLancarExcecaoAoCriarProjetoSemEstrategiaVigente() {
        when(estrategiaService.buscarVigente())
                .thenThrow(new RecursoNaoEncontradoException("Nenhuma estrategia vigente definida"));

        assertThrows(RecursoNaoEncontradoException.class,
                () -> projetoService.criar(dto, "carla@aguiabranca.com"));

        verify(projetoRepository, never()).save(any());
    }

    @Test
    void deveListarTodosOsProjetos() {
        List<ProjetoModel> lista = List.of(
                ProjetoModel.builder().id("1").nome("Projeto A").build(),
                ProjetoModel.builder().id("2").nome("Projeto B").build()
        );
        when(projetoRepository.findAll()).thenReturn(lista);

        List<ProjetoModel> resultado = projetoService.listarTodos();

        assertEquals(2, resultado.size());
    }

    @Test
    void deveBuscarProjetoPorIdComSucesso() {
        ProjetoModel existente = ProjetoModel.builder().id("1").nome("Projeto A").build();
        when(projetoRepository.findById("1")).thenReturn(Optional.of(existente));

        ProjetoModel resultado = projetoService.buscarPorId("1");

        assertEquals("1", resultado.getId());
    }

    @Test
    void deveLancarRecursoNaoEncontradoQuandoProjetoNaoExiste() {
        when(projetoRepository.findById("id-invalido")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> projetoService.buscarPorId("id-invalido"));
    }

    @Test
    void deveAtualizarCadastroDoProjeto() {
        ProjetoModel existente = ProjetoModel.builder()
                .id("1")
                .nome("Nome antigo")
                .investimento(new BigDecimal("10000.00"))
                .build();
        when(projetoRepository.findById("1")).thenReturn(Optional.of(existente));
        when(projetoRepository.save(any(ProjetoModel.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjetoDTO dtoAtualizado = new ProjetoDTO(
                "Nome novo", "Descricao nova", "ideia-2",
                new BigDecimal("50000.00"), LocalDate.of(2027, 1, 10)
        );

        ProjetoModel resultado = projetoService.atualizarCadastro("1", dtoAtualizado);

        assertEquals("Nome novo", resultado.getNome());
        assertEquals(new BigDecimal("50000.00"), resultado.getInvestimento());
        assertEquals("ideia-2", resultado.getIdeiaOrigemId());
    }

    @Test
    void deveAtualizarProgressoComRetornoEProdutividade() {
        ProjetoModel existente = ProjetoModel.builder()
                .id("1")
                .etapa(ProjetoModel.EtapaProjeto.EM_ANDAMENTO)
                .build();
        when(projetoRepository.findById("1")).thenReturn(Optional.of(existente));
        when(projetoRepository.save(any(ProjetoModel.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjetoAtualizacaoDTO progressoDTO = new ProjetoAtualizacaoDTO(
                ProjetoModel.EtapaProjeto.CONCLUIDO,
                new BigDecimal("62000.00"),
                new BigDecimal("15.5"),
                null
        );

        ProjetoModel resultado = projetoService.atualizarProgresso("1", progressoDTO);

        assertEquals(ProjetoModel.EtapaProjeto.CONCLUIDO, resultado.getEtapa());
        assertEquals(new BigDecimal("62000.00"), resultado.getRetornoFinanceiro());
        assertEquals(new BigDecimal("15.5"), resultado.getProdutividadeGanhaPercentual());
    }

    @Test
    void deveAtualizarProgressoSemAlterarRetornoQuandoNaoInformado() {
        ProjetoModel existente = ProjetoModel.builder()
                .id("1")
                .etapa(ProjetoModel.EtapaProjeto.PLANEJAMENTO)
                .retornoFinanceiro(null)
                .build();
        when(projetoRepository.findById("1")).thenReturn(Optional.of(existente));
        when(projetoRepository.save(any(ProjetoModel.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjetoAtualizacaoDTO progressoDTO = new ProjetoAtualizacaoDTO(
                ProjetoModel.EtapaProjeto.EM_ANDAMENTO, null, null, null
        );

        ProjetoModel resultado = projetoService.atualizarProgresso("1", progressoDTO);

        assertEquals(ProjetoModel.EtapaProjeto.EM_ANDAMENTO, resultado.getEtapa());
        assertNull(resultado.getRetornoFinanceiro());
    }

    @Test
    void deveExcluirProjetoExistente() {
        ProjetoModel existente = ProjetoModel.builder().id("1").nome("Projeto A").build();
        when(projetoRepository.findById("1")).thenReturn(Optional.of(existente));

        projetoService.excluir("1");

        verify(projetoRepository).delete(existente);
    }

    @Test
    void deveLancarExcecaoAoExcluirProjetoInexistente() {
        when(projetoRepository.findById("id-invalido")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> projetoService.excluir("id-invalido"));

        verify(projetoRepository, never()).delete(any());
    }
}