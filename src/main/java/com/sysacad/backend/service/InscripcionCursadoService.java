package com.sysacad.backend.service;

import com.sysacad.backend.dto.*;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class InscripcionCursadoService {

    @Autowired
    private InscripcionCursadoRepository inscripcionCursadoRepository;

    @Autowired
    private CalificacionCursadaRepository calificacionCursadaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private ComisionRepository comisionRepository;

    public InscripcionCursadoResponse inscribir(InscripcionCursadoRequest request) {
        // 1. Validar Usuario
        Usuario alumno = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // 2. Validar Materia
        Materia materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // 3. Validar Comision
        Comision comision = comisionRepository.findById(request.getIdComision())
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

        // 4. Validar que la Comisión dicte esa materia
        boolean dictaMateria = comision.getMaterias().stream()
                .anyMatch(m -> m.getId().equals(materia.getId()));
        if (!dictaMateria) {
            throw new RuntimeException("La comisión seleccionada no dicta la materia indicada.");
        }

        // 5. Validar si ya está inscripto
        if (inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(alumno.getId(), materia.getId()).isPresent()) {
            throw new RuntimeException("El alumno ya está inscripto a cursar esta materia.");
        }

        // 6. Crear Inscripción
        InscripcionCursado insc = new InscripcionCursado();
        insc.setUsuario(alumno);
        insc.setMateria(materia);
        insc.setComision(comision);
        insc.setFechaInscripcion(LocalDateTime.now());
        insc.setEstado("CURSENDO"); // Estado inicial

        insc = inscripcionCursadoRepository.save(insc);
        return mapToResponse(insc);
    }

    public List<InscripcionCursadoResponse> obtenerHistorial(UUID idUsuario) {
        return inscripcionCursadoRepository.findByUsuarioId(idUsuario).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CalificacionCursadaResponse cargarNota(UUID idInscripcion, CalificacionCursadaRequest request) {
        InscripcionCursado insc = inscripcionCursadoRepository.findById(idInscripcion)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        CalificacionCursada calif = new CalificacionCursada();
        calif.setInscripcionCursado(insc);
        calif.setDescripcion(request.getDescripcion());
        calif.setNota(request.getNota());
        calif.setFecha(LocalDate.now());

        calif = calificacionCursadaRepository.save(calif);
        return mapToCalificacionResponse(calif);
    }

    // Mapper methods
    private InscripcionCursadoResponse mapToResponse(InscripcionCursado insc) {
        InscripcionCursadoResponse dto = new InscripcionCursadoResponse();
        dto.setId(insc.getId());
        dto.setNombreMateria(insc.getMateria().getNombre());
        dto.setNombreComision(insc.getComision().getNombre());
        dto.setAnioCursado(insc.getComision().getAnio());
        dto.setEstado(insc.getEstado());
        dto.setNotaFinal(insc.getNotaFinal());
        dto.setFechaPromocion(insc.getFechaPromocion());
        dto.setFechaInscripcion(insc.getFechaInscripcion());
        dto.setCalificaciones(insc.getCalificaciones().stream()
                .map(this::mapToCalificacionResponse)
                .collect(Collectors.toList()));
        return dto;
    }

    private CalificacionCursadaResponse mapToCalificacionResponse(CalificacionCursada calif) {
        CalificacionCursadaResponse dto = new CalificacionCursadaResponse();
        dto.setId(calif.getId());
        dto.setDescripcion(calif.getDescripcion());
        dto.setNota(calif.getNota());
        dto.setFecha(calif.getFecha());
        return dto;
    }
}
