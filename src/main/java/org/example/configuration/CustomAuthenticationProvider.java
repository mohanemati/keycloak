package org.example.configuration;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class CustomAuthenticationProvider implements AuthenticationProvider {

    PasswordEncoder passwordEncoder;

//    @Autowired
//    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) {
        if (authentication.getPrincipal() == "")
            throw new Exception("");
        String username = (String) authentication.getPrincipal();
        String password = authentication.getCredentials().toString();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new UsernamePasswordAuthenticationToken(username, passwordEncoder.encode(password), authentication.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> auth) {
        return auth.equals(UsernamePasswordAuthenticationToken.class);
    }
}

