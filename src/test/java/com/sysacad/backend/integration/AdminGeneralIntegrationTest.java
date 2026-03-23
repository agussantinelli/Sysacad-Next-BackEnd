package com.sysacad.backend.integration;

import com.sysacad.backend.dto.admin.AdminGeneralResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class AdminGeneralIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("Admin puede obtener estadísticas generales")
    void obtenerEstadisticas_Success() throws Exception {
        mockMvc.perform(get("/api/admin/general/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadEstudiantes").exists())
                .andExpect(jsonPath("$.cantidadCarreras").exists());
    }

    @Test
    @DisplayName("Usuario sin rol ADMIN no puede acceder a estadísticas")
    @WithMockUser(roles = "ESTUDIANTE")
    void obtenerEstadisticas_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/general/estadisticas"))
                .andExpect(status().isForbidden());
    }
}
