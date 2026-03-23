package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class InscripcionCursadoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InscripcionCursadoRepository inscripcionCursadoRepository;

    private Usuario alumno;
    private Usuario profesor;
    private Materia materia;
    private Comision comision;
    private Carrera carrera;

    @BeforeEach
    void setUp() {
        carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");
        entityManager.persist(carrera);

        materia = new Materia();
        materia.setNombre("Matemática I");
        entityManager.persist(materia);

        alumno = createUsuario("ALU001", RolUsuario.ESTUDIANTE);
        profesor = createUsuario("PROF001", RolUsuario.PROFESOR);
        entityManager.persist(alumno);
        entityManager.persist(profesor);

        comision = new Comision();
        comision.setNombre("1K1");
        comision.setAnio(2024);
        comision.setTurno("MAÑANA");
        comision.setNivel(1);
        comision.setCarrera(carrera);
        comision.setMaterias(List.of(materia));
        comision.setProfesores(List.of(profesor));
        entityManager.persist(comision);
    }

    private Usuario createUsuario(String legajo, RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setLegajo(legajo);
        usuario.setPassword("password");
        usuario.setTipoDocumento(TipoDocumento.DNI);
        usuario.setDni(UUID.randomUUID().toString().substring(0, 8));
        usuario.setNombre("Nombre");
        usuario.setApellido("Apellido");
        usuario.setMail(legajo + "@test.com");
        usuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setGenero(Genero.M);
        usuario.setFechaIngreso(LocalDate.now());
        usuario.setRol(rol);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        return usuario;
    }

    @Test
    @DisplayName("Debe encontrar inscripciones por ID de usuario")
    void findByUsuarioId_Success() {
        InscripcionCursado inscripcion = createInscripcion(alumno, materia, comision, EstadoCursada.CURSANDO);
        entityManager.persist(inscripcion);
        entityManager.flush();

        List<InscripcionCursado> result = inscripcionCursadoRepository.findByUsuarioId(alumno.getId());

        assertFalse(result.isEmpty());
        assertEquals(alumno.getId(), result.get(0).getUsuario().getId());
    }

    @Test
    @DisplayName("Debe encontrar inscripciones activas o semi-activas mediante query personalizada")
    void findInscriptosActivosOSemiesActivos_Success() {
        InscripcionCursado inscripcion = createInscripcion(alumno, materia, comision, EstadoCursada.CURSANDO);
        entityManager.persist(inscripcion);
        entityManager.flush();

        List<InscripcionCursado> result = inscripcionCursadoRepository.findInscriptosActivosOSemiesActivos(
                comision.getId(), materia.getId(), LocalDate.now().minusDays(1));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Debe contar alumnos por profesor y año")
    void countAlumnosByProfesor_Success() {
        InscripcionCursado inscripcion = createInscripcion(alumno, materia, comision, EstadoCursada.CURSANDO);
        entityManager.persist(inscripcion);
        entityManager.flush();

        long count = inscripcionCursadoRepository.countAlumnosByProfesor(profesor.getId(), 2024);

        assertEquals(1, count);
    }

    @Test
    @DisplayName("Debe calcular el promedio de notas por profesor")
    void calculateAverageGradeByProfesor_Success() {
        InscripcionCursado inscripcion = createInscripcion(alumno, materia, comision, EstadoCursada.REGULAR);
        inscripcion.setNotaFinal(new BigDecimal("8.50"));
        entityManager.persist(inscripcion);
        entityManager.flush();

        Double average = inscripcionCursadoRepository.calculateAverageGradeByProfesor(profesor.getId(), 2024);

        assertNotNull(average);
        assertEquals(8.50, average, 0.01);
    }

    private InscripcionCursado createInscripcion(Usuario u, Materia m, Comision c, EstadoCursada estado) {
        InscripcionCursado inscripcion = new InscripcionCursado();
        inscripcion.setUsuario(u);
        inscripcion.setMateria(m);
        inscripcion.setComision(c);
        inscripcion.setEstado(estado);
        inscripcion.setFechaInscripcion(LocalDateTime.now());
        return inscripcion;
    }
}
