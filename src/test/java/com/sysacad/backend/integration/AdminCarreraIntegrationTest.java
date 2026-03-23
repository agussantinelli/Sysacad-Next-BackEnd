package com.sysacad.backend.integration;

import com.sysacad.backend.dto.carrera.CarreraRequest;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.Facultad;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.FacultadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class AdminCarreraIntegrationTest extends IntegrationTestBase {

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private FacultadRepository facultadRepository;

    @Test
    @DisplayName("Admin puede crear una carrera correctamente")
    void crearCarrera_Success() throws Exception {
        // Setup: Facultad real
        Facultad facultad = new Facultad();
        facultad.setNombre("Facultad de Ingeniería");
        facultad.setSede("Sede Central");
        facultad = facultadRepository.save(facultad);

        CarreraRequest request = new CarreraRequest();
        request.setNombre("Ingeniería en Sistemas");
        request.setDuracion(5);
        request.setFacultadId(facultad.getId());

        mockMvc.perform(post("/api/admin/carreras")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verificar persistencia real
        assertTrue(carreraRepository.findAll().stream()
                .anyMatch(c -> c.getNombre().equals("Ingeniería en Sistemas")));
    }

    @Test
    @DisplayName("Crear carrera falla si el nombre está vacío")
    void crearCarrera_BadRequest() throws Exception {
        CarreraRequest request = new CarreraRequest();
        request.setNombre(""); // Inválido

        mockMvc.perform(post("/api/admin/carreras")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
