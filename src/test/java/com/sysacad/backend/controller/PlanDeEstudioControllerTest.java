package com.sysacad.backend.controller;

import com.sysacad.backend.mapper.PlanDeEstudioMapper;
import com.sysacad.backend.service.PlanDeEstudioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

@WebMvcTest(PlanDeEstudioController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class PlanDeEstudioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanDeEstudioService planService;

    @MockBean
    private PlanDeEstudioMapper planMapper;

    @Test
    @WithMockUser
    @DisplayName("Debe listar planes vigentes por carrera")
    void listarPlanesVigentes_Success() throws Exception {
        UUID idCarrera = UUID.randomUUID();
        when(planService.listarPlanesVigentes(idCarrera)).thenReturn(List.of());

        mockMvc.perform(get("/api/planes/vigentes/{idCarrera}", idCarrera))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Debe listar todos los planes por carrera")
    void listarTodosPorCarrera_Success() throws Exception {
        UUID idCarrera = UUID.randomUUID();
        when(planService.listarTodosPorCarrera(idCarrera)).thenReturn(List.of());

        mockMvc.perform(get("/api/planes/carrera/{idCarrera}", idCarrera))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe crear plan de estudio")
    void crearPlan_Success() throws Exception {
        String body = "{\"idCarrera\":\"" + UUID.randomUUID() + "\", \"nroPlan\":1, \"nombrePlan\":\"Plan 2024\"}";
        when(planMapper.toEntity(any())).thenReturn(new com.sysacad.backend.modelo.PlanDeEstudio());
        when(planService.crearPlanDeEstudio(any())).thenReturn(new com.sysacad.backend.modelo.PlanDeEstudio());

        mockMvc.perform(post("/api/planes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede crear plan (Forbidden)")
    void crearPlan_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(post("/api/planes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe agregar materia a plan")
    void agregarMateriaAPlan_Success() throws Exception {
        mockMvc.perform(post("/api/planes/materias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede agregar materia a plan (Forbidden)")
    void agregarMateriaAPlan_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(post("/api/planes/materias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anónimo no puede ver planes (Unauthorized)")
    void listarPlanes_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/planes/vigentes/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anónimo no puede crear planes (Unauthorized)")
    void crearPlan_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/planes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede crear plan (Forbidden)")
    void crearPlan_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(post("/api/planes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede agregar materia a plan (Forbidden)")
    void agregarMateriaAPlan_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(post("/api/planes/materias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}
