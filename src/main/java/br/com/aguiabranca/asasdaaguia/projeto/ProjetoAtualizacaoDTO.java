package br.com.aguiabranca.asasdaaguia.projeto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjetoAtualizacaoDTO(
        @NotNull ProjetoModel.EtapaProjeto etapa,
        @PositiveOrZero BigDecimal retornoFinanceiro,
        @PositiveOrZero BigDecimal produtividadeGanhaPercentual,
        LocalDate prazo
) {
}