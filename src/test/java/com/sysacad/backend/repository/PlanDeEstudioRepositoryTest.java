package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.PlanDeEstudio.PlanId;
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
class PlanDeEstudioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlanDeEstudioRepository planDeEstudioRepository;

    private Carrera carrera;

    @BeforeEach
    void setUp() {
        carrera = new Carrera();
        carrera.setNombre("Industrial");
        entityManager.persist(carrera);
    }

    @Test
    @DisplayName("Debe encontrar planes por ID de carrera")
    void findByIdIdCarrera_Success() {
        PlanDeEstudio p = new PlanDeEstudio();
        p.setId(new PlanId(carrera.getId(), 2023));
        p.setNombre("Plan 2023");
        p.setEsVigente(true);
        p.setCarrera(carrera);
        entityManager.persist(p);
        entityManager.flush();

        List<PlanDeEstudio> result = planDeEstudioRepository.findByIdIdCarrera(carrera.getId());
        assertEquals(1, result.size());
        assertEquals(2023, result.get(0).getId().getNroPlan());
    }
}
