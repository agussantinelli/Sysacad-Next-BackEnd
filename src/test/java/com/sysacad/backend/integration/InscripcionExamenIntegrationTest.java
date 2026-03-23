package com.sysacad.backend.integration;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.MesaExamen;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.DetalleMesaExamenRepository;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.MesaExamenRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private DetalleMesaExamenRepository detalleMesaExamenRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    @DisplayName("Estudiante puede inscribirse a un examen final")
    @WithMockUser(username = "ALU001", roles = "ESTUDIANTE")
    void inscribirExamen_Success() throws Exception {
        Usuario alumno = new Usuario();
        alumno.setLegajo("ALU001");
        alumno.setNombre("Agus");
        alumno.setApellido("Santi");
        alumno.setMail("alu001@test.com");
        alumno.setDni("12345678");
        alumno.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        alumno.setGenero(com.sysacad.backend.modelo.enums.Genero.M);
        alumno.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        alumno.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        alumno.setFechaIngreso(LocalDate.now());
        alumno.setPassword("password");
        alumno.setRol(RolUsuario.ESTUDIANTE);
        alumno = usuarioRepository.save(alumno);

        Materia materia = new Materia();
        materia.setNombre("Física I");
        materia.setTipoMateria(com.sysacad.backend.modelo.enums.TipoMateria.BASICA);
        materia.setDuracion(com.sysacad.backend.modelo.enums.DuracionMateria.CUATRIMESTRAL);
        materia.setHorasCursado((short) 64);
        materia = materiaRepository.save(materia);

        MesaExamen mesa = new MesaExamen();
        mesa.setNombre("Febrero 2024");
        mesa.setFechaInicio(LocalDate.now().plusDays(1));
        mesa.setFechaFin(LocalDate.now().plusDays(10));
        mesa = mesaExamenRepository.save(mesa);

        DetalleMesaExamen detalle = new DetalleMesaExamen();
        detalle.setId(new DetalleMesaExamen.DetalleId(mesa.getId(), 1));
        detalle.setMesaExamen(mesa);
        detalle.setMateria(materia);
        detalle.setDiaExamen(LocalDate.now().plusDays(5));
        detalle.setHoraExamen(LocalTime.of(9, 0));
        
        Usuario profesor = new Usuario();
        profesor.setLegajo("PROF1");
        profesor.setNombre("Prof");
        profesor.setApellido("Test");
        profesor.setMail("prof1@test.com");
        profesor.setDni("87654321");
        profesor.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        profesor.setGenero(com.sysacad.backend.modelo.enums.Genero.M);
        profesor.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        profesor.setFechaNacimiento(LocalDate.of(1980, 1, 1));
        profesor.setFechaIngreso(LocalDate.now());
        profesor.setPassword("password");
        profesor.setRol(RolUsuario.PROFESOR);
        profesor = usuarioRepository.save(profesor);
        detalle.setPresidente(profesor);
        
        detalle = detalleMesaExamenRepository.save(detalle);

        com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenRequest request = new com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenRequest();
        request.setIdUsuario(alumno.getId());
        request.setIdDetalleMesa(detalle.getId().getIdMesaExamen());
        request.setNroDetalle(detalle.getId().getNroDetalle());

        mockMvc.perform(post("/api/inscripciones-examen")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
