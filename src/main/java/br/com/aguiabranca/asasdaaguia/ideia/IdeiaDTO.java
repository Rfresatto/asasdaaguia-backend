package br.com.aguiabranca.asasdaaguia.ideia;

import jakarta.validation.constraints.NotBlank;

public record IdeiaDTO(
        @NotBlank String titulo,
        @NotBlank String descricao
) {
}