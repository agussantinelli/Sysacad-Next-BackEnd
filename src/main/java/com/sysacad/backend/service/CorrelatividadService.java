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
        // Obtener la materia a la que se quiere anotar
        Materia materiaObjetivo = materiaRepository.findById(idMateriaAspirante)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // Si la materia no tiene correlativas, el alumno puede cursar libremente
        if (materiaObjetivo.getCorrelativas() == null || materiaObjetivo.getCorrelativas().isEmpty()) {
            return true;
        }

        // Obtener IDs de materias aprobadas (Promocionadas o Examen Final Aprobado)
        List<UUID> idsAprobadas = new java.util.ArrayList<>();

        // Promocionadas
        idsAprobadas.addAll(inscripcionCursadoRepository.findByUsuarioId(idAlumno).stream()
                .filter(i -> i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO ||
                        i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO)
                .map(i -> i.getMateria().getId())
                .collect(Collectors.toList()));

        // Finales Aprobados
        idsAprobadas.addAll(inscripcionExamenRepository.findByUsuarioId(idAlumno).stream()
                .filter(i -> i.getEstado() == com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO)
                .map(i -> i.getDetalleMesaExamen().getMateria().getId())
                .collect(Collectors.toList()));

        // Verificar requisitos
        List<UUID> idsCorrelativasNecesarias = materiaObjetivo.getCorrelativas().stream()
                .map(Materia::getId)
                .collect(Collectors.toList());

        return idsAprobadas.containsAll(idsCorrelativasNecesarias);
    }
}