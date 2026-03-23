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

    @BeforeEach
    void setUp() {
        alumno = new Usuario();
        alumno.setLegajo("ALU777");
        alumno.setPassword("p");
        alumno.setTipoDocumento(TipoDocumento.DNI);
        alumno.setDni(UUID.randomUUID().toString().substring(0, 8));
        alumno.setNombre("A");
        alumno.setApellido("B");
        alumno.setMail("a@b.com");
        alumno.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        alumno.setGenero(Genero.MASCULINO);
        alumno.setFechaIngreso(LocalDate.now());
        alumno.setRol(RolUsuario.ALUMNO);
        alumno.setEstado(EstadoUsuario.ACTIVO);
        entityManager.persist(alumno);

        carrera = new Carrera();
        carrera.setNombre("Sistemas");
        entityManager.persist(carrera);
    }

    @Test
    @DisplayName("Debe listar matriculaciones por ID de usuario")
    void findByIdIdUsuario_Success() {
        Matriculacion m = new Matriculacion();
        m.setId(new Matriculacion.MatriculacionId(alumno.getId(), carrera.getId()));
        m.setUsuario(alumno);
        m.setCarrera(carrera);
        m.setFechaMatriculacion(LocalDate.now());
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
        m.setId(new Matriculacion.MatriculacionId(alumno.getId(), carrera.getId()));
        m.setUsuario(alumno);
        m.setCarrera(carrera);
        m.setFechaMatriculacion(LocalDate.now());
        m.setEstado("ACTIVO");
        entityManager.persist(m);
        entityManager.flush();

        long count = matriculacionRepository.countByIdIdCarrera(carrera.getId());
        assertEquals(1, count);
    }
}
