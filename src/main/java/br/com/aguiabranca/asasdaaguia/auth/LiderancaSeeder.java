package br.com.aguiabranca.asasdaaguia.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LiderancaSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        boolean jaExisteLideranca = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.LIDERANCA);

        if (jaExisteLideranca) {
            return;
        }

        Usuario admin = Usuario.builder()
                .nome("Admin Lideranca")
                .email("lideranca@aguiabranca.com")
                .senha(passwordEncoder.encode("senha123"))
                .role(Role.LIDERANCA)
                .ativo(true)
                .build();

        usuarioRepository.save(admin);
        System.out.println(">> Usuario LIDERANCA inicial criado: lideranca@aguiabranca.com / senha123");
    }
}