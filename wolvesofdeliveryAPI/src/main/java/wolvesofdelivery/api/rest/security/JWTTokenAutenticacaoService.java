package wolvesofdelivery.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;

@Service
public class JWTTokenAutenticacaoService {

    private static final long EXPIRATION_TIME = 172800000;
    private static final String SECRET = "wolvesofdelivery-chave-secreta-jwt-2026";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void addAuthentication(HttpServletResponse response, String username) throws IOException {

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

        String JWT = Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();

        response.addHeader(HEADER_STRING, TOKEN_PREFIX + JWT);

        response.setContentType("application/json");
        response.getWriter().write("{\"Authorization\": \"" + TOKEN_PREFIX + JWT + "\"}");
    }

    public Authentication getAuthentication(HttpServletRequest request) {

        String token = request.getHeader(HEADER_STRING);

        if (token != null && token.startsWith(TOKEN_PREFIX)) {

            try {
                SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

                String user = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token.replace(TOKEN_PREFIX, ""))
                        .getPayload()
                        .getSubject();

                if (user != null) {

                    Usuario usuario = usuarioRepository.findUserByLogin(user);

                    if (usuario != null) {
                        return new UsernamePasswordAuthenticationToken(
                                usuario.getLogin(),
                                null,
                                usuario.getAuthorities()
                        );
                    }
                }

            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }
}
