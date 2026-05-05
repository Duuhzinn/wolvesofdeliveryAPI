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
        boolean skip = path.equals("/wolvesofdeliveryAPI/login"); 
        return skip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        Authentication authentication =
                jwtTokenAutenticacaoService.getAuthentication(request);

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}