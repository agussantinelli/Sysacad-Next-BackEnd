package com.sysacad.backend.controller;

import com.sysacad.backend.service.MatriculacionService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatriculacionController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class MatriculacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatriculacionService matriculacionService;

    @Test
    @WithMockUser(roles = "ESTUDIANTE", username = "ALU001")
    @DisplayName("Estudiante debe poder ver sus materias por carrera")
    void obtenerMisMateriasPorCarrera_Success() throws Exception {
        when(matriculacionService.obtenerMateriasPorCarreraDelAlumno(anyString())).thenReturn(List.of(new com.sysacad.backend.dto.carrera_materias.CarreraMateriasDTO()));

        mockMvc.perform(get("/api/alumnos/mis-carreras-materias"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE", username = "ALU001")
    @DisplayName("Estudiante debe poder ver historial de una materia")
    void obtenerHistorialMateria_Success() throws Exception {
        UUID idMateria = UUID.randomUUID();
        when(matriculacionService.obtenerHistorialMateria(anyString(), any())).thenReturn(new com.sysacad.backend.dto.historial.HistorialMateriaDTO());

        mockMvc.perform(get("/api/alumnos/mis-carreras-materias/historial/{idMateria}", idMateria))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE", username = "ALU001")
    @DisplayName("Estudiante no tiene materias por carrera (No Content)")
    void obtenerMisMateriasPorCarrera_NoContent() throws Exception {
        when(matriculacionService.obtenerMateriasPorCarreraDelAlumno(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/alumnos/mis-carreras-materias"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin no puede ver materias de alumno (Forbidden)")
    void obtenerMisMateriasPorCarrera_Forbidden_AsAdmin() throws Exception {
        mockMvc.perform(get("/api/alumnos/mis-carreras-materias"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede ver materias de alumno (Forbidden)")
    void obtenerMisMateriasPorCarrera_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(get("/api/alumnos/mis-carreras-materias"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE", username = "ALU001")
    @DisplayName("Obtener historial materia (Vacio)")
    void obtenerHistorialMateria_Empty() throws Exception {
        when(matriculacionService.obtenerHistorialMateria(anyString(), any())).thenReturn(null);

        mockMvc.perform(get("/api/alumnos/mis-carreras-materias/historial/{idMateria}", UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin no puede ver historial de alumno (Forbidden)")
    void obtenerHistorialMateria_Forbidden_AsAdmin() throws Exception {
        mockMvc.perform(get("/api/alumnos/mis-carreras-materias/historial/{idMateria}", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor no puede ver historial de alumno (Forbidden)")
    void obtenerHistorialMateria_Forbidden_AsProfesor() throws Exception {
        mockMvc.perform(get("/api/alumnos/mis-carreras-materias/historial/{idMateria}", UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anónimo no puede ver materias de alumno (Unauthorized)")
    void obtenerMisMateriasPorCarrera_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/alumnos/mis-carreras-materias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anónimo no puede ver historial de alumno (Unauthorized)")
    void obtenerHistorialMateria_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/alumnos/mis-carreras-materias/historial/{idMateria}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}
