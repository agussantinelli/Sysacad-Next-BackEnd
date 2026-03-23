package com.sysacad.backend.integration;

import com.sysacad.backend.dto.facultad.FacultadRequest;
import com.sysacad.backend.modelo.Facultad;
import com.sysacad.backend.repository.FacultadRepository;
import org.junit.jupiter.api.DisplayName;
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
    private FacultadRepository facultadRepository;

    @Test
    @DisplayName("Admin puede crear una facultad correctamente")
    void crearFacultad_Success() throws Exception {
        FacultadRequest request = new FacultadRequest();
        request.setNombre("Facultad de Prueba");
        request.setSede("Sede Norte");

        mockMvc.perform(post("/api/admin/facultades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertTrue(facultadRepository.findAll().stream()
                .anyMatch(f -> f.getNombre().equals("Facultad de Prueba")));
    }

    @Test
    @DisplayName("Crear facultad falla si faltan datos")
    void crearFacultad_BadRequest() throws Exception {
        FacultadRequest request = new FacultadRequest();
        request.setNombre(""); // Inválido

        mockMvc.perform(post("/api/admin/facultades")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
