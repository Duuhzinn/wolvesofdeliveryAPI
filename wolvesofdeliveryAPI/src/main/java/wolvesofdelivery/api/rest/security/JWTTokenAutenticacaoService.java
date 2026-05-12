package wolvesofdelivery.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;

@Service
public class JWTTokenAutenticacaoService {

	private static final long EXPIRATION_TIME = 3600000;
	private static final String SECRET = "wolvesofdelivery-chave-secreta-jwt-2026";
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String HEADER_STRING = "Authorization";

	@Autowired
	private UsuarioRepository usuarioRepository;

	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

		String JWT = Jwts.builder().subject(username).expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(key).compact();

		response.addHeader(HEADER_STRING, TOKEN_PREFIX + JWT);
		response.setContentType("application/json");
		// LIBERANDO RESPOSTA PARA PORTAS DIFERENTES QUE USAM API OU CASO CLIENTES WEB
		liberacaoCors(response);
		response.getWriter().write("{\"Authorization\": \"" + TOKEN_PREFIX + JWT + "\"}");
	}

	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		String token = request.getHeader(HEADER_STRING);

		if (token != null && token.startsWith(TOKEN_PREFIX)) {

			try {
				SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

				String user = Jwts.parser().verifyWith(key).build().parseSignedClaims(token.replace(TOKEN_PREFIX, ""))
						.getPayload().getSubject();

				if (user != null) {

					Usuario usuario = usuarioRepository.findUserByLogin(user);

					if (usuario != null) {
						return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
								usuario.getAuthorities());
					}
				}

			} catch (io.jsonwebtoken.ExpiredJwtException e) {
				try {
			        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
			        response.setContentType("application/json");
			        response.setCharacterEncoding("UTF-8");
			        response.getWriter().write("{\"error\": \"Token expirado\", \"code\": \"401 ==> Unauthorized\", \"mensagem\": \"Seu token está expirado, faça o login novamente!\"}");
			        response.getWriter().flush();
				} catch (IOException e1) {

				}
				
				return null;
				
			} catch (Exception e) {
				return null;
			}
		}

		liberacaoCors(response);

		return null;
	}

	// METODO DE LIBERAÇÃO DE CORS
	public void liberacaoCors(HttpServletResponse response) {

		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}

		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}

		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");

		}
	}
}
