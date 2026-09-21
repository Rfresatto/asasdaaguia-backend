package br.com.aguiabranca.asasdaaguia.auth;

import br.com.aguiabranca.asasdaaguia.config.security.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Autentica um usuario e retorna um token JWT valido por 24 horas.")
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @Operation(summary = "Registrar usuario", description = "Cadastra um novo usuario (Operador, Gestor ou Lideranca). Somente usuarios com role LIDERANCA podem executar esta acao.")
    @PostMapping("/registro")
    @PreAuthorize("hasRole('LIDERANCA')")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistroDTO dto) {
        UsuarioResponseDTO criado = authService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(summary = "Buscar dados do usuario logado", description = "Retorna nome, email, role e XP atual do usuario autenticado, extraido do token JWT.")
    @GetMapping("/me")
    public ResponseEntity<UsuarioLogadoDTO> buscarUsuarioLogado(Authentication authentication) {
        return ResponseEntity.ok(authService.buscarUsuarioLogado(authentication.getName()));
    }
}