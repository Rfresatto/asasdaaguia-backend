package br.com.aguiabranca.asasdaaguia.auth;

import br.com.aguiabranca.asasdaaguia.common.exception.AutenticacaoException;
import br.com.aguiabranca.asasdaaguia.common.exception.NegocioException;
import br.com.aguiabranca.asasdaaguia.config.security.TokenDTO;
import br.com.aguiabranca.asasdaaguia.config.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioAtivo;

    @BeforeEach
    void setUp() {
        usuarioAtivo = Usuario.builder()
                .id("1")
                .nome("Joao Operador")
                .email("joao@aguiabranca.com")
                .senha("hash-da-senha")
                .role(Role.OPERADOR)
                .ativo(true)
                .build();
    }

    @Test
    void deveLogarComSucessoQuandoCredenciaisValidas() {
        LoginDTO dto = new LoginDTO("joao@aguiabranca.com", "123456");
        TokenDTO tokenEsperado = new TokenDTO("token-fake", "Bearer", 86400000L);

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.of(usuarioAtivo));
        when(passwordEncoder.matches(dto.senha(), usuarioAtivo.getSenha())).thenReturn(true);
        when(tokenService.gerarToken(usuarioAtivo)).thenReturn(tokenEsperado);

        TokenDTO resultado = authService.login(dto);

        assertEquals(tokenEsperado, resultado);
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoExiste() {
        LoginDTO dto = new LoginDTO("naoexiste@aguiabranca.com", "123456");
        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        assertThrows(AutenticacaoException.class, () -> authService.login(dto));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        LoginDTO dto = new LoginDTO("joao@aguiabranca.com", "senha-errada");
        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.of(usuarioAtivo));
        when(passwordEncoder.matches(dto.senha(), usuarioAtivo.getSenha())).thenReturn(false);

        assertThrows(AutenticacaoException.class, () -> authService.login(dto));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioInativo() {
        usuarioAtivo.setAtivo(false);
        LoginDTO dto = new LoginDTO("joao@aguiabranca.com", "123456");
        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.of(usuarioAtivo));

        assertThrows(AutenticacaoException.class, () -> authService.login(dto));
    }

    @Test
    void deveRegistrarUsuarioComSucesso() {
        RegistroDTO dto = new RegistroDTO("Carla Gestora", "carla@aguiabranca.com", "123456", Role.GESTOR);

        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("hash-gerado");

        UsuarioResponseDTO resultado = authService.registrar(dto);

        assertEquals(dto.nome(), resultado.nome());
        assertEquals(dto.email(), resultado.email());
        assertEquals(dto.role(), resultado.role());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoAoRegistrarEmailJaExistente() {
        RegistroDTO dto = new RegistroDTO("Carla Gestora", "carla@aguiabranca.com", "123456", Role.GESTOR);
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(NegocioException.class, () -> authService.registrar(dto));
        verify(usuarioRepository, never()).save(any());
    }
}