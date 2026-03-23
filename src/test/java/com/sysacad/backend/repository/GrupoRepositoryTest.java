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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class GrupoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GrupoRepository grupoRepository;

    private Comision comision;
    private Materia materia;

    @BeforeEach
    void setUp() {
        Carrera carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");
        carrera.setAlias("ISI");
        entityManager.persist(carrera);

        materia = new Materia();
        materia.setNombre("Algoritmos");
        materia.setTipoMateria(TipoMateria.BASICA);
        materia.setDuracion(DuracionMateria.CUATRIMESTRAL);
        materia.setHorasCursado((short) 64);
        entityManager.persist(materia);

        comision = new Comision();
        comision.setNombre("1K1");
        comision.setAnio(2024);
        comision.setTurno("MAÑANA");
        comision.setNivel(1);
        comision.setCarrera(carrera);
        entityManager.persist(comision);
    }

    @Test
    @DisplayName("Debe encontrar grupo por ID de comisión e ID de materia")
    void findByIdComisionAndIdMateria_Success() {
        Grupo grupo = new Grupo();
        grupo.setNombre("Grupo de SO");
        grupo.setIdComision(comision.getId());
        grupo.setIdMateria(materia.getId());
        entityManager.persist(grupo);
        entityManager.flush();

        Optional<Grupo> result = grupoRepository.findByIdComisionAndIdMateria(comision.getId(), materia.getId());
        assertTrue(result.isPresent());
        assertEquals("Grupo de SO", result.get().getNombre());
    }
}
