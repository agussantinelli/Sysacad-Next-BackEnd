package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.CertificadoService;
import com.sysacad.backend.service.ProfesorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfesorController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class ProfesorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfesorService profesorService;

    @MockBean
    private CertificadoService certificadoService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener sus materias asignadas")
    void getMisMaterias_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        profesor.setLegajo("PROF001");

        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerMateriasAsignadas(profesor.getId())).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores/mis-materias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener sus comisiones")
    void getMisComisiones_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        profesor.setLegajo("PROF001");

        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerTodasLasComisiones(profesor.getId())).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores/mis-comisiones"))
                .andExpect(status().isOk());
    }
}
