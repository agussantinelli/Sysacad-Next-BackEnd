package com.sysacad.backend.integration;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "ALU001", roles = "ESTUDIANTE")
class CertificadoIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Estudiante puede solicitar certificado de alumno regular")
    void solicitarCertificado_Success() throws Exception {
        Usuario alumno = new Usuario();
        alumno.setLegajo("ALU001");
        alumno.setNombre("Agus");
        alumno.setApellido("Santi");
        alumno.setMail("alu001@test.com");
        alumno.setDni("12345678");
        alumno.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        alumno.setGenero(com.sysacad.backend.modelo.enums.Genero.M);
        alumno.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        alumno.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
        alumno.setFechaIngreso(java.time.LocalDate.now());
        alumno.setPassword("password");
        alumno.setRol(RolUsuario.ESTUDIANTE);
        usuarioRepository.save(alumno);

        mockMvc.perform(get("/api/certificados/alumno-regular"))
                .andExpect(status().isOk());
    }
}
