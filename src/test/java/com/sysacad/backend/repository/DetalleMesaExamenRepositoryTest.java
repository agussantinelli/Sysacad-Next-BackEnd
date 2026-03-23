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
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DetalleMesaExamenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DetalleMesaExamenRepository detalleMesaExamenRepository;

    private MesaExamen mesa;
    private Materia materia;
    private Carrera carrera;
    private Usuario presidente;

    @BeforeEach
    void setUp() {
        mesa = new MesaExamen();
        mesa.setNombre("Mesa"); mesa.setFechaInicio(LocalDate.now()); mesa.setFechaFin(LocalDate.now().plusDays(10));
        entityManager.persist(mesa);

        carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");
        carrera.setAlias("ISI");
        entityManager.persist(carrera);

        materia = new Materia();
        materia.setNombre("Materia");
        materia.setHorasCursado((short) 64);
        materia.setDuracion(DuracionMateria.CUATRIMESTRAL);
        entityManager.persist(materia);

        presidente = createMinimalUsuario("PRES001");
        entityManager.persist(presidente);
    }

    @Test
    @DisplayName("Debe encontrar el nro máximo de detalle para una mesa")
    void findMaxNroDetalle_Success() {
        DetalleMesaExamen d = new DetalleMesaExamen();
        d.setId(new DetalleMesaExamen.DetalleId(mesa.getId(), 5));
        d.setMesaExamen(mesa); d.setMateria(materia); d.setPresidente(presidente);
        d.setDiaExamen(LocalDate.now()); d.setHoraExamen(LocalTime.now());
        entityManager.persist(d);
        entityManager.flush();

        java.util.Optional<Integer> max = detalleMesaExamenRepository.findMaxNroDetalle(mesa.getId());
        assertTrue(max.isPresent());
        assertEquals(5, max.get());
    }

    private Usuario createMinimalUsuario(String legajo) {
        Usuario u = new Usuario();
        u.setLegajo(legajo); u.setPassword("p"); u.setTipoDocumento(TipoDocumento.DNI);
        u.setDni(UUID.randomUUID().toString().substring(0, 8)); u.setNombre("N"); u.setApellido("A");
        u.setMail(legajo + "@t.com"); u.setFechaNacimiento(LocalDate.now());
        u.setGenero(Genero.M); u.setFechaIngreso(LocalDate.now());
        u.setRol(RolUsuario.PROFESOR); u.setEstado(EstadoUsuario.ACTIVO);
        return u;
    }
}
