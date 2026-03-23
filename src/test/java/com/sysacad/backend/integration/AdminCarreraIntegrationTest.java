package com.sysacad.backend.integration;

import com.sysacad.backend.dto.carrera.CarreraRequest;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.FacultadRegionalRepository;
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
class AdminCarreraIntegrationTest extends IntegrationTestBase {

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private FacultadRegionalRepository facultadRegionalRepository;

    @Test
    @DisplayName("Admin puede crear una carrera correctamente")
    void crearCarrera_Success() throws Exception {
        // Setup: Facultad real
        FacultadRegional facultad = new FacultadRegional();
        facultad.setCiudad("Tucumán");
        facultad.setProvincia("Tucumán");
        facultad = facultadRegionalRepository.save(facultad);

        CarreraRequest request = new CarreraRequest();
        request.setNombre("Ingeniería en Sistemas");
        request.setAlias("ISI");
        request.setIdFacultad(facultad.getId());

        mockMvc.perform(post("/api/admin/carreras")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verificar persistencia real
        assertTrue(carreraRepository.findAll().stream()
                .anyMatch(c -> c.getNombre().equals("Ingeniería en Sistemas")));
    }
}
