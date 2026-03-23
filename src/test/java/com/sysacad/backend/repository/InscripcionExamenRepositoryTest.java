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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class InscripcionExamenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InscripcionExamenRepository inscripcionExamenRepository;

    private Usuario alumno;
    private Usuario presidente;
    private Materia materia;
    private MesaExamen mesaExamen;
    private DetalleMesaExamen detalleMesaExamen;

    @BeforeEach
    void setUp() {
        alumno = createUsuario("ALU001", RolUsuario.ESTUDIANTE);
        presidente = createUsuario("PROF001", RolUsuario.PROFESOR);
        entityManager.persist(alumno);
        entityManager.persist(presidente);

        materia = new Materia();
        materia.setNombre("Sistemas Operativos");
        entityManager.persist(materia);

        mesaExamen = new MesaExamen();
        mesaExamen.setNombre("Finales Dic 2024");
        mesaExamen.setFechaInicio(LocalDate.of(2024, 12, 1));
        mesaExamen.setFechaFin(LocalDate.of(2024, 12, 15));
        entityManager.persist(mesaExamen);

        detalleMesaExamen = new DetalleMesaExamen();
        detalleMesaExamen.setId(new DetalleMesaExamen.DetalleId(mesaExamen.getId(), 1));
        detalleMesaExamen.setMesaExamen(mesaExamen);
        detalleMesaExamen.setMateria(materia);
        detalleMesaExamen.setPresidente(presidente);
        detalleMesaExamen.setDiaExamen(LocalDate.of(2024, 12, 5));
        detalleMesaExamen.setHoraExamen(LocalTime.of(9, 0));
        entityManager.persist(detalleMesaExamen);
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
        InscripcionExamen inscripcion = createInscripcion(alumno, detalleMesaExamen, EstadoExamen.PENDIENTE);
        entityManager.persist(inscripcion);
        entityManager.flush();

        List<InscripcionExamen> result = inscripcionExamenRepository.findByUsuarioId(alumno.getId());

        assertFalse(result.isEmpty());
        assertEquals(alumno.getId(), result.get(0).getUsuario().getId());
    }

    @Test
    @DisplayName("Debe contar inscripciones por detalle de mesa")
    void countByDetalleMesaExamenId_Success() {
        InscripcionExamen inscripcion = createInscripcion(alumno, detalleMesaExamen, EstadoExamen.PENDIENTE);
        entityManager.persist(inscripcion);
        entityManager.flush();

        Long count = inscripcionExamenRepository.countByDetalleMesaExamenId(detalleMesaExamen.getId());

        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Debe contar exámenes por profesor y año")
    void countExamenesByProfesor_Success() {
        InscripcionExamen inscripcion = createInscripcion(alumno, detalleMesaExamen, EstadoExamen.PENDIENTE);
        entityManager.persist(inscripcion);
        entityManager.flush();

        long count = inscripcionExamenRepository.countExamenesByProfesor(presidente.getId(), 2024);

        assertEquals(1, count);
    }

    @Test
    @DisplayName("Debe verificar si existe inscripción por usuario, materia y estado")
    void existsByUsuarioIdAndMateriaIdAndEstado_Success() {
        InscripcionExamen inscripcion = createInscripcion(alumno, detalleMesaExamen, EstadoExamen.APROBADO);
        entityManager.persist(inscripcion);
        entityManager.flush();

        boolean exists = inscripcionExamenRepository.existsByUsuarioIdAndMateriaIdAndEstado(
                alumno.getId(), materia.getId(), EstadoExamen.APROBADO);

        assertTrue(exists);
    }

    private InscripcionExamen createInscripcion(Usuario u, DetalleMesaExamen d, EstadoExamen estado) {
        InscripcionExamen inscripcion = new InscripcionExamen();
        inscripcion.setUsuario(u);
        inscripcion.setDetalleMesaExamen(d);
        inscripcion.setEstado(estado);
        inscripcion.setFechaInscripcion(LocalDateTime.now());
        return inscripcion;
    }
}
