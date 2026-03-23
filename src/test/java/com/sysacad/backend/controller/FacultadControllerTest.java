package com.sysacad.backend.controller;

import com.sysacad.backend.dto.facultad.FacultadResponse;
import com.sysacad.backend.mapper.FacultadMapper;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.service.FacultadService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultadController.class)
@ActiveProfiles("test")
class FacultadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultadService facultadService;

    @MockBean
    private FacultadMapper facultadMapper;

    @Test
    @WithMockUser
    @DisplayName("Usuario autenticado debe poder listar facultades")
    void listarTodas_Success() throws Exception {
        when(facultadService.listarTodas()).thenReturn(List.of(new FacultadRegional()));
        when(facultadMapper.toDTOs(any())).thenReturn(List.of(new FacultadResponse()));

        mockMvc.perform(get("/api/facultades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    @DisplayName("Debe buscar facultad por ID")
    void buscarPorId_Success() throws Exception {
        UUID id = UUID.randomUUID();
        FacultadRegional f = new FacultadRegional();
        f.setId(id);

        when(facultadService.buscarPorId(id)).thenReturn(f);
        when(facultadMapper.toDTO(f)).thenReturn(new FacultadResponse());

        mockMvc.perform(get("/api/facultades/{id}", id))
                .andExpect(status().isOk());
    }
}
