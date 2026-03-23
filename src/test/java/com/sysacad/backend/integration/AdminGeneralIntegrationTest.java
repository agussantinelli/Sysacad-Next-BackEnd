package com.sysacad.backend.integration;

import com.sysacad.backend.dto.carrera.CarreraResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class AdminGeneralIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("Admin puede obtener la lista de carreras")
    void obtenerCarreras_Success() throws Exception {
        mockMvc.perform(get("/api/admin/general/carreras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Usuario sin rol ADMIN no puede acceder a carreras del admin")
    @WithMockUser(roles = "ESTUDIANTE")
    void obtenerCarreras_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/general/carreras"))
                .andExpect(status().isForbidden());
    }
}
