package br.com.aguiabranca.asasdaaguia.dashboard;

import br.com.aguiabranca.asasdaaguia.projeto.ProjetoModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResumoProjetoDTO(
        String id,
        String nome,
        String estrategiaId,
        ProjetoModel.EtapaProjeto etapa,
        BigDecimal investimento,
        BigDecimal retornoFinanceiro,
        BigDecimal lucro,
        BigDecimal roi,
        BigDecimal produtividadeGanhaPercentual,
        LocalDate prazo
) {
}