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
        // 1. Obtener el historial académico del alumno
        List<Inscripcion> historial = inscripcionRepository.findByIdIdUsuario(idAlumno);

        // 2. Filtrar solo las materias aprobadas (Nota >= 6 o Promocionada)
        // Nota: Esto es un ejemplo, ajusta la lógica de "Aprobado" a tu reglamento
        List<UUID> materiasAprobadas = historial.stream()
                .filter(i -> i.getNotaFinal() != null && i.getNotaFinal().doubleValue() >= 6.0)
                .map(i -> i.getComision().getMaterias().get(0).getId()) // Asumiendo comisión de una sola materia
                .collect(Collectors.toList());

        // 3. Obtener las correlativas de la materia objetivo
        // (Aquí necesitaríamos que Materia tenga la lista de correlativas mapeada o usar una query nativa)
        // Por ahora, simulamos que validamos si tiene las correlativas
        // List<Materia> correlativasNecesarias = materiaRepository.obtenerCorrelativas(idMateriaAspirante);

        // if (materiasAprobadas.containsAll(correlativasNecesarias.map(Materia::getId))) return true;

        return true; // Retorno temporal hasta mapear la relación ManyToMany de Correlativas
    }
}