package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.modelo.enums.RolCargo;
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
class AsignacionMateriaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AsignacionMateriaRepository asignacionMateriaRepository;

    private Usuario docente;
    private Materia materia;

    @BeforeEach
    void setUp() {
        docente = new Usuario();
        docente.setLegajo("DOC001");
        // ... set mandatory fields ...
        docente.setRol(com.sysacad.backend.modelo.enums.RolUsuario.PROFESOR);
        docente.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        docente.setPassword("pass");
        docente.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        docente.setDni(UUID.randomUUID().toString().substring(0, 8));
        docente.setNombre("Test");
        docente.setApellido("Docente");
        docente.setMail("doc@test.com");
        docente.setFechaNacimiento(java.time.LocalDate.of(1980, 1, 1));
        docente.setGenero(com.sysacad.backend.modelo.enums.Genero.M);
        docente.setFechaIngreso(java.time.LocalDate.now());
        entityManager.persist(docente);

        materia = new Materia();
        materia.setNombre("Algoritmos");
        materia.setHorasCursado((short) 64);
        materia.setDuracion(DuracionMateria.CUATRIMESTRAL);
        materia.setTipoMateria(TipoMateria.BASICA);
        entityManager.persist(materia);
    }

    @Test
    @DisplayName("Debe encontrar asignaciones por ID de usuario y cargo")
    void findByIdIdUsuarioAndCargo_Success() {
        AsignacionMateria asignacion = new AsignacionMateria();
        asignacion.setId(new AsignacionMateria.AsignacionMateriaId(docente.getId(), materia.getId()));
        asignacion.setCargo(RolCargo.JEFE_CATEDRA);
        asignacion.setProfesor(docente);
        asignacion.setMateria(materia);
        entityManager.persist(asignacion);
        entityManager.flush();

        List<AsignacionMateria> result = asignacionMateriaRepository.findByIdIdUsuarioAndCargo(docente.getId(), RolCargo.JEFE_CATEDRA);
        assertEquals(1, result.size());
    }
}
