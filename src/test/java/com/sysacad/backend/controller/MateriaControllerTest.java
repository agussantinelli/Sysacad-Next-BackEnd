package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.materia.MateriaRequest;
import com.sysacad.backend.dto.materia.MateriaResponse;
import com.sysacad.backend.mapper.MateriaMapper;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.MateriaService;
import com.sysacad.backend.service.MesaExamenService;
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

@WebMvcTest(MateriaController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class MateriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MateriaService materiaService;

    @MockBean
    private MateriaMapper materiaMapper;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private MesaExamenService mesaExamenService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder crear una materia")
    void crearMateria_Admin_Success() throws Exception {
        MateriaRequest request = new MateriaRequest();
        request.setNombre("Matemática");

        Materia stub = new Materia();
        stub.setId(UUID.randomUUID());
        stub.setNombre("Matemática");

        MateriaResponse dto = new MateriaResponse();
        dto.setId(stub.getId());
        dto.setNombre("Matemática");

        when(materiaMapper.toEntity(any())).thenReturn(stub);
        when(materiaService.crearMateria(any())).thenReturn(stub);
        when(materiaMapper.toDTO(any())).thenReturn(dto);

        mockMvc.perform(post("/api/materias")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Matemática"));
    }

    @Test
    @WithMockUser
    @DisplayName("Usuario autenticado debe poder listar materias")
    void listarTodas_Success() throws Exception {
        when(materiaService.listarTodas()).thenReturn(List.of(new Materia()));
        when(materiaMapper.toDTO(any())).thenReturn(new MateriaResponse());

        mockMvc.perform(get("/api/materias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder eliminar una materia")
    void eliminarMateria_Admin_Success() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/materias/{id}", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede eliminar materia (Forbidden)")
    void eliminarMateria_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(delete("/api/materias/{id}", UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor puede actualizar materia")
    void actualizarMateria_Profesor_Success() throws Exception {
        UUID id = UUID.randomUUID();
        MateriaRequest request = new MateriaRequest();
        when(materiaMapper.toEntity(any())).thenReturn(new Materia());
        when(materiaService.actualizarMateria(any(), any())).thenReturn(new Materia());
        when(materiaMapper.toDTO(any())).thenReturn(new MateriaResponse());

        mockMvc.perform(put("/api/materias/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Usuario debe obtener una materia por ID")
    void buscarPorId_Success() throws Exception {
        UUID id = UUID.randomUUID();
        when(materiaService.buscarPorId(id)).thenReturn(Optional.of(new Materia()));
        when(materiaMapper.toDTO(any())).thenReturn(new MateriaResponse());

        mockMvc.perform(get("/api/materias/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE", username = "ALU001")
    @DisplayName("Estudiante debe ver mesas de examen de una materia")
    void getMesasPorMateria_Success() throws Exception {
        UUID id = UUID.randomUUID();
        com.sysacad.backend.modelo.Usuario usuario = new com.sysacad.backend.modelo.Usuario();
        usuario.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo(anyString())).thenReturn(Optional.of(usuario));
        when(mesaExamenService.obtenerMesasDisponibles(any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/materias/{id}/mesas", id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Usuario debe ver correlativas de una materia")
    void getCorrelativas_Success() throws Exception {
        UUID id = UUID.randomUUID();
        when(materiaService.obtenerArbolCorrelativas(any(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/materias/{id}/correlativas", id)
                        .param("carreraId", UUID.randomUUID().toString())
                        .param("nroPlan", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anónimo no puede listar materias (Unauthorized)")
    void listarTodas_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/materias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("buscarPorId devuelve error si no existe materia")
    void buscarPorId_NotFound() throws Exception {
        when(materiaService.buscarPorId(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/materias/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe ver materias filtradas por tipo")
    void listarTodas_WithTipo_Success() throws Exception {
        when(materiaService.buscarPorTipo(any())).thenReturn(List.of(new Materia()));
        when(materiaMapper.toDTO(any())).thenReturn(new MateriaResponse());

        mockMvc.perform(get("/api/materias")
                        .param("tipo", "BASICA"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Estudiante no puede crear materias (Forbidden)")
    void crearMateria_Forbidden_AsStudent() throws Exception {
        mockMvc.perform(post("/api/materias")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}
