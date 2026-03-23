package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.CertificadoService;
import com.sysacad.backend.service.ProfesorService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(ProfesorController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class ProfesorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfesorService profesorService;

    @MockBean
    private CertificadoService certificadoService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener sus materias asignadas")
    void getMisMaterias_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        profesor.setLegajo("PROF001");

        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerMateriasAsignadas(profesor.getId())).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores/mis-materias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener sus comisiones")
    void getMisComisiones_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        profesor.setLegajo("PROF001");

        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerTodasLasComisiones(profesor.getId())).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores/mis-comisiones"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener comisiones de una materia")
    void getComisionesDeMateria_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerComisionesDeMateria(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores/materias/" + UUID.randomUUID() + "/comisiones"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener sus mesas de examen")
    void getMisMesasExamen_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerMesasExamenProfesor(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores/mesas-examen"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener detalles de mesa de examen")
    void getDetallesMesaExamen_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerDetallesMesaProfesor(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores/mesas-examen/" + UUID.randomUUID() + "/materias"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener inscriptos a examen")
    void getInscriptosMesaExamen_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerInscriptosExamen(any(), any(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores/mesas-examen/" + UUID.randomUUID() + "/materias/1/inscriptos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe cargar notas por lote")
    void cargarNotasLote_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));

        mockMvc.perform(post("/api/profesores/mesas-examen/calificar-lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe descargar su certificado")
    void descargarCertificadoRegularDocente_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        profesor.setLegajo("PROF001");
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(certificadoService.generarCertificadoDocente(any())).thenReturn(new byte[0]);

        mockMvc.perform(get("/api/profesores/certificado-regular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().exists("Content-Disposition"));
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener inscriptos a comisión")
    void getInscriptosComision_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerInscriptosCursada(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/profesores/comisiones/" + UUID.randomUUID() + "/materias/" + UUID.randomUUID() + "/inscriptos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe cargar notas de cursada")
    void cargarNotasComision_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));

        String body = "{\"notas\":[]}"; // Simula CargaNotasCursadaDTO
        mockMvc.perform(post("/api/profesores/comisiones/" + UUID.randomUUID() + "/materias/" + UUID.randomUUID() + "/calificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener estadísticas generales")
    void getEstadisticasGenerales_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerEstadisticasGenerales(any(), any())).thenReturn(null);

        mockMvc.perform(get("/api/profesores/estadisticas/general"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener estadísticas por materia")
    void getEstadisticasPorMateria_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));

        mockMvc.perform(get("/api/profesores/estadisticas/materias/" + UUID.randomUUID()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Profesor debe obtener años disponibles")
    void getAniosDisponibles_Success() throws Exception {
        Usuario profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(profesor));
        when(profesorService.obtenerAniosConActividad(any())).thenReturn(List.of(2023, 2024));

        mockMvc.perform(get("/api/profesores/estadisticas/anios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "PROFESOR", username = "PROF001")
    @DisplayName("Falla cuando el profesor no existe")
    void getMisMaterias_NotFound() throws Exception {
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profesores/mis-materias"))
                .andExpect(status().isInternalServerError()); // Porque lanza RuntimeException
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede acceder a materias de profesor")
    void getMisMaterias_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(get("/api/profesores/mis-materias"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede acceder a comisiones de profesor")
    void getMisComisiones_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(get("/api/profesores/mis-comisiones"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede acceder a mesas de profesor")
    void getMisMesasExamen_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(get("/api/profesores/mesas-examen"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede cargar notas")
    void cargarNotasLote_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(post("/api/profesores/mesas-examen/calificar-lote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede ver estadísticas de profesor")
    void getEstadisticasGenerales_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(get("/api/profesores/estadisticas/general"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Anónimo no puede descargar certificados")
    void descargarCertificadoRegularDocente_Unauthorized_AsAnonymous() throws Exception {
        mockMvc.perform(get("/api/profesores/certificado-regular"))
                .andExpect(status().isUnauthorized());
    }
}
