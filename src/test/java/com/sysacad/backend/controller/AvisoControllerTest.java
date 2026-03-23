package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.aviso.AvisoRequest;
import com.sysacad.backend.dto.aviso.AvisoResponse;
import com.sysacad.backend.mapper.AvisoMapper;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.AvisoService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvisoController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class AvisoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AvisoService avisoService;

    @MockBean
    private AvisoMapper avisoMapper;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder publicar un aviso")
    void publicarAviso_Admin_Success() throws Exception {
        AvisoRequest request = new AvisoRequest();
        request.setTitulo("Test");

        when(avisoMapper.toEntity(any())).thenReturn(new com.sysacad.backend.modelo.Aviso());
        when(avisoService.publicarAviso(any())).thenReturn(new com.sysacad.backend.modelo.Aviso());
        when(avisoMapper.toDTO(any())).thenReturn(new AvisoResponse());

        mockMvc.perform(post("/api/avisos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "USR001")
    @DisplayName("Usuario autenticado debe obtener sus avisos")
    void obtenerUltimosAvisos_Success() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setLegajo("USR001");

        when(usuarioRepository.findByLegajoOrMail(anyString(), anyString())).thenReturn(Optional.of(usuario));
        when(avisoService.obtenerUltimosAvisosParaUsuario(usuario.getId())).thenReturn(List.of());

        mockMvc.perform(get("/api/avisos"))
                .andExpect(status().isOk());
    }
}
