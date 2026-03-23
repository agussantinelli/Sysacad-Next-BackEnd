package com.sysacad.backend.config;

import com.sysacad.backend.config.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import java.io.IOException;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(null, null) {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
                    throws ServletException, IOException {
                filterChain.doFilter(request, response);
            }
        };
    }

    @MockBean
    private AuthenticationProvider authenticationProvider;
}
