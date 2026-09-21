package br.com.aguiabranca.asasdaaguia.auth;

public record UsuarioLogadoDTO(
        String nome,
        String email,
        Role role,
        int xp
) {
    public static UsuarioLogadoDTO from(Usuario usuario) {
        return new UsuarioLogadoDTO(usuario.getNome(), usuario.getEmail(), usuario.getRole(), usuario.getXp());
    }
}