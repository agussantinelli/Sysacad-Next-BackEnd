package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Inscripcion;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.repository.InscripcionRepository;
import com.sysacad.backend.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CorrelatividadService {

    private final InscripcionRepository inscripcionRepository;
    private final MateriaRepository materiaRepository;

    @Autowired
    public CorrelatividadService(InscripcionRepository inscripcionRepository, MateriaRepository materiaRepository) {
        this.inscripcionRepository = inscripcionRepository;
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

        // Obtener el historial académico del alumno
        List<Inscripcion> historial = inscripcionRepository.findByIdIdUsuario(idAlumno);

        // Filtrar solo las materias APROBADAS (Nota >= 6)
        // Obtenemos los IDs de las materias que el alumno ya metió
        List<UUID> idsMateriasAprobadas = historial.stream()
                .filter(i -> i.getNotaFinal() != null && i.getNotaFinal().doubleValue() >= 6.0)
                // Nota: Asumimos que la comisión tiene al menos una materia asociada
                .map(i -> i.getComision().getMaterias().get(0).getId())
                .collect(Collectors.toList());

        // Verificar requisitos
        List<UUID> idsCorrelativasNecesarias = materiaObjetivo.getCorrelativas().stream()
                .map(Materia::getId)
                .collect(Collectors.toList());

        return idsMateriasAprobadas.containsAll(idsCorrelativasNecesarias);
    }
}