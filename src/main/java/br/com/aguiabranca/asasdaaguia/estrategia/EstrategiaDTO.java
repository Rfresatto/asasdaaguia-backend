package br.com.aguiabranca.asasdaaguia.estrategia;

import jakarta.validation.constraints.NotBlank;

public record EstrategiaDTO(
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotBlank String categoria,
        @NotBlank String campanha,
        @NotBlank String peso
) {
}