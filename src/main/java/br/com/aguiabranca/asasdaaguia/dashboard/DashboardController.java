package br.com.aguiabranca.asasdaaguia.dashboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Dashboard")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LIDERANCA')")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Resumo geral do dashboard", description = "Retorna metricas agregadas de todos os projetos: ROI medio, lucro total, investimento total e retorno total. Somente LIDERANCA.")
    @GetMapping("/resumo")
    public ResponseEntity<ResumoGeralDTO> resumoGeral() {
        return ResponseEntity.ok(dashboardService.resumoGeral());
    }

    @Operation(summary = "Resumo por estrategia", description = "Retorna o resumo dos projetos vinculados a uma estrategia especifica. Somente LIDERANCA.")
    @GetMapping("/estrategia/{estrategiaId}")
    public ResponseEntity<List<ResumoProjetoDTO>> resumoPorEstrategia(@PathVariable String estrategiaId) {
        return ResponseEntity.ok(dashboardService.resumoPorEstrategia(estrategiaId));
    }

    @Operation(summary = "Gerar insights com IA", description = "Utiliza a Google Gemini API para gerar de 3 a 5 insights automaticos sobre os resultados dos projetos, com base nos dados agregados do dashboard. Somente LIDERANCA.")
    @GetMapping("/insights")
    public ResponseEntity<InsightsDTO> gerarInsights() {
        return ResponseEntity.ok(dashboardService.gerarInsights());
    }
}