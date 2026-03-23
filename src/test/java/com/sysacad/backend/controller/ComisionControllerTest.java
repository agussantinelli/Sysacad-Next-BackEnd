package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.dto.comision.ComisionResponse;
import com.sysacad.backend.mapper.ComisionMapper;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.service.ComisionService;
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

@WebMvcTest(ComisionController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class ComisionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ComisionService comisionService;

    @MockBean
    private ComisionMapper comisionMapper;



    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder crear una comisión")
    void crearComision_Admin_Success() throws Exception {
        ComisionRequest request = new ComisionRequest();
        request.setNombre("1K1");
        request.setAnio(2024);

        Comision stub = new Comision();
        stub.setId(UUID.randomUUID());
        stub.setNombre("1K1");

        ComisionResponse responseDto = new ComisionResponse();
        responseDto.setId(stub.getId());
        responseDto.setNombre("1K1");

        when(comisionMapper.toEntity(any())).thenReturn(stub);
        when(comisionService.crearComision(any())).thenReturn(stub);
        when(comisionMapper.toDTO(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/comisiones")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("1K1"));
    }

    @Test
    @WithMockUser(roles = "PROFESOR")
    @DisplayName("Profesor debe poder listar comisiones por año")
    void listarPorAnio_Profesor_Success() throws Exception {
        when(comisionService.listarPorAnio(2024)).thenReturn(List.of(new Comision()));
        when(comisionMapper.toDTOs(any())).thenReturn(List.of(new ComisionResponse()));

        mockMvc.perform(get("/api/comisiones").param("anio", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder buscar comisión por ID")
    void buscarPorId_Success() throws Exception {
        UUID id = UUID.randomUUID();
        Comision c = new Comision();
        c.setId(id);

        ComisionResponse dto = new ComisionResponse();
        dto.setId(id);

        when(comisionService.buscarPorId(id)).thenReturn(Optional.of(c));
        when(comisionMapper.toDTO(c)).thenReturn(dto);

        mockMvc.perform(get("/api/comisiones/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }
}
