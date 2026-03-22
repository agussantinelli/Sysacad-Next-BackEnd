package com.sysacad.backend.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Debe permitir CORS desde el origen del frontend (localhost:4200)")
    void corsConfiguration_Success() throws Exception {
        mockMvc.perform(options("/api/any")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    @DisplayName("Debe prohibir CORS desde un origen no autorizado")
    void corsConfiguration_ForbiddenOrigin() throws Exception {
        mockMvc.perform(options("/api/any")
                .header("Origin", "http://evil-site.com")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Debe permitir el acceso público a /api/auth/**")
    void publicEndpoints_Auth_IsAccessible() throws Exception {
        mockMvc.perform(get("/api/auth/any"))
                .andExpect(status().isNotFound()); // 404 es aceptable (el endpoint no existe), lo importante es que NO sea 401 o 403
    }

    @Test
    @DisplayName("Debe denegar el acceso a endpoints protegidos sin token")
    void protectedEndpoints_Requirement_Auth() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden()); // SecurityConfig deniega por defecto si no hay token
    }
}
