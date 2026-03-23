package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Equivalencia;
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
class EquivalenciaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EquivalenciaRepository equivalenciaRepository;

    @Test
    @DisplayName("Debe encontrar equivalencias por destino")
    void findByDestino_Success() {
        UUID carrDest = UUID.randomUUID();
        Equivalencia eq = new Equivalencia();
        eq.setIdCarreraDestino(carrDest);
        eq.setNroPlanDestino(2023);
        eq.setIdMateriaDestino(UUID.randomUUID());
        eq.setIdCarreraOrigen(UUID.randomUUID());
        eq.setNroPlanOrigen(2020);
        eq.setIdMateriaOrigen(UUID.randomUUID());
        entityManager.persist(eq);
        entityManager.flush();

        List<Equivalencia> result = equivalenciaRepository.findByIdCarreraDestinoAndNroPlanDestino(carrDest, 2023);
        assertEquals(1, result.size());
    }
}
