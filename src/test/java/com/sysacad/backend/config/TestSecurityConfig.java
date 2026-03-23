package com.sysacad.backend.config;

import com.sysacad.backend.config.security.JwtAuthenticationFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;

@TestConfiguration
public class TestSecurityConfig {

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthenticationProvider authenticationProvider;
}
