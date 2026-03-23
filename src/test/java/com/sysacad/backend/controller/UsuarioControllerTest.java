package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.usuario.UsuarioRequest;
import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.mapper.UsuarioMapper;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.service.MatriculacionService;
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

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private MatriculacionService matriculacionService;

    @MockBean
    private UsuarioMapper usuarioMapper;



    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder crear un usuario")
    void crearUsuario_Admin_Success() throws Exception {
        UsuarioRequest request = new UsuarioRequest();
        request.setLegajo("NEW001");
        request.setNombre("Nuevo");
        request.setApellido("Usuario");
        request.setRol(RolUsuario.ESTUDIANTE);

        Usuario stubUsuario = new Usuario();
        stubUsuario.setId(UUID.randomUUID());
        stubUsuario.setLegajo("NEW001");
        stubUsuario.setRol(RolUsuario.ESTUDIANTE);

        UsuarioResponse responseDto = new UsuarioResponse();
        responseDto.setId(stubUsuario.getId());
        responseDto.setLegajo("NEW001");

        when(usuarioMapper.toEntity(any())).thenReturn(stubUsuario);
        when(usuarioService.registrarUsuario(any())).thenReturn(stubUsuario);
        when(usuarioMapper.toDTO(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.legajo").value("NEW001"));
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante NO debe poder crear un usuario")
    void crearUsuario_Estudiante_Forbidden() throws Exception {
        UsuarioRequest request = new UsuarioRequest();
        
        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("Usuario autenticado debe poder obtener su perfil")
    void obtenerPorId_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setLegajo("USR123");
        usuario.setRol(RolUsuario.ESTUDIANTE);

        UsuarioResponse responseDto = new UsuarioResponse();
        responseDto.setId(id);
        responseDto.setLegajo("USR123");

        when(usuarioService.obtenerPorId(id)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(responseDto);

        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder eliminar un usuario")
    void eliminarUsuario_Admin_Success() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/usuarios/{id}", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
