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

    @Autowired
    private com.sysacad.backend.repository.InscripcionCursadoRepository inscripcionCursadoRepository;

    @Autowired
    private CorrelatividadService correlatividadService;

    @Transactional
    public InscripcionExamenResponse inscribirAlumno(InscripcionExamenRequest request) {
        DetalleMesaExamen.DetalleId detalleId = new DetalleMesaExamen.DetalleId(request.getIdMesaExamen(),
                request.getNroDetalle());

        DetalleMesaExamen detalle = detalleMesaExamenRepository.findById(detalleId)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa de examen (detalle) no encontrada"));

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getIdUsuario()));

        // 1. Validar si la materia ya está APROBADA o PROMOCIONADA
        boolean yaAprobada = inscripcionExamenRepository.existsByUsuarioIdAndDetalleMesaExamen_MateriaIdAndEstado(
                usuario.getId(), detalle.getMateria().getId(), com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO);
        boolean yaPromocionada = inscripcionCursadoRepository.existsByUsuarioIdAndMateriaIdAndEstado(
                usuario.getId(), detalle.getMateria().getId(), com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO);
        
        if (yaAprobada || yaPromocionada) {
            throw new BusinessLogicException("El alumno ya tiene aprobada esta materia.");
        }

        // 2. Validar Correlativas para Rendir
        if (!correlatividadService.puedeRendir(usuario.getId(), detalle.getMateria().getId())) {
            throw new BusinessLogicException("El alumno no cumple con los requisitos (Regularidad + Correlativas) para rendir esta materia.");
        }

        // 3. Validar si ya está inscripto a ESTE examen
        Optional<InscripcionExamen> existente = inscripcionExamenRepository
                .findByUsuarioIdAndDetalleMesaExamenId(usuario.getId(), detalle.getId());

        if (existente.isPresent()) {
            throw new BusinessLogicException("El alumno ya está inscripto a este examen");
        }

        // 4. Validar Superposición de Horarios con otros exámenes PENDIENTES
        List<InscripcionExamen> pendientes = inscripcionExamenRepository.findByUsuarioIdAndEstado(
                usuario.getId(), com.sysacad.backend.modelo.enums.EstadoExamen.PENDIENTE);
        
        for (InscripcionExamen insc : pendientes) {
            DetalleMesaExamen otroDetalle = insc.getDetalleMesaExamen();
            if (otroDetalle.getDiaExamen().equals(detalle.getDiaExamen())) {
                // Asumimos duración de 3 horas para el control
                java.time.LocalTime inicioNuevo = detalle.getHoraExamen();
                java.time.LocalTime finNuevo = inicioNuevo.plusHours(3);
                
                java.time.LocalTime inicioOtro = otroDetalle.getHoraExamen();
                java.time.LocalTime finOtro = inicioOtro.plusHours(3);

                if (inicioNuevo.isBefore(finOtro) && finNuevo.isAfter(inicioOtro)) {
                     throw new BusinessLogicException("Superposición horaria con el examen de: " + otroDetalle.getMateria().getNombre());
                }
            }
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

        inscripcionExamenRepository.delete(insc);
    }

    @Transactional
    public InscripcionExamenResponse calificarExamen(UUID idInscripcion, CargaNotaExamenRequest request) {
        InscripcionExamen insc = inscripcionExamenRepository.findById(idInscripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + idInscripcion));

        // Validación de Nota y Estado
        if (request.getEstado() == com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO) {
            if (request.getNota().compareTo(new java.math.BigDecimal("6.00")) < 0) {
                throw new BusinessLogicException("Para aprobar el examen, la nota debe ser 6.00 o superior.");
            }
        }

        insc.setNota(request.getNota());
        insc.setEstado(request.getEstado()); 

        insc = inscripcionExamenRepository.save(insc);

        // Regla: Si desaprueba 4 veces, pierde la regularidad
        if (request.getEstado() == com.sysacad.backend.modelo.enums.EstadoExamen.DESAPROBADO || 
            request.getEstado() == com.sysacad.backend.modelo.enums.EstadoExamen.AUSENTE) {
             
             // Buscar la cursada asociada (Debería ser la última cursada regular)
             // Asumimos que si rinde examen es porque tiene cursada (o es libre, pero si es libre no pierde regularidad)
             
             Usuario alumno = insc.getUsuario();
             com.sysacad.backend.modelo.Materia materia = insc.getDetalleMesaExamen().getMateria();
             
             Optional<com.sysacad.backend.modelo.InscripcionCursado> cursadaOpt = inscripcionCursadoRepository
                .findByUsuarioIdAndMateriaId(alumno.getId(), materia.getId());
             
             if (cursadaOpt.isPresent()) {
                 com.sysacad.backend.modelo.InscripcionCursado cursada = cursadaOpt.get();
                 if (cursada.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR) {
                     // Contar aplazos/ausentes en exámenes previos
                     long conteoFallidos = inscripcionExamenRepository.countByUsuarioIdAndDetalleMesaExamen_MateriaIdAndEstadoIn(
                         alumno.getId(), 
                         materia.getId(), 
                         List.of(com.sysacad.backend.modelo.enums.EstadoExamen.DESAPROBADO, com.sysacad.backend.modelo.enums.EstadoExamen.AUSENTE)
                     );
                     
                     // El conteo incluye el que acabamos de guardar? Sí, porque ya hicimos save() arriba.
                     if (conteoFallidos >= 4) {
                         cursada.setEstado(com.sysacad.backend.modelo.enums.EstadoCursada.LIBRE);
                         inscripcionCursadoRepository.save(cursada);
                         System.out.println(">> ALERTA: El alumno " + alumno.getLegajo() + " perdió la regularidad en " + materia.getNombre() + " por 4 aplazos.");
                     }
                 }
             }
        }

        return inscripcionExamenMapper.toDTO(insc);
    }

}
