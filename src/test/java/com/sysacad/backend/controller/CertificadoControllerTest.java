package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.CertificadoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CertificadoController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class CertificadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificadoService certificadoService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(roles = "ESTUDIANTE", username = "ALU001")
    @DisplayName("Estudiante debe poder descargar certificado regular")
    void descargarCertificadoRegular_Success() throws Exception {
        Usuario alumno = new Usuario();
        alumno.setId(UUID.randomUUID());
        alumno.setLegajo("ALU001");

        when(usuarioRepository.findByLegajoOrMail(anyString(), anyString())).thenReturn(Optional.of(alumno));
        when(certificadoService.generarCertificadoRegular(alumno.getId())).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/api/alumnos/certificado-regular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().exists("Content-Disposition"));
    }
}
