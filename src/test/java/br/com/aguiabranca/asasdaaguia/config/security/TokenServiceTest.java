package br.com.aguiabranca.asasdaaguia.config.security;

import br.com.aguiabranca.asasdaaguia.auth.Role;
import br.com.aguiabranca.asasdaaguia.auth.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private final TokenService tokenService = new TokenService();

    private void configurarSegredoDeTeste() {
        ReflectionTestUtils.setField(tokenService, "secret", "chave-de-teste-com-no-minimo-256-bits-para-o-hs256-funcionar");
        ReflectionTestUtils.setField(tokenService, "expirationMs", 3600000L);
    }

    @Test
    void deveGerarEValidarTokenCorretamente() {
        configurarSegredoDeTeste();

        Usuario usuario = Usuario.builder()
                .id("123")
                .nome("Maria Operadora")
                .email("maria@aguiabranca.com")
                .role(Role.OPERADOR)
                .build();

        TokenDTO tokenGerado = tokenService.gerarToken(usuario);

        assertNotNull(tokenGerado.token());
        assertTrue(tokenService.isTokenValido(tokenGerado.token()));
        assertEquals("maria@aguiabranca.com", tokenService.getEmailDoToken(tokenGerado.token()));
        assertEquals("OPERADOR", tokenService.getRoleDoToken(tokenGerado.token()));
    }

    @Test
    void deveRejeitarTokenInvalido() {
        configurarSegredoDeTeste();
        assertFalse(tokenService.isTokenValido("token-invalido-qualquer"));
    }
}