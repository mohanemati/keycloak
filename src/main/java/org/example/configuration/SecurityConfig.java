package org.example.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.BaseResponseDto;
import org.example.security.JwtAuthenticationFilter;
import org.example.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;


    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtService jwtService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(authorize -> authorize
                        .requestMatchers("/api/user/get-token").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {

                            BaseResponseDto baseResponse = new BaseResponseDto();
                            baseResponse.setMessage("شما احراز هویت نشده‌اید یا توکن معتبر نیست.");


                            ObjectMapper mapper = new ObjectMapper();
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(mapper.writeValueAsString(baseResponse));
                        })
                );

        return http.build();
    }
}












//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(
//                        AbstractHttpConfigurer::disable
//                )
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/user/get-token").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(
//                        a -> a.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/user/get-token").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(Customizer.withDefaults())
//                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler
//                .accessDeniedHandler((request, response, accessDeniedException) -> { )
//                    BaseResponseDto baseResponse = new BaseResponseDto(); baseResponse.setMessage("شما دسترسی ندارید.");
//                    ObjectMapper mapper = new ObjectMapper(); response.setStatus(HttpStatus.FORBIDDEN.value());
//                    response.setContentType(MediaType.APPLICATION_JSON_VALUE); response.setCharacterEncoding("UTF-8");
//                    response.getWriter().write(mapper.writeValueAsString(baseResponse)); });
//
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable) // غیرفعال‌سازی CSRF
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/user/get-token").permitAll() // مسیر بدون نیاز به احراز هویت
//                        .anyRequest().authenticated() // سایر مسیرها نیاز به احراز هویت
//                )
//                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // اضافه کردن فیلتر
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            BaseResponseDto baseResponse = new BaseResponseDto();
//                            baseResponse.setMessage("شما احراز هویت نشده‌اید یا توکن معتبر نیست.");
//                            ObjectMapper mapper = new ObjectMapper();
//                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                            response.setCharacterEncoding("UTF-8");
//                            response.getWriter().write(mapper.writeValueAsString(baseResponse));
//                        })
//                );
//
//        return http.build();
//    }