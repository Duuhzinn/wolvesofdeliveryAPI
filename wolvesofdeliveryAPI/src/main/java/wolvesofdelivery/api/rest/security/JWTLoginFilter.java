package wolvesofdelivery.api.rest.security;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import wolvesofdelivery.api.rest.model.Usuario;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final JWTTokenAutenticacaoService jwtTokenAutenticacaoService;

    public JWTLoginFilter(AuthenticationManager authenticationManager,
            JWTTokenAutenticacaoService jwtTokenAutenticacaoService) {
        super("/login"); // ✅ substitui o AntPathRequestMatcher("/login")
        setAuthenticationManager(authenticationManager);
        this.jwtTokenAutenticacaoService = jwtTokenAutenticacaoService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Usuario usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);

        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authResult) throws IOException, ServletException {

        System.out.println("=== LOGIN BEM SUCEDIDO ===");
        System.out.println("Usuario: " + authResult.getName());

        String username = authResult.getName();
        try {
            jwtTokenAutenticacaoService.addAuthentication(response, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}