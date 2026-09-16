package br.com.aguiabranca.asasdaaguia.auth;

public record UsuarioResponseDTO(
        String id,
        String nome,
        String email,
        Role role
) {
    public static UsuarioResponseDTO from(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole());
    }
}