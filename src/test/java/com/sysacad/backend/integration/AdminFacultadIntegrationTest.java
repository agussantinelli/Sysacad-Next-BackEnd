package com.sysacad.backend.integration;

import com.sysacad.backend.dto.facultad.FacultadRequest;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import org.junit.jupiter.api.DisplayName;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class AdminFacultadIntegrationTest extends IntegrationTestBase {

    @Autowired
    private FacultadRegionalRepository facultadRegionalRepository;

    @Test
    @DisplayName("Admin puede crear una facultad correctamente")
    void crearFacultad_Success() throws Exception {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);
        FacultadRequest request = new FacultadRequest();
        request.setCiudad("Tucumán_" + uniqueSuffix);
        request.setProvincia("Tucumán");

        mockMvc.perform(post("/api/admin/facultades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertTrue(facultadRegionalRepository.findAll().stream()
                .anyMatch(f -> f.getCiudad().equals("Tucumán_" + uniqueSuffix)));
    }

    @Test
    @DisplayName("Crear facultad falla si faltan datos")
    void crearFacultad_BadRequest() throws Exception {
        FacultadRequest request = new FacultadRequest();
        request.setCiudad(""); // Inválido

        mockMvc.perform(post("/api/admin/facultades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
