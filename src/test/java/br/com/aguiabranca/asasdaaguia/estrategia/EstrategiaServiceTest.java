package br.com.aguiabranca.asasdaaguia.estrategia;

import br.com.aguiabranca.asasdaaguia.common.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstrategiaServiceTest {

    @Mock
    private EstrategiaRepository estrategiaRepository;

    @InjectMocks
    private EstrategiaService estrategiaService;

    private EstrategiaDTO dto;

    @BeforeEach
    void setUp() {
        dto = new EstrategiaDTO(
                "Expansao no Sudeste",
                "Fortalecer presenca nas linhas intermunicipais",
                "Expansao",
                "Sudeste 2026"
        );
    }

    @Test
    void deveCriarPrimeiraEstrategiaSemDesativarNenhuma() {
        when(estrategiaRepository.findByVigenteTrue()).thenReturn(Optional.empty());
        when(estrategiaRepository.save(any(EstrategiaModel.class))).thenAnswer(inv -> inv.getArgument(0));

        EstrategiaModel resultado = estrategiaService.criar(dto, "lideranca@aguiabranca.com");

        assertTrue(resultado.isVigente());
        assertEquals("lideranca@aguiabranca.com", resultado.getCriadoPorId());
        verify(estrategiaRepository, times(1)).save(any(EstrategiaModel.class));
    }

    @Test
    void deveDesativarEstrategiaVigenteAnteriorAoCriarNova() {
        EstrategiaModel antiga = EstrategiaModel.builder()
                .id("antiga-id")
                .titulo("Estrategia antiga")
                .vigente(true)
                .build();

        when(estrategiaRepository.findByVigenteTrue()).thenReturn(Optional.of(antiga));
        when(estrategiaRepository.save(any(EstrategiaModel.class))).thenAnswer(inv -> inv.getArgument(0));

        estrategiaService.criar(dto, "lideranca@aguiabranca.com");

        ArgumentCaptor<EstrategiaModel> captor = ArgumentCaptor.forClass(EstrategiaModel.class);
        verify(estrategiaRepository, times(2)).save(captor.capture());

        EstrategiaModel primeiroSalvamento = captor.getAllValues().getFirst();
        assertEquals("antiga-id", primeiroSalvamento.getId());
        assertFalse(primeiroSalvamento.isVigente());

        EstrategiaModel segundoSalvamento = captor.getAllValues().get(1);
        assertTrue(segundoSalvamento.isVigente());
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        EstrategiaModel existente = EstrategiaModel.builder().id("123").titulo("Teste").build();
        when(estrategiaRepository.findById("123")).thenReturn(Optional.of(existente));

        EstrategiaModel resultado = estrategiaService.buscarPorId("123");

        assertEquals("123", resultado.getId());
    }

    @Test
    void deveLancarRecursoNaoEncontradoQuandoIdNaoExiste() {
        when(estrategiaRepository.findById("id-invalido")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> estrategiaService.buscarPorId("id-invalido"));
    }

    @Test
    void deveLancarRecursoNaoEncontradoQuandoNenhumaVigente() {
        when(estrategiaRepository.findByVigenteTrue()).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> estrategiaService.buscarVigente());
    }

    @Test
    void deveListarTodas() {
        List<EstrategiaModel> lista = List.of(
                EstrategiaModel.builder().id("1").titulo("A").build(),
                EstrategiaModel.builder().id("2").titulo("B").build()
        );
        when(estrategiaRepository.findAll()).thenReturn(lista);

        List<EstrategiaModel> resultado = estrategiaService.listarTodas();

        assertEquals(2, resultado.size());
    }

    @Test
    void deveAtualizarEstrategiaExistente() {
        EstrategiaModel existente = EstrategiaModel.builder().id("123").titulo("Antigo titulo").build();
        when(estrategiaRepository.findById("123")).thenReturn(Optional.of(existente));
        when(estrategiaRepository.save(any(EstrategiaModel.class))).thenAnswer(inv -> inv.getArgument(0));

        EstrategiaModel resultado = estrategiaService.atualizar("123", dto);

        assertEquals(dto.titulo(), resultado.getTitulo());
        assertEquals(dto.categoria(), resultado.getCategoria());
    }

    @Test
    void deveExcluirEstrategiaExistente() {
        EstrategiaModel existente = EstrategiaModel.builder().id("123").titulo("Teste").build();
        when(estrategiaRepository.findById("123")).thenReturn(Optional.of(existente));

        estrategiaService.excluir("123");

        verify(estrategiaRepository).delete(existente);
    }

    @Test
    void deveLancarExcecaoAoExcluirEstrategiaInexistente() {
        when(estrategiaRepository.findById("id-invalido")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> estrategiaService.excluir("id-invalido"));
        verify(estrategiaRepository, never()).delete(any());
    }
}