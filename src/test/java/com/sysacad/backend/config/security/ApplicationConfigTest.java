package com.sysacad.backend.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationConfigTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Debe cargar correctamente todos los beans de seguridad")
    void securityBeans_AreLoaded() {
        assertNotNull(userDetailsService, "UserDetailsService debe estar configurado");
        assertNotNull(authenticationProvider, "AuthenticationProvider debe estar configurado");
        assertNotNull(authenticationManager, "AuthenticationManager debe estar configurado");
        assertNotNull(passwordEncoder, "PasswordEncoder debe estar configurado");
    }
}
