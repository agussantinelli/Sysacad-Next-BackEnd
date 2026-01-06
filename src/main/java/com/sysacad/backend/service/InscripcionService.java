package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Calificacion;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Inscripcion;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.CalificacionRepository;
import com.sysacad.backend.repository.ComisionRepository;
import com.sysacad.backend.repository.InscripcionRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CalificacionRepository calificacionRepository;
    private final ComisionRepository comisionRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public InscripcionService(InscripcionRepository inscripcionRepository,
                              CalificacionRepository calificacionRepository,
                              ComisionRepository comisionRepository,
                              UsuarioRepository usuarioRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.calificacionRepository = calificacionRepository;
        this.comisionRepository = comisionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Inscripcion inscribirAlumno(Inscripcion inscripcion) {
        // 1. VALIDACIÓN DE SEGURIDAD (Identidad)
        validarIdentidadEstudiante(inscripcion.getId().getIdUsuario());

        // 2. Establecer fecha de sistema
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        return inscripcionRepository.save(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<Inscripcion> obtenerHistorialAlumno(UUID idAlumno) {
        // 1. VALIDACIÓN DE SEGURIDAD (Privacidad)
        validarIdentidadEstudiante(idAlumno);

        return inscripcionRepository.findByIdIdUsuario(idAlumno);
    }

    @Transactional
    public void cargarNota(Calificacion calificacion) {
        // 1. Validar existencia de la inscripción
        Inscripcion inscripcion = inscripcionRepository.findById(calificacion.getId().toInscripcionId())
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        // 2. Obtener la comisión asociada para verificar permisos
        Comision comision = comisionRepository.findById(inscripcion.getId().getIdComision())
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

        // 3. VALIDACIÓN DE SEGURIDAD (Permiso Docente)
        validarPermisoCargaNota(comision);

        calificacionRepository.save(calificacion);
    }

    private void validarIdentidadEstudiante(UUID idAlumnoObjetivo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return;

        String legajoUsuario = auth.getName();
        Usuario usuarioLogueado = usuarioRepository.findByLegajo(legajoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuarioLogueado.getId().equals(idAlumnoObjetivo)) {
            throw new RuntimeException("Acceso denegado: No puede operar sobre datos de otro estudiante.");
        }
    }

    private void validarPermisoCargaNota(Comision comision) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return;

        String legajoUsuario = auth.getName();
        Usuario profesorLogueado = usuarioRepository.findByLegajo(legajoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificamos si el profesor está en la lista de profesores de la comisión
        boolean esProfesorDeComision = comision.getProfesores().stream()
                .anyMatch(p -> p.getId().equals(profesorLogueado.getId()));

        if (!esProfesorDeComision) {
            throw new RuntimeException("Acceso denegado: Solo los profesores asignados a esta comisión pueden cargar notas.");
        }
    }
}