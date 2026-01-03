package com.sysacad.backend.service;

import com.sysacad.backend.modelo.EstudioUsuario;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.EstudioUsuarioRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MatriculacionService {

    private final EstudioUsuarioRepository estudioUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public MatriculacionService(EstudioUsuarioRepository estudioUsuarioRepository, UsuarioRepository usuarioRepository) {
        this.estudioUsuarioRepository = estudioUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public EstudioUsuario matricularAlumno(EstudioUsuario estudio) {
        // Validar que el usuario sea ESTUDIANTE
        Usuario alumno = usuarioRepository.findById(estudio.getId().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (alumno.getRol() != RolUsuario.ESTUDIANTE) {
            throw new RuntimeException("Solo se pueden matricular usuarios con rol ESTUDIANTE");
        }

        estudio.setFechaInscripcion(LocalDate.now());
        estudio.setEstado("ACTIVO");
        return estudioUsuarioRepository.save(estudio);
    }

    @Transactional(readOnly = true)
    public List<EstudioUsuario> obtenerCarrerasPorAlumno(UUID idAlumno) {
        return estudioUsuarioRepository.findByIdIdUsuario(idAlumno);
    }
}