package com.sysacad.backend.integration;

import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenRequest;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.MesaExamen;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.modelo.enums.TipoMesa;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.MesaExamenRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InscripcionExamenIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MesaExamenRepository mesaExamenRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    @DisplayName("Estudiante puede inscribirse a un examen final")
    @WithMockUser(username = "ALU001", roles = "ESTUDIANTE")
    void inscribirExamen_Success() throws Exception {
        Usuario alumno = new Usuario();
        alumno.setLegajo("ALU001");
        alumno.setRol(RolUsuario.ESTUDIANTE);
        alumno = usuarioRepository.save(alumno);

        Materia materia = new Materia();
        materia.setNombre("Física I");
        materia.setCodigo("FIS1");
        materia = materiaRepository.save(materia);

        MesaExamen mesa = new MesaExamen();
        mesa.setMateria(materia);
        mesa.setFecha(LocalDateTime.now().plusDays(2));
        mesa.setTipo(TipoMesa.FINAL);
        mesa = mesaExamenRepository.save(mesa);

        InscripcionExamenRequest request = new InscripcionExamenRequest();
        request.setIdMesa(mesa.getId());

        mockMvc.perform(post("/api/inscripciones-examen")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
