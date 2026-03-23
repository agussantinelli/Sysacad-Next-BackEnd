package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.admin.AdminEstadisticasDTO;
import com.sysacad.backend.dto.admin.AdminInscripcionDTO;
import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.mapper.UsuarioMapper;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.service.AdminService;
import com.sysacad.backend.service.UsuarioService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioMapper usuarioMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener un usuario por ID")
    void obtenerUsuario_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(id);
        
        UsuarioResponse response = new UsuarioResponse();
        response.setId(id);

        when(usuarioService.obtenerPorId(id)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDTO(usuario)).thenReturn(response);

        mockMvc.perform(get("/api/admin/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener todas las inscripciones")
    void obtenerTodasInscripciones_Success() throws Exception {
        when(adminService.obtenerTodasInscripciones()).thenReturn(List.of(new AdminInscripcionDTO()));

        mockMvc.perform(get("/api/admin/inscripciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener estadísticas globales")
    void obtenerEstadisticas_Success() throws Exception {
        when(adminService.obtenerEstadisticas(any(), any(), any())).thenReturn(new AdminEstadisticasDTO());

        mockMvc.perform(get("/api/admin/estadisticas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder eliminar una inscripción")
    void eliminarInscripcion_Success() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/admin/inscripciones/{id}", id)
                .param("tipo", "CURSADA")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
