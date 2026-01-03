package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Calificacion;
import com.sysacad.backend.modelo.Inscripcion;
import com.sysacad.backend.repository.CalificacionRepository;
import com.sysacad.backend.repository.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CalificacionRepository calificacionRepository;

    @Autowired
    public InscripcionService(InscripcionRepository inscripcionRepository,
                              CalificacionRepository calificacionRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.calificacionRepository = calificacionRepository;
    }

    @Transactional
    public Inscripcion inscribirAlumno(Inscripcion inscripcion) {
        inscripcion.setFechaInscripcion(LocalDateTime.now());
        return inscripcionRepository.save(inscripcion);
    }

    @Transactional(readOnly = true)
    public List<Inscripcion> obtenerHistorialAlumno(UUID idAlumno) {
        return inscripcionRepository.findByIdIdUsuario(idAlumno);
    }

    @Transactional
    public void cargarNota(Calificacion calificacion) {
        calificacionRepository.save(calificacion);
    }
}