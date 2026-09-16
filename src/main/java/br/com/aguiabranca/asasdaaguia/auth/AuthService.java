package br.com.aguiabranca.asasdaaguia.auth;

import br.com.aguiabranca.asasdaaguia.common.exception.AutenticacaoException;
import br.com.aguiabranca.asasdaaguia.common.exception.NegocioException;
import br.com.aguiabranca.asasdaaguia.config.security.TokenDTO;
import br.com.aguiabranca.asasdaaguia.config.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public TokenDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new AutenticacaoException("Email ou senha invalidos"));

        if (!usuario.isAtivo()) {
            throw new AutenticacaoException("Usuario inativo");
        }

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new AutenticacaoException("Email ou senha invalidos");
        }

        return tokenService.gerarToken(usuario);
    }

    public UsuarioResponseDTO registrar(RegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new NegocioException("Ja existe um usuario cadastrado com este email");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .role(dto.role())
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);
        return UsuarioResponseDTO.from(usuario);
    }
}