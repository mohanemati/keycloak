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
        // Ignore filter for the /get-token endpoint
        if (uri.equals("/api/user/get-token")) {
            System.out.println("Bypassing token validation for get-token endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        // Get the Authorization header
        String token = request.getHeader("Authorization");
        System.out.println("Token received: " + token); // Log token to see if it's being sent

        // If the token is missing or invalid, respond with 401
        if (token == null || !isValidToken(token)) {
            System.out.println("Unauthorized request - Token is missing or invalid.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized: Token is missing or invalid.");
            return;
        }

        // Continue with the filter chain if the token is valid
        filterChain.doFilter(request, response);
    }

    private boolean isValidToken(String token) {
        try {
            // Remove "Bearer " prefix if exists
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            System.out.println("Validating token: " + token); // Log token for validation

            // Verify the token using Keycloak's TokenVerifier
            AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();

            // Check if the token has expired
            if (accessToken.isExpired()) {
                System.out.println("Token is expired");
                return false;
            }

            // If token is valid, print the username
            System.out.println("Token is valid: " + accessToken.getPreferredUsername());
            return true;

        } catch (VerificationException e) {
            // If verification fails, print the error and return false
            System.out.println("Invalid token exception: " + e.getMessage());
            return false;
        }
    }
}
