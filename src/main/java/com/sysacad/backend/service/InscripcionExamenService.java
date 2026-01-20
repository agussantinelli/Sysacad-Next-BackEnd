package com.sysacad.backend.service;

import com.sysacad.backend.dto.*;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.InscripcionExamen;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.DetalleMesaExamenRepository;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InscripcionExamenService {

    @Autowired
    private InscripcionExamenRepository inscripcionExamenRepository;

    @Autowired
    private DetalleMesaExamenRepository detalleMesaExamenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public InscripcionExamenResponse inscribirAlumno(InscripcionExamenRequest request) {
        DetalleMesaExamen detalle = detalleMesaExamenRepository.findById(request.getIdDetalleMesa())
                .orElseThrow(() -> new RuntimeException("Mesa de examen no encontrada"));

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar si ya está inscripto
        Optional<InscripcionExamen> existente = inscripcionExamenRepository
                .findByUsuarioIdAndDetalleMesaExamenId(usuario.getId(), detalle.getId());

        if (existente.isPresent()) {
            throw new RuntimeException("El alumno ya está inscripto a este examen");
        }

        InscripcionExamen inscripcion = new InscripcionExamen();
        inscripcion.setUsuario(usuario);
        inscripcion.setDetalleMesaExamen(detalle);
        inscripcion.setFechaInscripcion(LocalDateTime.now());
        inscripcion.setEstado("PENDIENTE");

        inscripcion = inscripcionExamenRepository.save(inscripcion);
        return mapToResponse(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<InscripcionExamenResponse> getInscripcionesByAlumno(UUID idAlumno) {
        return inscripcionExamenRepository.findByUsuarioId(idAlumno).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void darDeBaja(UUID idInscripcion) {
        InscripcionExamen insc = inscripcionExamenRepository.findById(idInscripcion)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        // Aquí se podrían agregar validaciones de fecha (e.g. 48hs antes)

        inscripcionExamenRepository.delete(insc);
    }

    @Transactional
    public InscripcionExamenResponse calificarExamen(UUID idInscripcion, CargaNotaExamenRequest request) {
        InscripcionExamen insc = inscripcionExamenRepository.findById(idInscripcion)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        insc.setNota(request.getNota());
        insc.setEstado(request.getEstado()); // APROBADO, DESAPROBADO, etc.

        insc = inscripcionExamenRepository.save(insc);
        return mapToResponse(insc);
    }

    private InscripcionExamenResponse mapToResponse(InscripcionExamen insc) {
        InscripcionExamenResponse response = new InscripcionExamenResponse();
        response.setId(insc.getId());
        response.setNombreAlumno(insc.getUsuario().getNombre() + " " + insc.getUsuario().getApellido());
        response.setLegajoAlumno(insc.getUsuario().getLegajo());
        response.setNombreMateria(insc.getDetalleMesaExamen().getMateria().getNombre());
        response.setFechaExamen(insc.getDetalleMesaExamen().getDiaExamen());
        response.setHoraExamen(insc.getDetalleMesaExamen().getHoraExamen());
        response.setFechaInscripcion(insc.getFechaInscripcion());
        response.setEstado(insc.getEstado());
        response.setNota(insc.getNota());
        return response;
    }
}
