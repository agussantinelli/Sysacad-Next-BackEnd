package com.sysacad.backend.service;

import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.InscripcionRepository;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class EstadisticaService {

    private final UsuarioRepository usuarioRepository;
    private final CarreraRepository carreraRepository;
    private final MateriaRepository materiaRepository;
    private final InscripcionRepository inscripcionRepository;

    @Autowired
    public EstadisticaService(UsuarioRepository usuarioRepository,
                              CarreraRepository carreraRepository,
                              MateriaRepository materiaRepository,
                              InscripcionRepository inscripcionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.carreraRepository = carreraRepository;
        this.materiaRepository = materiaRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> obtenerResumenGeneral() {
        Map<String, Long> estadisticas = new HashMap<>();

        // Conteos básicos usando métodos count() de JPA (son muy rápidos)
        estadisticas.put("total_alumnos", (long) usuarioRepository.findByRol(RolUsuario.ESTUDIANTE).size());
        estadisticas.put("total_profesores", (long) usuarioRepository.findByRol(RolUsuario.PROFESOR).size());
        estadisticas.put("total_carreras", carreraRepository.count());
        estadisticas.put("total_materias", materiaRepository.count());
        estadisticas.put("total_inscripciones", inscripcionRepository.count());

        return estadisticas;
    }
}