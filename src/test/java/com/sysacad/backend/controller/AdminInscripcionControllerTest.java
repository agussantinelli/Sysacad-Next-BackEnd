package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.admin.AdminInscripcionRequest;
import com.sysacad.backend.service.AdminInscripcionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminInscripcionController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class AdminInscripcionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminInscripcionService adminInscripcionService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener materias para cursado de un alumno")
    void obtenerMateriasCursado_Success() throws Exception {
        UUID idAlumno = UUID.randomUUID();
        when(adminInscripcionService.obtenerMateriasParaCursado(idAlumno)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/inscripcion/cursado/materias")
                .param("idAlumno", idAlumno.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder inscribir a un alumno")
    void inscribir_Success() throws Exception {
        AdminInscripcionRequest request = new AdminInscripcionRequest();
        
        mockMvc.perform(post("/api/admin/inscripcion")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener comisiones para cursado")
    void obtenerComisionesCursado_Success() throws Exception {
        UUID idAlumno = UUID.randomUUID();
        UUID idMateria = UUID.randomUUID();
        when(adminInscripcionService.obtenerComisionesParaCursado(idAlumno, idMateria)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/inscripcion/cursado/comisiones")
                        .param("idAlumno", idAlumno.toString())
                        .param("idMateria", idMateria.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener materias para examen")
    void obtenerMateriasExamen_Success() throws Exception {
        UUID idAlumno = UUID.randomUUID();
        when(adminInscripcionService.obtenerMateriasParaExamen(idAlumno)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/inscripcion/examen/materias")
                        .param("idAlumno", idAlumno.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener mesas para examen")
    void obtenerMesasExamen_Success() throws Exception {
        UUID idAlumno = UUID.randomUUID();
        UUID idMateria = UUID.randomUUID();
        when(adminInscripcionService.obtenerMesasParaExamen(idAlumno, idMateria)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/inscripcion/examen/mesas")
                        .param("idAlumno", idAlumno.toString())
                        .param("idMateria", idMateria.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede inscribir (Forbidden)")
    void inscribir_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(post("/api/admin/inscripcion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede obtener materias de admin (Forbidden)")
    void obtenerMateriasCursado_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(get("/api/admin/inscripcion/cursado/materias")
                        .param("idAlumno", UUID.randomUUID().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede inscribir (Forbidden)")
    void inscribir_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(post("/api/admin/inscripcion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anónimo no puede inscribir (Unauthorized)")
    void inscribir_Unauthorized_AsAnonymous() throws Exception {
        mockMvc.perform(post("/api/admin/inscripcion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anónimo no puede ver materias académicas (Unauthorized)")
    void obtenerMateriasCursado_Unauthorized_AsAnonymous() throws Exception {
        mockMvc.perform(get("/api/admin/inscripcion/cursado/materias")
                        .param("idAlumno", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized());
    }
}
