package com.sysacad.backend.integration;

import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoRequest;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.ComisionRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "ALU001", roles = "ESTUDIANTE")
class InscripcionCursadoIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ComisionRepository comisionRepository;

    @Test
    @DisplayName("Estudiante puede inscribirse a una comisión")
    void inscribirComision_Success() throws Exception {
        Usuario alumno = new Usuario();
        alumno.setLegajo("ALU001");
        alumno.setRol(RolUsuario.ESTUDIANTE);
        alumno = usuarioRepository.save(alumno);

        Comision comision = new Comision();
        comision.setNombre("2K1");
        comision.setAnio(2024);
        comision.setTurno("NOCHE");
        comision = comisionRepository.save(comision);

        InscripcionCursadoRequest request = new InscripcionCursadoRequest();
        request.setIdComision(comision.getId());

        mockMvc.perform(post("/api/inscripciones-cursado")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
