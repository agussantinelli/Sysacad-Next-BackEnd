package com.sysacad.backend.integration;

import com.sysacad.backend.dto.admin.AdminInscripcionRequest;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.ComisionRepository;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class AdminInscripcionIntegrationTest extends IntegrationTestBase {

    @Autowired
    private InscripcionCursadoRepository inscripcionCursadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ComisionRepository comisionRepository;

    @Test
    @DisplayName("Admin puede inscribir a un alumno en una comisión")
    void inscribirAlumno_Success() throws Exception {
        // Setup: Alumno y Comisión real
        Usuario alumno = new Usuario();
        alumno.setLegajo("ALU001");
        alumno.setRol(RolUsuario.ESTUDIANTE);
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
        alumno = usuarioRepository.save(alumno);

        Comision comision = new Comision();
        comision.setNombre("1K1");
        comision.setAnio(2024);
        comision.setTurno("MAÑANA");
        comision = comisionRepository.save(comision);

        AdminInscripcionRequest request = new AdminInscripcionRequest();
        request.setIdAlumno(alumno.getId());
        request.setTipo("CURSADA");
        request.setIdReferencia(comision.getId());
        request.setIdMateria(UUID.randomUUID()); 

        mockMvc.perform(post("/api/admin/inscripcion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertTrue(inscripcionCursadoRepository.findAll().stream()
                .anyMatch(i -> i.getUsuario().getLegajo().equals("ALU001")));
    }
}
