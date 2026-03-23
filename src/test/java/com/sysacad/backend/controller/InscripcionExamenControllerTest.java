package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenRequest;
import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenResponse;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.InscripcionExamenService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InscripcionExamenController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class InscripcionExamenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InscripcionExamenService inscripcionExamenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(username = "ALU001")
    @DisplayName("Debe inscribir a examen exitosamente")
    void inscribir_Success() throws Exception {
        InscripcionExamenRequest request = new InscripcionExamenRequest();
        request.setIdDetalleMesa(UUID.randomUUID());
        
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setLegajo("ALU001");

        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(usuario));
        when(inscripcionExamenService.inscribirAlumno(any())).thenReturn(new InscripcionExamenResponse());

        mockMvc.perform(post("/api/inscripciones-examen")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ALU001")
    @DisplayName("Debe listar inscripciones a examen del usuario")
    void getMisInscripciones_Success() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setLegajo("ALU001");

        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(usuario));
        when(inscripcionExamenService.getInscripcionesByAlumno(usuario.getId())).thenReturn(List.of(new InscripcionExamenResponse()));

        mockMvc.perform(get("/api/inscripciones-examen/mis-inscripciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("Debe dar de baja una inscripción")
    void darDeBaja_Success() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/inscripciones-examen/{id}", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
