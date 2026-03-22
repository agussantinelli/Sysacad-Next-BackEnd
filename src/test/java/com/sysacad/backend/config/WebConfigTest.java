package com.sysacad.backend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Debe permitir el acceso público a /uploads/** (Verificación de Handler)")
    void uploadsPath_IsAccessible() throws Exception {
        // El test verifica que el path existe en la configuración. 
        // No necesitamos que el archivo exista realmente para verificar el status (404 es aceptable si el handler lo intercepta)
        // Pero lo más importante es que NO devuelva 403 (Forbidden) o 401 (Unauthorized)
        mockMvc.perform(get("/uploads/test-file.txt"))
                .andExpect(status().isNotFound()); // NotFound significa que el handler lo buscó pero no lo halló.
    }
}
