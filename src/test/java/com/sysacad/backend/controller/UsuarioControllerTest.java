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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener todos los usuarios")
    void obtenerTodos_Success() throws Exception {
        when(usuarioService.obtenerTodos()).thenReturn(List.of(new Usuario()));
        when(usuarioMapper.toDTO(any())).thenReturn(new UsuarioResponse());

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe cambiar estado de usuario")
    void cambiarEstado_Success() throws Exception {
        UUID id = UUID.randomUUID();
        when(usuarioService.cambiarEstado(any(), any())).thenReturn(new Usuario());
        when(usuarioMapper.toDTO(any())).thenReturn(new UsuarioResponse());

        mockMvc.perform(put("/api/usuarios/{id}/estado", id)
                        .param("nuevoEstado", "ACTIVO")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "USR123")
    @DisplayName("Usuario debe poder cambiar su password")
    void cambiarPassword_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario u = new Usuario();
        u.setLegajo("USR123");
        when(usuarioService.obtenerPorId(id)).thenReturn(Optional.of(u));

        com.sysacad.backend.dto.usuario.CambioPasswordRequest request = new com.sysacad.backend.dto.usuario.CambioPasswordRequest();
        request.setPasswordActual("1234");
        request.setPasswordNueva("5678");

        mockMvc.perform(post("/api/usuarios/{id}/cambiar-password", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor debe poder buscar por legajo")
    void obtenerPorLegajo_Success() throws Exception {
        when(usuarioService.obtenerPorLegajo(anyString())).thenReturn(Optional.of(new Usuario()));
        when(usuarioMapper.toDTO(any())).thenReturn(new UsuarioResponse());

        mockMvc.perform(get("/api/usuarios/buscar/legajo/123"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Usuario debe poder actualizar sus datos")
    void actualizarUsuario_Success() throws Exception {
        UUID id = UUID.randomUUID();
        when(usuarioService.actualizarUsuario(any(), any())).thenReturn(new Usuario());
        when(usuarioMapper.toDTO(any())).thenReturn(new UsuarioResponse());

        mockMvc.perform(put("/api/usuarios/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anónimo no puede obtener perfil (Unauthorized)")
    void obtenerPorId_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/usuarios/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener alumnos por materia")
    void obtenerAlumnosInscriptosPorMateria_Success() throws Exception {
        when(usuarioService.obtenerAlumnosInscriptosPorMateria(any())).thenReturn(List.of());
        mockMvc.perform(get("/api/usuarios/alumnos/materia/" + UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor debe obtener docentes por materia")
    void obtenerPorMateria_Success() throws Exception {
        when(usuarioService.obtenerDocentesPorMateria(any())).thenReturn(List.of());
        mockMvc.perform(get("/api/usuarios/materia/" + UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin busca usuarios por rol")
    void obtenerTodos_WithRol_Success() throws Exception {
        when(usuarioService.obtenerPorRol(any())).thenReturn(List.of());
        mockMvc.perform(get("/api/usuarios")
                        .param("rol", "ESTUDIANTE"))
                .andExpect(status().isOk());
    }
}
