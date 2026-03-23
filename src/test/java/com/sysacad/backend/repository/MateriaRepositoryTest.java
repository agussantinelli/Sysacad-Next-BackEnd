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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MateriaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    @DisplayName("Debe encontrar materias por nombre ignorando mayúsculas")
    void findByNombreContainingIgnoreCase_Success() {
        Materia m1 = new Materia();
        m1.setNombre("Analisis Matematico");
        m1.setTipoMateria(TipoMateria.BASICA);
        m1.setDuracion(com.sysacad.backend.modelo.enums.DuracionMateria.CUATRIMESTRAL);
        m1.setHorasCursado((short) 64);
        entityManager.persist(m1);
        
        Materia m2 = new Materia();
        m2.setNombre("Álgebra");
        m2.setTipoMateria(TipoMateria.BASICA);
        m2.setDuracion(com.sysacad.backend.modelo.enums.DuracionMateria.CUATRIMESTRAL);
        m2.setHorasCursado((short) 64);
        entityManager.persist(m2);
        
        entityManager.flush();

        List<Materia> result = materiaRepository.findByNombreContainingIgnoreCase("analisis");
        assertEquals(1, result.size());
        assertTrue(result.get(0).getNombre().contains("Análisis"));
    }

    @Test
    @DisplayName("Debe encontrar materias por tipo")
    void findByTipoMateria_Success() {
        Materia m1 = new Materia();
        m1.setNombre("Materia 1");
        m1.setTipoMateria(TipoMateria.BASICA);
        m1.setHorasCursado((short) 64);
        m1.setDuracion(com.sysacad.backend.modelo.enums.DuracionMateria.CUATRIMESTRAL);
        entityManager.persist(m1);
        
        Materia m2 = new Materia();
        m2.setNombre("Materia 2");
        m2.setTipoMateria(TipoMateria.ESPECIFICA);
        m2.setHorasCursado((short) 128);
        m2.setDuracion(com.sysacad.backend.modelo.enums.DuracionMateria.ANUAL);
        entityManager.persist(m2);
        
        entityManager.flush();

        List<Materia> result = materiaRepository.findByTipoMateria(TipoMateria.BASICA);
        assertEquals(1, result.size());
        assertEquals(TipoMateria.BASICA, result.get(0).getTipoMateria());
    }
}
