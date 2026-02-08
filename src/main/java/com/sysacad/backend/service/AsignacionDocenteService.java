package com.sysacad.backend.service;

import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AsignacionDocenteService {

    private final AsignacionMateriaRepository asignacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AsignacionDocenteService(AsignacionMateriaRepository asignacionRepository, UsuarioRepository usuarioRepository) {
        this.asignacionRepository = asignacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public AsignacionMateria asignarProfesorAMateria(AsignacionMateria asignacion) {
        
        Usuario profe = usuarioRepository.findById(asignacion.getId().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        if (profe.getRol() != RolUsuario.PROFESOR) {
            throw new RuntimeException("El usuario debe ser PROFESOR para dictar una materia");
        }

        return asignacionRepository.save(asignacion);
    }

    @Transactional(readOnly = true)
    public List<AsignacionMateria> listarMateriasDeProfesor(UUID idProfesor) {
        return asignacionRepository.findByIdIdUsuario(idProfesor);
    }

    @Transactional(readOnly = true)
    public List<AsignacionMateria> listarProfesoresDeMateria(UUID idMateria) {
        return asignacionRepository.findByIdIdMateria(idMateria);
    }
}