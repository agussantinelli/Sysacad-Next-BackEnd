package com.sysacad.backend.integration;

import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.ComisionRepository;
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
class AdminComisionIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ComisionRepository comisionRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Test
    @DisplayName("Admin puede crear una comisión")
    void crearComision_Success() throws Exception {
        comisionRepository.deleteAll();
        Carrera carrera = new Carrera();
        carrera.setNombre("Sistemas");
        carrera.setAlias("SIS");
        carrera = carreraRepository.save(carrera);

        ComisionRequest request = new ComisionRequest();
        request.setNombre("1K1");
        request.setAnio(2024);
        request.setTurno("MAÑANA");
        request.setNivel(1);
        request.setIdCarrera(carrera.getId());

        mockMvc.perform(post("/api/admin/comisiones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertTrue(comisionRepository.findAll().stream()
                .anyMatch(c -> c.getNombre().equals("1K1")));
    }
}
