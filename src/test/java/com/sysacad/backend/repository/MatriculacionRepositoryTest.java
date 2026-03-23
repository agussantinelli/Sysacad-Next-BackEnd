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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MatriculacionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MatriculacionRepository matriculacionRepository;

    private Usuario alumno;
    private Carrera carrera;
    private FacultadRegional facultad;
    private PlanDeEstudio plan;

    @BeforeEach
    void setUp() {
        facultad = new FacultadRegional();
        facultad.setCiudad("Cba");
        facultad.setProvincia("Cba");
        entityManager.persist(facultad);

        carrera = new Carrera();
        carrera.setNombre("Sistemas");
        carrera.setAlias("ISI");
        entityManager.persist(carrera);

        plan = new PlanDeEstudio();
        plan.setId(new PlanDeEstudio.PlanId(carrera.getId(), 2023));
        plan.setCarrera(carrera);
        plan.setNombre("Plan 2023");
        entityManager.persist(plan);

        alumno = new Usuario();
        alumno.setLegajo("ALU777");
        alumno.setPassword("p");
        alumno.setTipoDocumento(TipoDocumento.DNI);
        alumno.setDni(UUID.randomUUID().toString().substring(0, 8));
        alumno.setNombre("A");
        alumno.setApellido("B");
        alumno.setMail("a@b.com");
        alumno.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        alumno.setGenero(Genero.M);
        alumno.setFechaIngreso(LocalDate.now());
        alumno.setRol(RolUsuario.ESTUDIANTE);
        alumno.setEstado(EstadoUsuario.ACTIVO);
        entityManager.persist(alumno);
    }

    @Test
    @DisplayName("Debe listar matriculaciones por ID de usuario")
    void findByIdIdUsuario_Success() {
        Matriculacion m = new Matriculacion();
        m.setId(new Matriculacion.MatriculacionId(alumno.getId(), facultad.getId(), carrera.getId(), 2023));
        m.setUsuario(alumno);
        m.setPlan(plan);
        m.setFacultad(facultad);
        m.setFechaInscripcion(LocalDate.now());
        m.setEstado("ACTIVO");
        entityManager.persist(m);
        entityManager.flush();

        List<Matriculacion> result = matriculacionRepository.findByIdIdUsuario(alumno.getId());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Debe contar matriculaciones por ID de carrera")
    void countByIdIdCarrera_Success() {
        Matriculacion m = new Matriculacion();
        m.setId(new Matriculacion.MatriculacionId(alumno.getId(), facultad.getId(), carrera.getId(), 2023));
        m.setUsuario(alumno);
        m.setPlan(plan);
        m.setFacultad(facultad);
        m.setFechaInscripcion(LocalDate.now());
        m.setEstado("ACTIVO");
        entityManager.persist(m);
        entityManager.flush();

        long count = matriculacionRepository.countByIdIdCarrera(carrera.getId());
        assertEquals(1, count);
    }
}
