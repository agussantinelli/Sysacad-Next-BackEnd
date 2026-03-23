package com.sysacad.backend.integration;

import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoRequest;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.ComisionRepository;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.repository.MateriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "ALU_CURSADO", roles = "ESTUDIANTE")
class InscripcionCursadoIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ComisionRepository comisionRepository;

    @Autowired
    private FacultadRegionalRepository facultadRegionalRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    @DisplayName("Estudiante puede inscribirse a una comisión")
    void inscribirComision_Success() throws Exception {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);
        String legajo = "ALU_CURSADO";
        String dni = "4444" + uniqueSuffix;

        Usuario alumno = new Usuario();
        alumno.setLegajo(legajo);
        alumno.setNombre("Agus");
        alumno.setApellido("Santi");
        alumno.setMail("alu_" + uniqueSuffix + "@test.com");
        alumno.setDni(dni);
        alumno.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        alumno.setGenero(com.sysacad.backend.modelo.enums.Genero.M);
        alumno.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        alumno.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
        alumno.setFechaIngreso(java.time.LocalDate.now());
        alumno.setPassword("password");
        alumno.setRol(RolUsuario.ESTUDIANTE);
        alumno = usuarioRepository.save(alumno);

        FacultadRegional facultad = new FacultadRegional();
        facultad.setCiudad("Tucumán_" + uniqueSuffix);
        facultad.setProvincia("Tucumán");
        facultad = facultadRegionalRepository.save(facultad);

        Carrera carrera = new Carrera();
        carrera.setNombre("Sistemas_" + uniqueSuffix);
        carrera.setAlias("ISI_" + uniqueSuffix);
        carrera.setFacultades(java.util.Set.of(facultad));
        carrera = carreraRepository.save(carrera);

        Materia materia = new Materia();
        materia.setNombre("Materia_" + uniqueSuffix);
        materia.setTipoMateria(com.sysacad.backend.modelo.enums.TipoMateria.BASICA);
        materia.setDuracion(com.sysacad.backend.modelo.enums.DuracionMateria.CUATRIMESTRAL);
        materia.setHorasCursado((short) 64);
        materia = materiaRepository.save(materia);

        Comision comision = new Comision();
        comision.setNombre("2K1_" + uniqueSuffix);
        comision.setAnio(2024);
        comision.setTurno("NOCHE");
        comision.setNivel(2);
        comision.setCarrera(carrera);
        comision.setMaterias(java.util.List.of(materia));
        comision = comisionRepository.save(comision);

        InscripcionCursadoRequest request = new InscripcionCursadoRequest();
        request.setIdComision(comision.getId());
        request.setIdMateria(materia.getId());

        mockMvc.perform(post("/api/inscripciones-cursado")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
