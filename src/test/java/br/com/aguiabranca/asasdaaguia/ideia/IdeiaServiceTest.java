package br.com.aguiabranca.asasdaaguia.ideia;

import br.com.aguiabranca.asasdaaguia.common.exception.AcessoNegadoException;
import br.com.aguiabranca.asasdaaguia.common.exception.RecursoNaoEncontradoException;
import br.com.aguiabranca.asasdaaguia.estrategia.EstrategiaModel;
import br.com.aguiabranca.asasdaaguia.estrategia.EstrategiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdeiaServiceTest {

    @Mock
    private IdeiaRepository ideiaRepository;

    @Mock
    private EstrategiaService estrategiaService;

    @InjectMocks
    private IdeiaService ideiaService;

    private IdeiaDTO dto;
    private EstrategiaModel estrategiaVigente;

    @BeforeEach
    void setUp() {
        dto = new IdeiaDTO("Checklist digital", "Substituir o processo em papel");
        estrategiaVigente = EstrategiaModel.builder().id("estrategia-1").titulo("Digitalizacao").build();
    }

    @Test
    void deveCriarIdeiaVinculadaAEstrategiaVigente() {
        when(estrategiaService.buscarVigente()).thenReturn(estrategiaVigente);
        when(ideiaRepository.save(any(IdeiaModel.class))).thenAnswer(inv -> inv.getArgument(0));

        IdeiaModel resultado = ideiaService.criar(dto, "joao@aguiabranca.com");

        assertEquals("joao@aguiabranca.com", resultado.getOperadorId());
        assertEquals("estrategia-1", resultado.getEstrategiaId());
        assertEquals(IdeiaModel.StatusIdeia.PENDENTE, resultado.getStatus());
    }

    @Test
    void deveLancarExcecaoAoCriarIdeiaSemEstrategiaVigente() {
        when(estrategiaService.buscarVigente())
                .thenThrow(new RecursoNaoEncontradoException("Nenhuma estrategia vigente definida"));

        assertThrows(RecursoNaoEncontradoException.class,
                () -> ideiaService.criar(dto, "joao@aguiabranca.com"));

        verify(ideiaRepository, never()).save(any());
    }

    @Test
    void deveListarApenasIdeiasDoOperador() {
        List<IdeiaModel> minhas = List.of(
                IdeiaModel.builder().id("1").operadorId("joao@aguiabranca.com").build()
        );
        when(ideiaRepository.findByOperadorId("joao@aguiabranca.com")).thenReturn(minhas);

        List<IdeiaModel> resultado = ideiaService.listarMinhas("joao@aguiabranca.com");

        assertEquals(1, resultado.size());
        verify(ideiaRepository, never()).findAll();
    }

    @Test
    void deveLancarRecursoNaoEncontradoQuandoIdeiaNaoExiste() {
        when(ideiaRepository.findById("id-invalido")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class,
                () -> ideiaService.buscarPorId("id-invalido"));
    }

    @Test
    void deveAtualizarIdeiaQuandoOperadorEDono() {
        IdeiaModel existente = IdeiaModel.builder()
                .id("1")
                .operadorId("joao@aguiabranca.com")
                .titulo("Titulo antigo")
                .build();
        when(ideiaRepository.findById("1")).thenReturn(Optional.of(existente));
        when(ideiaRepository.save(any(IdeiaModel.class))).thenAnswer(inv -> inv.getArgument(0));

        IdeiaModel resultado = ideiaService.atualizar("1", dto, "joao@aguiabranca.com");

        assertEquals(dto.titulo(), resultado.getTitulo());
    }

    @Test
    void deveLancarAcessoNegadoQuandoOperadorNaoEDono() {
        IdeiaModel existente = IdeiaModel.builder()
                .id("1")
                .operadorId("joao@aguiabranca.com")
                .build();
        when(ideiaRepository.findById("1")).thenReturn(Optional.of(existente));

        assertThrows(AcessoNegadoException.class,
                () -> ideiaService.atualizar("1", dto, "outro-operador@aguiabranca.com"));

        verify(ideiaRepository, never()).save(any());
    }

    @Test
    void deveExcluirIdeiaQuandoOperadorEDono() {
        IdeiaModel existente = IdeiaModel.builder()
                .id("1")
                .operadorId("joao@aguiabranca.com")
                .build();
        when(ideiaRepository.findById("1")).thenReturn(Optional.of(existente));

        ideiaService.excluir("1", "joao@aguiabranca.com");

        verify(ideiaRepository).delete(existente);
    }

    @Test
    void deveLancarAcessoNegadoAoExcluirIdeiaDeOutroOperador() {
        IdeiaModel existente = IdeiaModel.builder()
                .id("1")
                .operadorId("joao@aguiabranca.com")
                .build();
        when(ideiaRepository.findById("1")).thenReturn(Optional.of(existente));

        assertThrows(AcessoNegadoException.class,
                () -> ideiaService.excluir("1", "outro-operador@aguiabranca.com"));

        verify(ideiaRepository, never()).delete(any());
    }

    @Test
    void devePriorizarIdeia() {
        IdeiaModel existente = IdeiaModel.builder().id("1").build();
        PriorizacaoDTO priorizacaoDTO = new PriorizacaoDTO(1);

        when(ideiaRepository.findById("1")).thenReturn(Optional.of(existente));
        when(ideiaRepository.save(any(IdeiaModel.class))).thenAnswer(inv -> inv.getArgument(0));

        IdeiaModel resultado = ideiaService.priorizar("1", priorizacaoDTO);

        assertEquals(1, resultado.getPrioridade());
    }

    @Test
    void deveAprovarIdeia() {
        IdeiaModel existente = IdeiaModel.builder().id("1").status(IdeiaModel.StatusIdeia.PENDENTE).build();
        when(ideiaRepository.findById("1")).thenReturn(Optional.of(existente));
        when(ideiaRepository.save(any(IdeiaModel.class))).thenAnswer(inv -> inv.getArgument(0));

        IdeiaModel resultado = ideiaService.aprovar("1");

        assertEquals(IdeiaModel.StatusIdeia.APROVADA, resultado.getStatus());
    }

    @Test
    void deveReprovarIdeia() {
        IdeiaModel existente = IdeiaModel.builder().id("1").status(IdeiaModel.StatusIdeia.PENDENTE).build();
        when(ideiaRepository.findById("1")).thenReturn(Optional.of(existente));
        when(ideiaRepository.save(any(IdeiaModel.class))).thenAnswer(inv -> inv.getArgument(0));

        IdeiaModel resultado = ideiaService.reprovar("1");

        assertEquals(IdeiaModel.StatusIdeia.REPROVADA, resultado.getStatus());
    }
}