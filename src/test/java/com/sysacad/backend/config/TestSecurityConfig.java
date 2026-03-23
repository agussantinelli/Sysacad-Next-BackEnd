package com.sysacad.backend.config;

import com.sysacad.backend.config.security.JwtAuthenticationFilter;
import com.sysacad.backend.config.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.io.IOException;

@TestConfiguration
@EnableMethodSecurity
public class TestSecurityConfig {

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

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
