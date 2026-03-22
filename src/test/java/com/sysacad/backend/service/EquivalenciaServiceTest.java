package com.sysacad.backend.service;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.EstadoExamen;
import com.sysacad.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EquivalenciaServiceTest {

    @Mock private EquivalenciaRepository equivalenciaRepository;
    @Mock private InscripcionExamenRepository inscripcionExamenRepository;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private MateriaRepository materiaRepository;

    @InjectMocks
    private EquivalenciaService equivalenciaService;

    private Usuario alumno;
    private Matriculacion nuevaMatricula;
    private UUID materiaOrigenId;
    private UUID materiaDestinoId;

    @BeforeEach
    void setUp() {
        alumno = new Usuario();
        alumno.setId(UUID.randomUUID());

        materiaOrigenId = UUID.randomUUID();
        materiaDestinoId = UUID.randomUUID();

        Carrera carreraDestino = new Carrera();
        carreraDestino.setId(UUID.randomUUID());

        PlanDeEstudio plan = new PlanDeEstudio();
        plan.setId(new PlanDeEstudio.PlanId(carreraDestino.getId(), 2023));
        plan.setCarrera(carreraDestino);

        nuevaMatricula = new Matriculacion();
        nuevaMatricula.setPlan(plan);
    }

    @Test
    void procesarEquivalencias_DeberiaGenerarEquivalencia_CuandoExisteReglaYAlumnoAproboOrigen() {
        InscripcionExamen examenAprobado = new InscripcionExamen();
        Materia materiaOrigen = new Materia();
        materiaOrigen.setId(materiaOrigenId);
        examenAprobado.setMateria(materiaOrigen);
        examenAprobado.setEstado(EstadoExamen.APROBADO);

        when(inscripcionExamenRepository.findByUsuarioId(alumno.getId())).thenReturn(List.of(examenAprobado));

        Equivalencia regla = new Equivalencia();
        regla.setIdMateriaOrigen(materiaOrigenId);
        regla.setIdMateriaDestino(materiaDestinoId);
        when(equivalenciaRepository.findByIdCarreraDestinoAndNroPlanDestino(any(), anyInt()))
                .thenReturn(List.of(regla));

        when(materiaRepository.findById(materiaDestinoId)).thenReturn(Optional.of(new Materia()));

        equivalenciaService.procesarEquivalencias(alumno, nuevaMatricula);

        verify(inscripcionExamenRepository, times(1)).save(any(InscripcionExamen.class));
    }

    @Test
    void procesarEquivalencias_NoDeberiaGenerarNada_CuandoMateriaDestinoYaEstaAprobada() {
        InscripcionExamen examenAprobado = new InscripcionExamen();
        Materia materiaOrigen = new Materia();
        materiaOrigen.setId(materiaOrigenId);
        examenAprobado.setMateria(materiaOrigen);
        examenAprobado.setEstado(EstadoExamen.APROBADO);

        InscripcionExamen examenDestino = new InscripcionExamen();
        Materia materiaDestino = new Materia();
        materiaDestino.setId(materiaDestinoId);
        examenDestino.setMateria(materiaDestino);
        examenDestino.setEstado(EstadoExamen.APROBADO);

        when(inscripcionExamenRepository.findByUsuarioId(alumno.getId())).thenReturn(List.of(examenAprobado, examenDestino));

        Equivalencia regla = new Equivalencia();
        regla.setIdMateriaOrigen(materiaOrigenId);
        regla.setIdMateriaDestino(materiaDestinoId);
        when(equivalenciaRepository.findByIdCarreraDestinoAndNroPlanDestino(any(), anyInt()))
                .thenReturn(List.of(regla));

        equivalenciaService.procesarEquivalencias(alumno, nuevaMatricula);

        verify(inscripcionExamenRepository, never()).save(any(InscripcionExamen.class));
    }
}
