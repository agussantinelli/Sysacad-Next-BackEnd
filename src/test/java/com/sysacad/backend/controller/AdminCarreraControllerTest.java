package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.admin.CarreraAdminDTO;
import com.sysacad.backend.dto.carrera.CarreraRequest;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.service.AdminCarreraService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCarreraController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class AdminCarreraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminCarreraService carreraService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener estadísticas de carreras")
    void obtenerCarrerasConEstadisticas_Success() throws Exception {
        when(carreraService.obtenerTodasConEstadisticas()).thenReturn(List.of(new CarreraAdminDTO()));

        mockMvc.perform(get("/api/admin/carreras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe registrar una carrera")
    void registrarCarrera_Success() throws Exception {
        CarreraRequest request = new CarreraRequest();
        request.setNombre("Ingeniería");

        when(carreraService.registrarCarrera(any())).thenReturn(new CarreraResponse());

        mockMvc.perform(post("/api/admin/carreras")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener todas las carreras simples")
    void obtenerTodasSimples_Success() throws Exception {
        when(carreraService.obtenerTodas()).thenReturn(List.of());
        mockMvc.perform(get("/api/admin/carreras/simples"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe asociar carrera a facultad")
    void asociarCarreraFacultad_Success() throws Exception {
        mockMvc.perform(post("/api/admin/carreras/{carreraId}/facultades/{facultadId}", UUID.randomUUID(), UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener planes detallados")
    void obtenerPlanesDetallados_Success() throws Exception {
        when(carreraService.obtenerPlanesDetallados(any())).thenReturn(List.of());
        mockMvc.perform(get("/api/admin/carreras/{carreraId}/planes/detallados", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener detalle de plan por año")
    void obtenerDetallePlan_Success() throws Exception {
        when(carreraService.obtenerDetallePlan(any(), anyInt())).thenReturn(new com.sysacad.backend.dto.admin.PlanDetalleDTO());
        mockMvc.perform(get("/api/admin/carreras/{carreraId}/plan/2024", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede registrar carrera (Forbidden)")
    void registrarCarrera_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(post("/api/admin/carreras")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anónimo no puede ver carreras (Unauthorized)")
    void obtenerCarreras_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/carreras"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin obtiene planes detallados (Vacio)")
    void obtenerPlanesDetallados_Empty() throws Exception {
        when(carreraService.obtenerPlanesDetallados(any())).thenReturn(List.of());
        mockMvc.perform(get("/api/admin/carreras/" + UUID.randomUUID() + "/planes/detallados"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante NO debe acceder a administración de carreras")
    void obtenerCarreras_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/carreras"))
                .andExpect(status().isForbidden());
    }
}
