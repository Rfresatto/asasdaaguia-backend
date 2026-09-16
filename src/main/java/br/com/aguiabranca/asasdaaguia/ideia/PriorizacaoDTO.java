package br.com.aguiabranca.asasdaaguia.ideia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PriorizacaoDTO(
        @NotNull @Positive Integer prioridade
) {
}