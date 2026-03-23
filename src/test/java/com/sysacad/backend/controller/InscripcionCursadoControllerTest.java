package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoRequest;
import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoResponse;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.InscripcionCursadoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InscripcionCursadoController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class InscripcionCursadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InscripcionCursadoService inscripcionCursadoService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(username = "ALU001", roles = "ESTUDIANTE")
    @DisplayName("Estudiante debe poder inscribirse a una cursada")
    void inscribir_Estudiante_Success() throws Exception {
        InscripcionCursadoRequest request = new InscripcionCursadoRequest();
        request.setIdComision(UUID.randomUUID());
        request.setIdMateria(UUID.randomUUID());

        Usuario alumno = new Usuario();
        alumno.setId(UUID.randomUUID());
        alumno.setLegajo("ALU001");

        InscripcionCursadoResponse response = new InscripcionCursadoResponse();
        response.setId(UUID.randomUUID());

        when(usuarioRepository.findByLegajoOrMail(anyString(), anyString())).thenReturn(Optional.of(alumno));
        when(inscripcionCursadoService.inscribir(any())).thenReturn(response);

        mockMvc.perform(post("/api/inscripciones-cursado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "ALU001", roles = "ESTUDIANTE")
    @DisplayName("Estudiante debe poder ver sus cursadas")
    void getMisCursadas_Success() throws Exception {
        Usuario alumno = new Usuario();
        alumno.setId(UUID.randomUUID());
        alumno.setLegajo("ALU001");

        when(usuarioRepository.findByLegajoOrMail(anyString(), anyString())).thenReturn(Optional.of(alumno));
        when(inscripcionCursadoService.obtenerHistorial(alumno.getId())).thenReturn(List.of(new InscripcionCursadoResponse()));

        mockMvc.perform(get("/api/inscripciones-cursado/mis-cursadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor debe poder cargar notas")
    void cargarNota_Profesor_Success() throws Exception {
        UUID inscripcionId = UUID.randomUUID();
        com.sysacad.backend.dto.calificacion_cursada.CalificacionCursadaRequest request = new com.sysacad.backend.dto.calificacion_cursada.CalificacionCursadaRequest();
        
        when(inscripcionCursadoService.cargarNota(any(), any())).thenReturn(new com.sysacad.backend.dto.calificacion_cursada.CalificacionCursadaResponse());

        mockMvc.perform(post("/api/inscripciones-cursado/{id}/notas", inscripcionId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
