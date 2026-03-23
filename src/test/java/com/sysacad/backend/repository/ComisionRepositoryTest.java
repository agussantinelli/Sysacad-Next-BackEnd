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
class ComisionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ComisionRepository comisionRepository;

    private Carrera carrera;
    private Materia materia;
    private Usuario profesor;

    @BeforeEach
    void setUp() {
        carrera = new Carrera();
        carrera.setNombre("Sistemas");
        carrera.setAlias("ISI");
        entityManager.persist(carrera);

        materia = new Materia();
        materia.setNombre("Algoritmos");
        materia.setTipoMateria(TipoMateria.BASICA);
        materia.setDuracion(DuracionMateria.CUATRIMESTRAL);
        materia.setHorasCursado((short) 64);
        entityManager.persist(materia);

        profesor = new Usuario();
        profesor.setLegajo("PROF001");
        profesor.setPassword("password");
        profesor.setTipoDocumento(TipoDocumento.DNI);
        profesor.setDni("12345678");
        profesor.setNombre("Juan");
        profesor.setApellido("Perez");
        profesor.setMail("juan@test.com");
        profesor.setFechaNacimiento(LocalDate.of(1980, 1, 1));
        profesor.setGenero(Genero.M);
        profesor.setFechaIngreso(LocalDate.now());
        profesor.setRol(RolUsuario.PROFESOR);
        profesor.setEstado(EstadoUsuario.ACTIVO);
        entityManager.persist(profesor);
    }

    @Test
    @DisplayName("Debe encontrar comisiones por ID de materia")
    void findByMateriasId_Success() {
        Comision comision = createComision("1K1", 2024);
        comision.setMaterias(List.of(materia));
        entityManager.persist(comision);
        entityManager.flush();

        List<Comision> result = comisionRepository.findByMateriasId(materia.getId());

        assertFalse(result.isEmpty());
        assertEquals("1K1", result.get(0).getNombre());
    }

    @Test
    @DisplayName("Debe encontrar años distintos por ID de profesor")
    void findDistinctAniosByProfesorId_Success() {
        Comision c1 = createComision("1K1", 2024);
        c1.setProfesores(List.of(profesor));
        Comision c2 = createComision("1K2", 2023);
        c2.setProfesores(List.of(profesor));
        
        entityManager.persist(c1);
        entityManager.persist(c2);
        entityManager.flush();

        List<Integer> anios = comisionRepository.findDistinctAniosByProfesorId(profesor.getId());

        assertEquals(2, anios.size());
        assertTrue(anios.contains(2024));
        assertTrue(anios.contains(2023));
    }

    @Test
    @DisplayName("Debe verificar existencia por nombre")
    void existsByNombre_Success() {
        entityManager.persist(createComision("EXISTE", 2024));
        entityManager.flush();

        assertTrue(comisionRepository.existsByNombre("EXISTE"));
        assertFalse(comisionRepository.existsByNombre("NO_EXISTE"));
    }

    private Comision createComision(String nombre, Integer anio) {
        Comision c = new Comision();
        c.setNombre(nombre);
        c.setAnio(anio);
        c.setTurno("NOCHE");
        c.setNivel(1);
        c.setCarrera(carrera);
        return c;
    }
}
