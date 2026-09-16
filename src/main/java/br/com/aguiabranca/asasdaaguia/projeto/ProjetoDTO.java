package br.com.aguiabranca.asasdaaguia.projeto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjetoDTO(
        @NotBlank String nome,
        @NotBlank String descricao,
        String ideiaOrigemId, // opcional
        @NotNull @PositiveOrZero BigDecimal investimento,
        @NotNull LocalDate prazo
) {
}