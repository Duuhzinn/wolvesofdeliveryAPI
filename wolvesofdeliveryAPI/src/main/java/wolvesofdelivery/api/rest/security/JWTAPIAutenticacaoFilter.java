package wolvesofdelivery.api.rest.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAPIAutenticacaoFilter extends OncePerRequestFilter {

    private final JWTTokenAutenticacaoService jwtTokenAutenticacaoService;

    public JWTAPIAutenticacaoFilter(JWTTokenAutenticacaoService jwtTokenAutenticacaoService) {
        this.jwtTokenAutenticacaoService = jwtTokenAutenticacaoService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/wolvesofdeliveryAPI/login") || path.equals("/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        System.out.println("TOKEN HEADER: " + request.getHeader("Authorization"));
        System.out.println("URI: " + request.getRequestURI());

        Authentication authentication =
                jwtTokenAutenticacaoService.getAuthentication(request);

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}