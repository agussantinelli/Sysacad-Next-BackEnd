package com.sysacad.backend.integration;

import com.sysacad.backend.modelo.Aviso;
import com.sysacad.backend.repository.AvisoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ESTUDIANTE")
class AvisoIntegrationTest extends IntegrationTestBase {

    @Autowired
    private AvisoRepository avisoRepository;

    @Test
    @DisplayName("Estudiante puede ver avisos del sistema")
    void listarAvisos_Success() throws Exception {
        Aviso aviso = new Aviso();
        aviso.setTitulo("Mantenimiento");
        aviso.setContenido("El sistema estará fuera de línea");
        aviso.setFechaPublicacion(LocalDateTime.now());
        avisoRepository.save(aviso);

        mockMvc.perform(get("/api/avisos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
