package org.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.BaseResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " from the token

        try {
            // Validate the token using a service (you should implement the validation logic in JwtService)
            if (!jwtService.isTokenValid(token)) {
                throw new SecurityException("Invalid token");
            }

            // Get the authentication object from the token and set it in the context
            Authentication authentication = getAuthenticationFromToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            BaseResponseDto baseResponse = new BaseResponseDto();
            baseResponse.setMessage("Invalid token");
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(baseResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthenticationFromToken(String token) {
        // Extract user details from the token (e.g., username, roles, etc.)
        String username = jwtService.extractUsername(token); // This should be implemented in JwtService

        // Return a valid Authentication object with user details
        return new UsernamePasswordAuthenticationToken(username, null, List.of());
    }
}
