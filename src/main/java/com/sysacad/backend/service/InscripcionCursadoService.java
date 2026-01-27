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

    public InscripcionCursadoResponse inscribir(InscripcionCursadoRequest request) {

        // 1. Validar Usuario
        Usuario alumno = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con ID: " + request.getIdUsuario()));

        // 2. Validar Materia
        Materia materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con ID: " + request.getIdMateria()));

        // 3. Validar Comision
        Comision comision = comisionRepository.findById(request.getIdComision())
                .orElseThrow(() -> new ResourceNotFoundException("Comisión no encontrada con ID: " + request.getIdComision()));

        // 4. Validar que la Comisión dicte esa materia
        boolean dictaMateria = comision.getMaterias().stream()
                .anyMatch(m -> m.getId().equals(materia.getId()));
        if (!dictaMateria) {
            throw new BusinessLogicException("La comisión seleccionada no dicta la materia indicada.");
        }

        // 5. Validar si ya está inscripto
        if (inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(alumno.getId(), materia.getId()).isPresent()) {
            throw new BusinessLogicException("El alumno ya está inscripto a cursar esta materia.");
        }

        // 6. Crear Inscripción
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

    public CalificacionCursadaResponse cargarNota(UUID idInscripcion, CalificacionCursadaRequest request) {
        InscripcionCursado insc = inscripcionCursadoRepository.findById(idInscripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + idInscripcion));

        CalificacionCursada calif = new CalificacionCursada();
        calif.setInscripcionCursado(insc);
        calif.setDescripcion(request.getDescripcion());
        calif.setNota(request.getNota());
        calif.setFecha(LocalDate.now());

        calif = calificacionCursadaRepository.save(calif);
        // Nota: Si queremos mapear CalificacionCursadaResponse, deberíamos agregar un método al mapper o crear CalificacionCursadaMapper.
        // Por consistencia, por ahora lo dejamos "semi-manual" o definimos un mapper rápido.
        // Pero el usuario pidió refactorizar. Usaré un método auxiliar simple o un mapper interno si es necesario, 
        // pero idealmente InscripcionCursadoMapper maneja la respuesta principal.
        
        // Como eliminamos el metodo manual mapToCalificacionResponse, necesitamos una forma de devolver CalificacionCursadaResponse.
        // Lo correcto: Agregar un CalificacionCursadaMapper o agregarlo a InscripcionCursadoMapper.
        // Voy a asumir que puedo agregarlo a InscripcionCursadoMapper en el siguiente paso o usar un builder aqui para no romper.
        // Sin embargo, para ser limpio, crearé un CalificacionCursadaMapper pronto.
        // Por ahora, lo haré manual aquí para no bloquear y en el siguiente paso lo paso al mapper si es posible.
        
        CalificacionCursadaResponse dto = new CalificacionCursadaResponse();
        dto.setId(calif.getId());
        dto.setDescripcion(calif.getDescripcion());
        dto.setNota(calif.getNota());
        dto.setFecha(calif.getFecha());
        return dto;
    }

    public List<InscripcionCursadoResponse> obtenerCursadasActuales(UUID idUsuario) {
        return inscripcionCursadoMapper.toDTOs(inscripcionCursadoRepository.findByUsuarioIdAndEstado(idUsuario, com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO));
    }
}

