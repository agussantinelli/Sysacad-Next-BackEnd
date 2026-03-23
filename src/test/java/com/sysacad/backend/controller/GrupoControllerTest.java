package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.grupo.GrupoRequest;
import com.sysacad.backend.dto.grupo.GrupoResponse;
import com.sysacad.backend.dto.grupo.MensajeGrupoRequest;
import com.sysacad.backend.dto.grupo.MensajeGrupoResponse;
import com.sysacad.backend.mapper.GrupoMapper;
import com.sysacad.backend.mapper.MensajeGrupoMapper;
import com.sysacad.backend.modelo.Grupo;
import com.sysacad.backend.modelo.MensajeGrupo;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.service.GrupoService;
import com.sysacad.backend.service.UsuarioService;
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

@WebMvcTest(GrupoController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class GrupoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GrupoService grupoService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private GrupoMapper grupoMapper;

    @MockBean
    private MensajeGrupoMapper mensajeGrupoMapper;

    @Test
    @WithMockUser(username = "USR001")
    @DisplayName("Debe listar mis grupos")
    void listarMisGrupos_Success() throws Exception {
        Usuario mockUser = new Usuario();
        mockUser.setId(UUID.randomUUID());
        mockUser.setLegajo("USR001");

        when(usuarioService.obtenerPorLegajo(anyString())).thenReturn(Optional.of(mockUser));
        when(grupoService.obtenerMisGrupos(mockUser.getId())).thenReturn(List.of(new GrupoResponse()));

        mockMvc.perform(get("/api/grupos/mis-grupos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "USR001")
    @DisplayName("Debe poder enviar un mensaje al grupo")
    void enviarMensaje_Success() throws Exception {
        UUID grupoId = UUID.randomUUID();
        MensajeGrupoRequest request = new MensajeGrupoRequest();
        request.setContenido("Hola");

        Usuario mockUser = new Usuario();
        mockUser.setId(UUID.randomUUID());
        mockUser.setLegajo("USR001");

        when(usuarioService.obtenerPorLegajo(anyString())).thenReturn(Optional.of(mockUser));
        when(grupoService.enviarMensaje(any(), any(), any())).thenReturn(new MensajeGrupo());
        when(mensajeGrupoMapper.toDTO(any())).thenReturn(new MensajeGrupoResponse());

        mockMvc.perform(post("/api/grupos/{id}/mensajes", grupoId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
