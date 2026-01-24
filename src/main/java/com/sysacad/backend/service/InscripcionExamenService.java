package com.sysacad.backend.service;

import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenRequest;
import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenResponse;
import com.sysacad.backend.dto.inscripcion_examen.CargaNotaExamenRequest;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.InscripcionExamen;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.DetalleMesaExamenRepository;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
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

    @Autowired
    private com.sysacad.backend.mapper.InscripcionExamenMapper inscripcionExamenMapper;

    @Transactional
    public InscripcionExamenResponse inscribirAlumno(InscripcionExamenRequest request) {
        DetalleMesaExamen.DetalleId detalleId = new DetalleMesaExamen.DetalleId(request.getIdMesaExamen(),
                request.getNroDetalle());

        DetalleMesaExamen detalle = detalleMesaExamenRepository.findById(detalleId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa de examen (detalle) no encontrada"));

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getIdUsuario()));

        // Validar si ya está inscripto
        Optional<InscripcionExamen> existente = inscripcionExamenRepository
                .findByUsuarioIdAndDetalleMesaExamenId(usuario.getId(), detalle.getId());

        if (existente.isPresent()) {
            throw new BusinessLogicException("El alumno ya está inscripto a este examen");
        }

        InscripcionExamen inscripcion = new InscripcionExamen();
        inscripcion.setUsuario(usuario);
        inscripcion.setDetalleMesaExamen(detalle);
        inscripcion.setFechaInscripcion(LocalDateTime.now());
        inscripcion.setEstado(com.sysacad.backend.modelo.enums.EstadoExamen.PENDIENTE);

        inscripcion = inscripcionExamenRepository.save(inscripcion);
        return inscripcionExamenMapper.toDTO(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<InscripcionExamenResponse> getInscripcionesByAlumno(UUID idAlumno) {
        return inscripcionExamenMapper.toDTOs(inscripcionExamenRepository.findByUsuarioId(idAlumno));
    }

    @Transactional
    public void darDeBaja(UUID idInscripcion) {
        InscripcionExamen insc = inscripcionExamenRepository.findById(idInscripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + idInscripcion));

        // Aquí se podrían agregar validaciones de fecha (e.g. 48hs antes)

        inscripcionExamenRepository.delete(insc);
    }

    @Transactional
    public InscripcionExamenResponse calificarExamen(UUID idInscripcion, CargaNotaExamenRequest request) {
        InscripcionExamen insc = inscripcionExamenRepository.findById(idInscripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + idInscripcion));

        insc.setNota(request.getNota());
        insc.setEstado(request.getEstado()); // APROBADO, DESAPROBADO, etc.

        insc = inscripcionExamenRepository.save(insc);
        return inscripcionExamenMapper.toDTO(insc);
    }

}
