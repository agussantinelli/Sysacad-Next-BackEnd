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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MateriaController.class)
@ActiveProfiles("test")
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
}
