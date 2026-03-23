package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.EstadoAviso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AvisoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AvisoRepository avisoRepository;

    @Test
    @DisplayName("Debe encontrar avisos emitidos después de una fecha")
    void findByFechaEmisionAfter_Success() {
        Aviso a1 = new Aviso();
        a1.setTitulo("Aviso 1");
        a1.setDescripcion("Contenido 1");
        a1.setFechaEmision(LocalDateTime.now().minusDays(2));
        a1.setEstado(EstadoAviso.ACTIVO);
        entityManager.persist(a1);

        Aviso a2 = new Aviso();
        a2.setTitulo("Aviso 2");
        a2.setDescripcion("Contenido 2");
        a2.setFechaEmision(LocalDateTime.now().plusDays(1));
        a2.setEstado(EstadoAviso.ACTIVO);
        entityManager.persist(a2);

        entityManager.flush();

        List<Aviso> result = avisoRepository.findByFechaEmisionAfter(LocalDateTime.now());
        assertEquals(1, result.size());
        assertEquals("Aviso 2", result.get(0).getTitulo());
    }

    @Test
    @DisplayName("Debe listar avisos ordenados por fecha descendente")
    void findAllByOrderByFechaEmisionDesc_Success() {
        Aviso a1 = createAviso("Viejo", LocalDateTime.now().minusDays(5));
        Aviso a2 = createAviso("Nuevo", LocalDateTime.now());
        
        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.flush();

        List<Aviso> result = avisoRepository.findAllByOrderByFechaEmisionDesc();
        assertEquals("Nuevo", result.get(0).getTitulo());
    }

    private Aviso createAviso(String titulo, LocalDateTime fecha) {
        Aviso a = new Aviso();
        a.setTitulo(titulo);
        a.setDescripcion("Desc");
        a.setFechaEmision(fecha);
        a.setEstado(EstadoAviso.ACTIVO);
        return a;
    }
}
