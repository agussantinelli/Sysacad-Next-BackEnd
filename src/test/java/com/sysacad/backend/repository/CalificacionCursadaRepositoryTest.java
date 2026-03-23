package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CalificacionCursadaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CalificacionCursadaRepository calificacionCursadaRepository;

    @Test
    @DisplayName("Debe encontrar calificación por inscripción e instancia")
    void findByInscripcionCursadoIdAndInstanciaEvaluacionId_Success() {
        // Mocking structure for brevity but persisting mandatory fields
        Usuario alu = createMinimalUsuario("ALU777");
        entityManager.persist(alu);
        
        Materia mat = new Materia(); mat.setNombre("Mat"); entityManager.persist(mat);
        Carrera carr = new Carrera(); carr.setNombre("Carr"); entityManager.persist(carr);
        
        Comision com = new Comision();
        com.setNombre("1K1"); com.setAnio(2024); com.setTurno("T"); com.setNivel(1); com.setCarrera(carr);
        entityManager.persist(com);

        InscripcionCursado ins = new InscripcionCursado();
        ins.setUsuario(alu); ins.setMateria(mat); ins.setComision(com); 
        ins.setEstado(EstadoCursada.CURSANDO); ins.setFechaInscripcion(LocalDateTime.now());
        entityManager.persist(ins);

        InstanciaEvaluacion inst = new InstanciaEvaluacion();
        inst.setNombre("Parcial 1");
        entityManager.persist(inst);

        CalificacionCursada cal = new CalificacionCursada();
        cal.setInscripcionCursado(ins);
        cal.setInstanciaEvaluacion(inst);
        cal.setNota(new BigDecimal("7.00"));
        entityManager.persist(cal);
        entityManager.flush();

        java.util.Optional<CalificacionCursada> result = calificacionCursadaRepository
                .findByInscripcionCursadoIdAndInstanciaEvaluacionId(ins.getId(), inst.getId());
        
        assertTrue(result.isPresent());
        assertEquals(0, new BigDecimal("7.00").compareTo(result.get().getNota()));
    }

    private Usuario createMinimalUsuario(String legajo) {
        Usuario u = new Usuario();
        u.setLegajo(legajo); u.setPassword("p"); u.setTipoDocumento(TipoDocumento.DNI);
        u.setDni(UUID.randomUUID().toString().substring(0, 8)); u.setNombre("N"); u.setApellido("A");
        u.setMail(legajo + "@t.com"); u.setFechaNacimiento(java.time.LocalDate.now());
        u.setGenero(Genero.M); u.setFechaIngreso(java.time.LocalDate.now());
        u.setRol(RolUsuario.ESTUDIANTE); u.setEstado(EstadoUsuario.ACTIVO);
        return u;
    }
}
