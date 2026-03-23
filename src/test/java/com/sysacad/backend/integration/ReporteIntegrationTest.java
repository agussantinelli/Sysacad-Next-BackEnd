package com.sysacad.backend.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ESTUDIANTE")
class ReporteIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("Estudiante puede acceder a reportes académicos básicos")
    void accederReportes_Success() throws Exception {
        mockMvc.perform(get("/api/reportes/mi-seguimiento"))
                .andExpect(status().isOk());
    }
}
