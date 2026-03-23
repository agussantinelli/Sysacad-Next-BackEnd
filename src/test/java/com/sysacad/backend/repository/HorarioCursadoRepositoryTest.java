package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.modelo.enums.DiaSemana;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class HorarioCursadoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HorarioCursadoRepository horarioCursadoRepository;

    private Comision comision;
    private Materia materia;
    private Carrera carrera;

    @BeforeEach
    void setUp() {
        carrera = new Carrera();
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
    @DisplayName("Debe encontrar horarios por ID de comisión")
    void findByIdIdComision_Success() {
        HorarioCursado horario = createHorario(comision, materia, DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(10, 0));
        entityManager.persist(horario);
        entityManager.flush();

        List<HorarioCursado> result = horarioCursadoRepository.findByIdIdComision(comision.getId());

        assertFalse(result.isEmpty());
        assertEquals(comision.getId(), result.get(0).getId().getIdComision());
    }

    @Test
    @DisplayName("Debe detectar solapamientos de horario en la misma comisión")
    void encontrarSolapamientos_ShouldDetectOverlap() {
        // Horario existente: Lunes 08:00 - 10:00
        HorarioCursado horarioExistente = createHorario(comision, materia, DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(10, 0));
        entityManager.persist(horarioExistente);
        entityManager.flush();

        // Nuevo horario que se solapa: Lunes 09:00 - 11:00
        List<HorarioCursado> solapamientos = horarioCursadoRepository.encontrarSolapamientos(
                comision.getId(), DiaSemana.LUNES, LocalTime.of(9, 0), LocalTime.of(11, 0));

        assertFalse(solapamientos.isEmpty());
        assertEquals(1, solapamientos.size());
    }

    @Test
    @DisplayName("No debe detectar solapamiento si los horarios son contiguos")
    void encontrarSolapamientos_ShouldNotDetectWhenContiguous() {
        // Horario existente: Lunes 08:00 - 10:00
        HorarioCursado horarioExistente = createHorario(comision, materia, DiaSemana.LUNES, LocalTime.of(8, 0), LocalTime.of(10, 0));
        entityManager.persist(horarioExistente);
        entityManager.flush();

        // Nuevo horario contiguo: Lunes 10:00 - 12:00
        List<HorarioCursado> solapamientos = horarioCursadoRepository.encontrarSolapamientos(
                comision.getId(), DiaSemana.LUNES, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertTrue(solapamientos.isEmpty());
    }

    private HorarioCursado createHorario(Comision c, Materia m, DiaSemana dia, LocalTime desde, LocalTime hasta) {
        HorarioCursado h = new HorarioCursado();
        h.setId(new HorarioCursado.HorarioCursadoId(c.getId(), m.getId(), dia, desde));
        h.setHoraHasta(hasta);
        h.setComision(c);
        h.setMateria(m);
        return h;
    }
}
