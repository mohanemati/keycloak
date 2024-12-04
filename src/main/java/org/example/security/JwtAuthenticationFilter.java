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
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final HandlerExceptionResolver handlerExceptionResolver;
//    private final JwtService jwtService;
//
//    @Autowired
//    public JwtAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver, JwtService jwtService) {
//        this.handlerExceptionResolver = handlerExceptionResolver;
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request, HttpServletResponse response,
//            FilterChain filterChain) throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            final String jwt = authHeader.substring(7); // remove "Bearer "
//            final String accountNationalCode = jwtService.extractUsername(jwt);
//
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            filterChain.doFilter(request, response);
//        } catch (Exception exception) {
//            handlerExceptionResolver.resolveException(request, response, null, exception);
//        }
//
//    }
//}
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7); // حذف "Bearer " از توکن

        try {
            // بررسی اعتبار توکن
            if (!validateToken(token)) {
                throw new SecurityException("Invalid token");
            }

            // تنظیم احراز هویت (اختیاری: بر اساس نیاز پروژه)
            Authentication authentication = getAuthenticationFromToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            BaseResponseDto baseResponse = new BaseResponseDto();
            baseResponse.setMessage("توکن معتبر نیست.");
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(baseResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateToken(String token) {
        // منطق بررسی اعتبار توکن (مثلاً امضا، تاریخ انقضا، و غیره)
        return token.equals("valid-token-example"); // این فقط یک مثال است
    }

    private Authentication getAuthenticationFromToken(String token) {
        // استخراج اطلاعات کاربر از توکن و ایجاد شیء Authentication
        return new UsernamePasswordAuthenticationToken("user", null, List.of());
    }
}
