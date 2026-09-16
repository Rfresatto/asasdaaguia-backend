package br.com.aguiabranca.asasdaaguia.config.security;

public record TokenDTO(
        String token,
        String tipo, // "Bearer"
        long expiraEmMs
) {
}