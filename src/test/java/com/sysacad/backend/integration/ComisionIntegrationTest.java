package com.sysacad.backend.integration;

import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.repository.ComisionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ESTUDIANTE")
class ComisionIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ComisionRepository comisionRepository;

    @Test
    @DisplayName("Estudiante puede listar comisiones disponibles")
    void listarComisiones_Success() throws Exception {
        Comision comision = new Comision();
        comision.setNombre("Sistemas 1K1");
        comision.setAnio(2024);
        comision.setTurno("MAÑANA");
        comision.setNivel(1);
        comisionRepository.save(comision);

        mockMvc.perform(get("/api/comisiones")
                        .param("anio", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
