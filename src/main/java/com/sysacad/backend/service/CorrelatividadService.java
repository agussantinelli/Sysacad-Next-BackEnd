package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CorrelatividadService {

    private final com.sysacad.backend.repository.InscripcionCursadoRepository inscripcionCursadoRepository;
    private final com.sysacad.backend.repository.InscripcionExamenRepository inscripcionExamenRepository;
    private final MateriaRepository materiaRepository;

    @Autowired
    public CorrelatividadService(
            com.sysacad.backend.repository.InscripcionCursadoRepository inscripcionCursadoRepository,
            com.sysacad.backend.repository.InscripcionExamenRepository inscripcionExamenRepository,
            MateriaRepository materiaRepository) {
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
        this.materiaRepository = materiaRepository;
    }

    @Transactional(readOnly = true)
    public boolean puedeCursar(UUID idAlumno, UUID idMateriaAspirante) {
        Materia materiaObjetivo = materiaRepository.findById(idMateriaAspirante)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        if (materiaObjetivo.getCorrelativas() == null || materiaObjetivo.getCorrelativas().isEmpty()) {
            return true;
        }

        // Obtener IDs de materias con cursada aprobada (Regular o Promocionado)
        java.util.Set<UUID> materiasRegularizadas = inscripcionCursadoRepository.findByUsuarioId(idAlumno).stream()
                .filter(i -> i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR ||
                             i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO)
                .map(i -> i.getMateria().getId())
                .collect(Collectors.toSet());
        
        // Agregar también las que tienen final aprobado (implica regularizada)
        java.util.Set<UUID> finalesAprobados = inscripcionExamenRepository.findByUsuarioId(idAlumno).stream()
                .filter(i -> i.getEstado() == com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO)
                .map(i -> {
                    if (i.getDetalleMesaExamen() != null && i.getDetalleMesaExamen().getMateria() != null) {
                        return i.getDetalleMesaExamen().getMateria().getId();
                    }
                    if (i.getMateria() != null) {
                        return i.getMateria().getId();
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        materiasRegularizadas.addAll(finalesAprobados);
        
        // IDs de materias aprobadas definitivamente (Promocionadas o Final Aprobado)
        java.util.Set<UUID> materiasAprobadas = inscripcionCursadoRepository.findByUsuarioId(idAlumno).stream()
                .filter(i -> i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO)
                .map(i -> i.getMateria().getId())
                .collect(Collectors.toSet());
        
        materiasAprobadas.addAll(finalesAprobados);

        for (com.sysacad.backend.modelo.Correlatividad correlatividad : materiaObjetivo.getCorrelativas()) {
            UUID idCorrelativa = correlatividad.getCorrelativa().getId();
            if (correlatividad.getTipo() == com.sysacad.backend.modelo.enums.TipoCorrelatividad.REGULAR) {
                // Requiere estar regularizada (o aprobada)
                if (!materiasRegularizadas.contains(idCorrelativa)) {
                    return false;
                }
            } else {
                // Requiere estar aprobada (Promocionada o Final)
                if (!materiasAprobadas.contains(idCorrelativa)) {
                    return false;
                }
            }
        }

        return true;
    }
    public boolean puedeRendir(UUID idAlumno, UUID idMateriaAspirante) {
        Materia materiaObjetivo = materiaRepository.findById(idMateriaAspirante)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        boolean cursadaAprobada = inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(idAlumno, idMateriaAspirante)
                .stream()
                .anyMatch(i -> i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR ||
                               i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO);

        if (!cursadaAprobada) {
            return false;
        }

        if (materiaObjetivo.getCorrelativas() == null || materiaObjetivo.getCorrelativas().isEmpty()) {
            return true;
        }

        java.util.Set<UUID> materiasAprobadas = inscripcionCursadoRepository.findByUsuarioId(idAlumno).stream()
                .filter(i -> i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO)
                .map(i -> i.getMateria().getId())
                .collect(Collectors.toSet());
        
        java.util.Set<UUID> finalesAprobados = inscripcionExamenRepository.findByUsuarioId(idAlumno).stream()
                .filter(i -> i.getEstado() == com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO)
                .map(i -> {
                    if (i.getDetalleMesaExamen() != null && i.getDetalleMesaExamen().getMateria() != null) {
                        return i.getDetalleMesaExamen().getMateria().getId();
                    }
                    if (i.getMateria() != null) {
                        return i.getMateria().getId();
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        
        materiasAprobadas.addAll(finalesAprobados);

        for (com.sysacad.backend.modelo.Correlatividad correlatividad : materiaObjetivo.getCorrelativas()) {
            UUID idCorrelativa = correlatividad.getCorrelativa().getId();
            if (!materiasAprobadas.contains(idCorrelativa)) {
                return false;
            }
        }

        return true;
    }
}