package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.MesaExamenService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MesaExamenController.class)
@ActiveProfiles("test")
class MesaExamenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MesaExamenService mesaExamenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(username = "ALU001")
    @DisplayName("Debe listar mesas de examen disponibles para el alumno")
    void getExamenesDisponibles_Success() throws Exception {
        Usuario alumno = new Usuario();
        alumno.setId(UUID.randomUUID());
        alumno.setLegajo("ALU001");

        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(alumno));
        when(mesaExamenService.getExamenesDisponibles(alumno.getId())).thenReturn(List.of());

        mockMvc.perform(get("/api/mesas/disponibles"))
                .andExpect(status().isOk());
    }
}
