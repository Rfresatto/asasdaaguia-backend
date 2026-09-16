package br.com.aguiabranca.asasdaaguia.config.security;

import br.com.aguiabranca.asasdaaguia.auth.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public TokenDTO gerarToken(Usuario usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMs);

        String token = Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .claim("role", usuario.getRole().name())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getKey())
                .compact();

        return new TokenDTO(token, "Bearer", expirationMs);
    }

    public String getEmailDoToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleDoToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean isTokenValido(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}