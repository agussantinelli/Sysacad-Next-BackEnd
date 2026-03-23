package com.sysacad.backend.controller;

import com.sysacad.backend.service.CertificadoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReporteController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificadoService certificadoService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe obtener historial de certificados")
    void obtenerHistorialCertificados_Success() throws Exception {
        when(certificadoService.obtenerHistorialDescargas()).thenReturn(List.of());

        mockMvc.perform(get("/api/reportes/certificados"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede ver historial de reportes")
    void obtenerHistorialCertificados_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(get("/api/reportes/certificados"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede ver historial de reportes")
    void obtenerHistorialCertificados_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(get("/api/reportes/certificados"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anónimo no puede ver historial de reportes")
    void obtenerHistorialCertificados_Unauthorized_AsAnonymous() throws Exception {
        mockMvc.perform(get("/api/reportes/certificados"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin obtiene lista vacía de historial")
    void obtenerHistorialCertificados_EmptyList() throws Exception {
        when(certificadoService.obtenerHistorialDescargas()).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/reportes/certificados"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Fallo interno en servicio de reportes")
    void obtenerHistorialCertificados_InternalError() throws Exception {
        when(certificadoService.obtenerHistorialDescargas()).thenThrow(new RuntimeException("DB fail"));

        mockMvc.perform(get("/api/reportes/certificados"))
                .andExpect(status().isInternalServerError());
    }
}
