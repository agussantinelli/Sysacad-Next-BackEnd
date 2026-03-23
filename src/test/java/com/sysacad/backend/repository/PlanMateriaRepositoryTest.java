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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PlanMateriaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlanMateriaRepository planMateriaRepository;

    private Carrera carrera;
    private Materia materia;

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
    }

    @Test
    @DisplayName("Debe encontrar materias de plan por ID de materia")
    void findByIdIdMateria_Success() {
        PlanMateria pm = new PlanMateria();
        pm.setId(new PlanMateria.PlanMateriaId(carrera.getId(), 2023, materia.getId()));
        pm.setNivel((short) 1);
        pm.setMateria(materia);
        pm.setCodigoMateria("K1234");
        entityManager.persist(pm);
        entityManager.flush();

        List<PlanMateria> result = planMateriaRepository.findByIdIdMateria(materia.getId());
        assertEquals(1, result.size());
    }
}
