package br.com.aguiabranca.asasdaaguia.dashboard;

import br.com.aguiabranca.asasdaaguia.projeto.ProjetoModel;
import br.com.aguiabranca.asasdaaguia.projeto.ProjetoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private GeminiClient geminiClient;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void deveRetornarResumoZeradoQuandoNaoHaProjetos() {
        when(projetoRepository.findAll()).thenReturn(List.of());

        ResumoGeralDTO resultado = dashboardService.resumoGeral();

        assertEquals(0, resultado.totalProjetos());
        assertEquals(BigDecimal.ZERO, resultado.investimentoTotal());
        assertEquals(BigDecimal.ZERO, resultado.retornoTotal());
        assertEquals(BigDecimal.ZERO, resultado.lucroTotal());
        assertEquals(BigDecimal.ZERO, resultado.roiMedio());
        assertTrue(resultado.projetos().isEmpty());
    }

    @Test
    void deveCalcularResumoGeralComUmProjetoConcluido() {
        ProjetoModel projeto = ProjetoModel.builder()
                .id("1")
                .nome("App de vistoria digital")
                .estrategiaId("estrategia-1")
                .etapa(ProjetoModel.EtapaProjeto.CONCLUIDO)
                .investimento(new BigDecimal("45000.00"))
                .retornoFinanceiro(new BigDecimal("62000.00"))
                .build();

        when(projetoRepository.findAll()).thenReturn(List.of(projeto));

        ResumoGeralDTO resultado = dashboardService.resumoGeral();

        assertEquals(1, resultado.totalProjetos());
        assertEquals(1, resultado.projetosConcluidos());
        assertEquals(0, resultado.projetosEmAndamento());
        assertEquals(new BigDecimal("45000.00"), resultado.investimentoTotal());
        assertEquals(new BigDecimal("62000.00"), resultado.retornoTotal());
        assertEquals(new BigDecimal("17000.00"), resultado.lucroTotal());
        // ROI = (62000 - 45000) / 45000 * 100 = 37.7777...% -> arredondado 4 casas
        assertEquals(new BigDecimal("37.7800"), resultado.roiMedio());
    }

    @Test
    void deveRetornarRoiZeroQuandoInvestimentoForZero() {
        ProjetoModel projeto = ProjetoModel.builder()
                .id("1")
                .nome("Projeto sem investimento")
                .etapa(ProjetoModel.EtapaProjeto.PLANEJAMENTO)
                .investimento(BigDecimal.ZERO)
                .retornoFinanceiro(null)
                .build();

        when(projetoRepository.findAll()).thenReturn(List.of(projeto));

        ResumoGeralDTO resultado = dashboardService.resumoGeral();

        assertEquals(BigDecimal.ZERO, resultado.roiMedio());
        assertEquals(BigDecimal.ZERO, resultado.projetos().getFirst().roi());
    }

    @Test
    void deveTratarRetornoFinanceiroNuloComoZeroNoCalculo() {
        ProjetoModel projeto = ProjetoModel.builder()
                .id("1")
                .nome("Projeto em andamento")
                .etapa(ProjetoModel.EtapaProjeto.EM_ANDAMENTO)
                .investimento(new BigDecimal("10000.00"))
                .retornoFinanceiro(null) // ainda nao gerou retorno
                .build();

        when(projetoRepository.findAll()).thenReturn(List.of(projeto));

        ResumoGeralDTO resultado = dashboardService.resumoGeral();

        assertEquals(new BigDecimal("10000.00").negate(), resultado.lucroTotal());
        assertEquals(BigDecimal.ZERO, resultado.projetos().getFirst().retornoFinanceiro());
    }

    @Test
    void deveFiltrarProjetosPorEstrategia() {
        ProjetoModel projetoDaEstrategia = ProjetoModel.builder()
                .id("1").nome("Projeto A").estrategiaId("estrategia-1")
                .investimento(BigDecimal.ZERO).build();

        when(projetoRepository.findByEstrategiaId("estrategia-1")).thenReturn(List.of(projetoDaEstrategia));

        List<ResumoProjetoDTO> resultado = dashboardService.resumoPorEstrategia("estrategia-1");

        assertEquals(1, resultado.size());
        assertEquals("estrategia-1", resultado.getFirst().estrategiaId());
        verify(projetoRepository, never()).findAll();
    }

    @Test
    void deveGerarInsightsComPromptContendoDadosDoResumo() {
        ProjetoModel projeto = ProjetoModel.builder()
                .id("1")
                .nome("App de vistoria digital")
                .etapa(ProjetoModel.EtapaProjeto.CONCLUIDO)
                .investimento(new BigDecimal("45000.00"))
                .retornoFinanceiro(new BigDecimal("62000.00"))
                .build();

        when(projetoRepository.findAll()).thenReturn(List.of(projeto));
        when(geminiClient.gerarInsights(anyString())).thenReturn("- Insight gerado pela IA");

        InsightsDTO resultado = dashboardService.gerarInsights();

        assertEquals("- Insight gerado pela IA", resultado.insights());

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(geminiClient).gerarInsights(promptCaptor.capture());

        String promptEnviado = promptCaptor.getValue();
        assertTrue(promptEnviado.contains("App de vistoria digital"));
        assertTrue(promptEnviado.contains("45000.00"));
    }
}