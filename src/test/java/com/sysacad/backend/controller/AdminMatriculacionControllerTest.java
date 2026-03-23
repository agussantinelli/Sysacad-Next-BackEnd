package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.admin.MatriculacionRequest;
import com.sysacad.backend.service.AdminMatriculacionService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminMatriculacionController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class AdminMatriculacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminMatriculacionService matriculacionService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder buscar usuarios para matriculación")
    void buscarUsuarios_Success() throws Exception {
        when(matriculacionService.buscarUsuariosPorLegajo(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/matriculacion/usuarios/buscar")
                .param("legajo", "777"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder crear una matriculación")
    void crearMatriculacion_Success() throws Exception {
        MatriculacionRequest request = new MatriculacionRequest();
        
        mockMvc.perform(post("/api/admin/matriculacion")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener facultades")
    void obtenerFacultades_Success() throws Exception {
        when(matriculacionService.obtenerTodasFacultades()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/matriculacion/facultades"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener carreras por facultad")
    void obtenerCarreras_Success() throws Exception {
        UUID facultadId = UUID.randomUUID();
        when(matriculacionService.obtenerCarrerasPorFacultad(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/matriculacion/carreras")
                        .param("facultadId", facultadId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener planes por carrera")
    void obtenerPlanes_Success() throws Exception {
        UUID carreraId = UUID.randomUUID();
        when(matriculacionService.obtenerPlanesPorCarrera(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/matriculacion/planes")
                        .param("carreraId", carreraId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede crear matriculación (Forbidden)")
    void crearMatriculacion_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(post("/api/admin/matriculacion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede buscar usuarios de admin (Forbidden)")
    void buscarUsuarios_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(get("/api/admin/matriculacion/usuarios/buscar")
                        .param("legajo", "777"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anónimo no puede crear matriculaciones (Unauthorized)")
    void crearMatriculacion_Unauthorized_AsAnonymous() throws Exception {
        mockMvc.perform(post("/api/admin/matriculacion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("obtenerCarreras devuelve error si no se pasa facultadId")
    void obtenerCarreras_BadRequest() throws Exception {
        mockMvc.perform(get("/api/admin/matriculacion/carreras"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("obtenerPlanes devuelve error si no se pasa carreraId")
    void obtenerPlanes_BadRequest() throws Exception {
        mockMvc.perform(get("/api/admin/matriculacion/planes"))
                .andExpect(status().isBadRequest());
    }
}
