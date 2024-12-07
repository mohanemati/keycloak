package org.example.configuration;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.keycloak.TokenVerifier;

public class CustomTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (uri.equals("/api/user/get-token")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
//        System.out.println("Token received: " + token);

        if (token == null || !isValidToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized: Token is missing or invalid.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        try {

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }


            AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();


            if (accessToken.isExpired()) {
                System.out.println("Token is expired");
                return false;
            }

            System.out.println("Token is valid: " + accessToken.getPreferredUsername());
            return true;

        } catch (VerificationException e) {
            System.out.println("Invalid token: " + e.getMessage());
            return false;
        }
    }
}
