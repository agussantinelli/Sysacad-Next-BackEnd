package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.admin.MesaAdminDTO;
import com.sysacad.backend.dto.admin.MesaExamenRequest;
import com.sysacad.backend.service.AdminMesaService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminMesaController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class AdminMesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminMesaService mesaService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener mesas con estadísticas")
    void obtenerMesasConEstadisticas_Success() throws Exception {
        when(mesaService.obtenerTodasConEstadisticas()).thenReturn(List.of(new MesaAdminDTO()));

        mockMvc.perform(get("/api/admin/mesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder crear un turno de examen")
    void crearTurno_Success() throws Exception {
        MesaExamenRequest request = new MesaExamenRequest();
        
        mockMvc.perform(post("/api/admin/mesas/turnos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder eliminar un turno")
    void eliminarTurno_Success() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/admin/mesas/turnos/{id}", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder agregar detalle a mesa")
    void agregarDetalleMesa_Success() throws Exception {
        com.sysacad.backend.dto.admin.DetalleMesaRequest request = new com.sysacad.backend.dto.admin.DetalleMesaRequest();
        mockMvc.perform(post("/api/admin/mesas/detalles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener profesores disponibles")
    void obtenerProfesoresDisponibles_Success() throws Exception {
        mockMvc.perform(get("/api/admin/mesas/profesores-disponibles")
                        .param("idMateria", UUID.randomUUID().toString())
                        .param("fecha", "2024-05-20")
                        .param("hora", "09:00"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener un turno por ID")
    void obtenerTurno_Success() throws Exception {
        UUID id = UUID.randomUUID();
        when(mesaService.obtenerTurnoPorId(id)).thenReturn(new com.sysacad.backend.dto.mesa_examen.MesaExamenResponse());

        mockMvc.perform(get("/api/admin/mesas/turnos/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder editar un turno")
    void editarTurno_Success() throws Exception {
        UUID id = UUID.randomUUID();
        MesaExamenRequest request = new MesaExamenRequest();
        mockMvc.perform(put("/api/admin/mesas/turnos/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede crear turnos (Forbidden)")
    void crearTurno_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(post("/api/admin/mesas/turnos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anónimo no puede ver mesas de examen (Unauthorized)")
    void obtenerMesas_Unauthorized_AsAnonymous() throws Exception {
        mockMvc.perform(get("/api/admin/mesas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Eliminar detalle de mesa exitosamente")
    void eliminarDetalleMesa_Success() throws Exception {
        mockMvc.perform(delete("/api/admin/mesas/{idMesa}/detalle/{nroDetalle}", UUID.randomUUID(), 1)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
