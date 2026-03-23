package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.admin.AdminComisionDTO;
import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.service.AdminComisionService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminComisionController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class AdminComisionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminComisionService adminComisionService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe listar todas las comisiones con detalle")
    void obtenerTodas_Success() throws Exception {
        when(adminComisionService.obtenerTodasConDetalle()).thenReturn(List.of(new AdminComisionDTO()));

        mockMvc.perform(get("/api/admin/comisiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe crear una comisión")
    void crearComision_Success() throws Exception {
        ComisionRequest request = new ComisionRequest();
        request.setNombre("1K1");
        request.setAnio(2024);
        request.setTurno("MAÑANA");
        request.setNivel(1);
        request.setIdCarrera(UUID.randomUUID());

        mockMvc.perform(post("/api/admin/comisiones")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener salones disponibles")
    void obtenerSalonesDisponibles_Success() throws Exception {
        when(adminComisionService.obtenerSalonesDisponibles(anyString(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/comisiones/salones-disponibles")
                        .param("turno", "MAÑANA")
                        .param("anio", "2024"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder asignar materia a comisión")
    void asignarMateria_Success() throws Exception {
        com.sysacad.backend.dto.admin.AsignarMateriaComisionRequest request = new com.sysacad.backend.dto.admin.AsignarMateriaComisionRequest();
        mockMvc.perform(post("/api/admin/comisiones/{id}/materias", UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener profesores disponibles para comisión")
    void obtenerProfesoresDisponibles_Success() throws Exception {
        com.sysacad.backend.dto.admin.AsignarMateriaComisionRequest request = new com.sysacad.backend.dto.admin.AsignarMateriaComisionRequest();
        request.setIdMateria(UUID.randomUUID());
        request.setHorarios(List.of());

        mockMvc.perform(post("/api/admin/comisiones/profesores-disponibles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener materias disponibles para comisión")
    void obtenerMateriasDisponibles_Success() throws Exception {
        UUID id = UUID.randomUUID();
        when(adminComisionService.obtenerMateriasDisponibles(id)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/comisiones/{id}/materias-disponibles", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede crear comisiones (Forbidden)")
    void crearComision_Forbidden_AsStudent() throws Exception {
        String validJson = "{\"nombre\":\"Comision A\",\"turno\":\"MAÑANA\",\"anio\":2024,\"nivel\":1,\"idCarrera\":\"" + UUID.randomUUID() + "\"}";
        mockMvc.perform(post("/api/admin/comisiones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede asignar materias (Forbidden)")
    void asignarMateria_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(post("/api/admin/comisiones/{id}/materias", UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede obtener salones disponibles (Forbidden)")
    void obtenerSalonesDisponibles_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(get("/api/admin/comisiones/salones-disponibles")
                        .param("turno", "MAÑANA")
                        .param("anio", "2024"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anónimo no puede ver comisiones (Unauthorized)")
    void obtenerTodas_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/comisiones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("crearComision falla con datos inválidos")
    void crearComision_Invalid() throws Exception {
        mockMvc.perform(post("/api/admin/comisiones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
