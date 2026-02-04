package com.sysacad.backend.service;

import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoRequest;
import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoResponse;
import com.sysacad.backend.dto.calificacion_cursada.CalificacionCursadaRequest;
import com.sysacad.backend.dto.calificacion_cursada.CalificacionCursadaResponse;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.repository.*;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
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

    @Autowired
    private com.sysacad.backend.mapper.InscripcionCursadoMapper inscripcionCursadoMapper;

    @Autowired
    private com.sysacad.backend.mapper.CalificacionCursadaMapper calificacionCursadaMapper;

    @Autowired
    private CorrelatividadService correlatividadService;

    public InscripcionCursadoResponse inscribir(InscripcionCursadoRequest request) {

        // Validar Usuario
        Usuario alumno = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con ID: " + request.getIdUsuario()));

        // Validar Materia
        Materia materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con ID: " + request.getIdMateria()));

        // Validar Comision
        Comision comision = comisionRepository.findById(request.getIdComision())
                .orElseThrow(() -> new ResourceNotFoundException("Comisión no encontrada con ID: " + request.getIdComision()));

        // Validar que la Comisión dicte esa materia
        boolean dictaMateria = comision.getMaterias().stream()
                .anyMatch(m -> m.getId().equals(materia.getId()));
        if (!dictaMateria) {
            throw new BusinessLogicException("La comisión seleccionada no dicta la materia indicada.");
        }

        // Validar si ya está inscripto
        if (inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(alumno.getId(), materia.getId()).isPresent()) {
            throw new BusinessLogicException("El alumno ya está inscripto a cursar esta materia.");
        }

        // Validar Correlativas
        if (!correlatividadService.puedeCursar(alumno.getId(), materia.getId())) {
            throw new BusinessLogicException("El alumno no cumple con las correlativas necesarias para cursar esta materia.");
        }

        // Validar Superposición Horaria
        List<InscripcionCursado> cursadasActivas = inscripcionCursadoRepository.findByUsuarioIdAndEstado(alumno.getId(), com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO);
        
        List<HorarioCursado> horariosNuevaComision = horarioCursadoRepository.findByIdIdComisionAndIdIdMateria(comision.getId(), materia.getId());

        for (InscripcionCursado inscActiva : cursadasActivas) {
            List<HorarioCursado> horariosActiva = horarioCursadoRepository.findByIdIdComisionAndIdIdMateria(inscActiva.getComision().getId(), inscActiva.getMateria().getId());
            
            if (verificarSuperposicionHoraria(horariosNuevaComision, horariosActiva)) {
                throw new BusinessLogicException("El horario de la nueva comisión se superpone con la materia: " + inscActiva.getMateria().getNombre());
            }
        }

        // Finalmente Crea la Inscripción
        InscripcionCursado insc = new InscripcionCursado();
        insc.setUsuario(alumno);
        insc.setMateria(materia);
        insc.setComision(comision);
        insc.setFechaInscripcion(LocalDateTime.now());
        insc.setEstado(com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO); // Estado inicial

        insc = inscripcionCursadoRepository.save(insc);
        return inscripcionCursadoMapper.toDTO(insc);
    }

    public List<InscripcionCursadoResponse> obtenerHistorial(UUID idUsuario) {
        return inscripcionCursadoMapper.toDTOs(inscripcionCursadoRepository.findByUsuarioId(idUsuario));
    }

    @Autowired
    private InstanciaEvaluacionRepository instanciaEvaluacionRepository;

    public CalificacionCursadaResponse cargarNota(UUID idInscripcion, CalificacionCursadaRequest request) {
        InscripcionCursado insc = inscripcionCursadoRepository.findById(idInscripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + idInscripcion));

        // Buscar o crear InstanciaEvaluacion
        String nombreInstancia = request.getDescripcion().trim();
        InstanciaEvaluacion instancia = instanciaEvaluacionRepository.findByNombre(nombreInstancia)
                .orElseGet(() -> {
                    InstanciaEvaluacion nueva = new InstanciaEvaluacion();
                    nueva.setNombre(nombreInstancia);
                    return instanciaEvaluacionRepository.save(nueva);
                });

        CalificacionCursada calif = new CalificacionCursada();
        calif.setInscripcionCursado(insc);
        calif.setInstanciaEvaluacion(instancia);
        calif.setNota(request.getNota());
        calif.setFecha(LocalDate.now());

        calif = calificacionCursadaRepository.save(calif);

        return calificacionCursadaMapper.toDTO(calif);

    }

    public InscripcionCursadoResponse finalizarCursada(UUID idInscripcion, java.math.BigDecimal notaFinal, com.sysacad.backend.modelo.enums.EstadoCursada estado) {
        InscripcionCursado insc = inscripcionCursadoRepository.findById(idInscripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + idInscripcion));

        // Validaciones Reglas de Negocio
        if (estado == com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR) {
            if (notaFinal.compareTo(new java.math.BigDecimal("4.00")) < 0 || notaFinal.compareTo(new java.math.BigDecimal("5.50")) > 0) {
                 throw new BusinessLogicException("Para regularizar, la nota debe estar entre 4.00 y 5.50");
            }
        } else if (estado == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO) {
            if (notaFinal.compareTo(new java.math.BigDecimal("6.00")) < 0) {
                 throw new BusinessLogicException("Para promocionar, la nota debe ser 6.00 o superior");
            }
        }

        insc.setNotaFinal(notaFinal);
        insc.setEstado(estado);
        
        if (estado == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO) {
            insc.setFechaPromocion(LocalDate.now());
        }

        insc = inscripcionCursadoRepository.save(insc);
        return inscripcionCursadoMapper.toDTO(insc);
    }

    public List<InscripcionCursadoResponse> obtenerCursadasActuales(UUID idUsuario) {
        return inscripcionCursadoMapper.toDTOs(inscripcionCursadoRepository.findByUsuarioIdAndEstado(idUsuario, com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO));
    }

    @Autowired
    private com.sysacad.backend.mapper.ComisionMapper comisionMapper;



    @Autowired
    private com.sysacad.backend.repository.HorarioCursadoRepository horarioCursadoRepository;
    
    @Autowired
    private com.sysacad.backend.repository.AsignacionMateriaRepository asignacionMateriaRepository;

    public List<com.sysacad.backend.dto.inscripcion_cursado.ComisionDisponibleDTO> obtenerOpcionesInscripcion(UUID idMateria, UUID idUsuario) {
        // 1. Validaciones básicas
        Materia materia = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        
        Usuario alumno = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        if (!correlatividadService.puedeCursar(idUsuario, idMateria)) {
             throw new BusinessLogicException("El alumno no cumple con las correlativas para cursar esta materia.");
        }

        if (inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(idUsuario, idMateria).isPresent()) {
             throw new BusinessLogicException("El alumno ya está inscripto en esta materia.");
        }

        // 2. Buscar comisiones que dicten la materia
        List<Comision> comisiones = comisionRepository.findByMateriasId(idMateria);
        
        List<com.sysacad.backend.dto.inscripcion_cursado.ComisionDisponibleDTO> opciones = new java.util.ArrayList<>();

        // 3. Obtener cursadas activas el alumno para validar superposición
        List<InscripcionCursado> cursadasActivas = inscripcionCursadoRepository.findByUsuarioIdAndEstado(idUsuario, com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO);

        // 4. Para cada comisión, construir el DTO y validar reglas
        for (Comision c : comisiones) {
            com.sysacad.backend.dto.inscripcion_cursado.ComisionDisponibleDTO dto = new com.sysacad.backend.dto.inscripcion_cursado.ComisionDisponibleDTO();
            dto.setIdComision(c.getId());
            dto.setNombreComision(c.getNombre());
            dto.setTurno(c.getTurno());
            
            // Aula
            if (c.getSalon() != null) {
                dto.setUbicacion(c.getSalon().getNombre() + " (" + c.getSalon().getFacultad().getCiudad() + ")");
            } else {
                dto.setUbicacion("Sin asignar");
            }

            // Horarios ESPECIFICOS de la materia en esta comisión
            List<HorarioCursado> horariosMateria = horarioCursadoRepository.findByIdIdComisionAndIdIdMateria(c.getId(), idMateria);
            
            List<String> horariosTexto = horariosMateria.stream()
                    .map(h -> h.getId().getDia() + " " + h.getId().getHoraDesde() + " - " + h.getHoraHasta())
                    .collect(java.util.stream.Collectors.toList());
            dto.setHorarios(horariosTexto);

            // Profesores ESPECIFICOS de la materia en esta comisión
            List<String> profesoresNombres = new java.util.ArrayList<>();
            if (c.getProfesores() != null) {
                for (Usuario p : c.getProfesores()) {
                     com.sysacad.backend.modelo.AsignacionMateria.AsignacionMateriaId asignacionId = 
                        new com.sysacad.backend.modelo.AsignacionMateria.AsignacionMateriaId(p.getId(), idMateria);
                    
                    if (asignacionMateriaRepository.existsById(asignacionId)) {
                        profesoresNombres.add(p.getNombre() + " " + p.getApellido());
                    }
                }
            }
            dto.setProfesores(profesoresNombres);



            // VALIDACIÓN DE SUPERPOSICIÓN
            boolean superposicion = false;
            String motivo = "Disponible para inscripción";

            for (InscripcionCursado inscActiva : cursadasActivas) {
                List<HorarioCursado> horariosActiva = horarioCursadoRepository.findByIdIdComisionAndIdIdMateria(
                        inscActiva.getComision().getId(), 
                        inscActiva.getMateria().getId()
                );
                
                if (verificarSuperposicionHoraria(horariosMateria, horariosActiva)) {
                    superposicion = true;
                    motivo = "Superposición con " + inscActiva.getMateria().getNombre();
                    break;
                }
            }

            if (superposicion) {
                dto.setHabilitada(false);
                dto.setMensaje(motivo);
            } else {
                dto.setHabilitada(true);
                dto.setMensaje("Disponible");
            }
            
            opciones.add(dto);
        }

        return opciones;
    }

    private boolean verificarSuperposicionHoraria(List<HorarioCursado> horariosNuevo, List<HorarioCursado> horariosActivo) {
        for (HorarioCursado hNuevo : horariosNuevo) {
            for (HorarioCursado hActivo : horariosActivo) {
                if (hNuevo.getId().getDia().equals(hActivo.getId().getDia())) {
                    // (StartA < EndB) and (EndA > StartB)
                    if (hNuevo.getId().getHoraDesde().isBefore(hActivo.getHoraHasta()) &&
                        hNuevo.getHoraHasta().isAfter(hActivo.getId().getHoraDesde())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public List<CalificacionCursadaResponse> obtenerCalificaciones(UUID idUsuario, UUID idMateria) {
        InscripcionCursado insc = inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(idUsuario, idMateria)
                .orElseThrow(() -> new ResourceNotFoundException("El alumno no está inscripto en esta materia."));
        
        return calificacionCursadaMapper.toDTOs(insc.getCalificaciones());
    }
}

