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

    public CalificacionCursadaResponse cargarNota(UUID idInscripcion, CalificacionCursadaRequest request) {
        InscripcionCursado insc = inscripcionCursadoRepository.findById(idInscripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + idInscripcion));

        CalificacionCursada calif = new CalificacionCursada();
        calif.setInscripcionCursado(insc);
        calif.setDescripcion(request.getDescripcion());
        calif.setNota(request.getNota());
        calif.setFecha(LocalDate.now());

        calif = calificacionCursadaRepository.save(calif);

        return calificacionCursadaMapper.toDTO(calif);

    }

    public List<InscripcionCursadoResponse> obtenerCursadasActuales(UUID idUsuario) {
        return inscripcionCursadoMapper.toDTOs(inscripcionCursadoRepository.findByUsuarioIdAndEstado(idUsuario, com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO));
    }

    @Autowired
    private com.sysacad.backend.mapper.ComisionMapper comisionMapper;

    public List<com.sysacad.backend.dto.comision.ComisionResponse> obtenerComisionesDisponibles(UUID idMateria, UUID idUsuario) {
        // 1. Validar Materia y Usuario
        Materia materia = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        
        Usuario alumno = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        // 2. Validar Correlativas
        if (!correlatividadService.puedeCursar(idUsuario, idMateria)) {
            // Opcional: Lanzar excepción o devolver lista vacía. 
            // Para "disponibles", devolver vacío tiene sentido, pero si el usuario preguntó por ESTA materia, mejor explicar por qué.
            throw new BusinessLogicException("El alumno no cumple con las correlativas para cursar esta materia.");
        }

        // 3. Validar si ya está cursando o aprobada (si aplica)
        boolean yaInscripto = inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(idUsuario, idMateria).isPresent();
        if (yaInscripto) {
             throw new BusinessLogicException("El alumno ya está inscripto en esta materia.");
        }
        
        // 4. Buscar Comisiones que dicten la materia
        List<Comision> comisiones = comisionRepository.findByMateriasId(idMateria);

        // 5. Filtrar (si tuviera cupos, etc) - Por ahora devolvemos todas las que dictan la materia
        return comisionMapper.toDTOs(comisiones);
    }
}

