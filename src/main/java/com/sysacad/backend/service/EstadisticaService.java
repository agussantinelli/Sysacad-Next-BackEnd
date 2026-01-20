package com.sysacad.backend.service;

import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EstadisticaService {

    private final UsuarioRepository usuarioRepository;
    private final CarreraRepository carreraRepository;
    private final MateriaRepository materiaRepository;
    private final InscripcionCursadoRepository inscripcionCursadoRepository;

    @Autowired
    public EstadisticaService(UsuarioRepository usuarioRepository,
            CarreraRepository carreraRepository,
            MateriaRepository materiaRepository,
            InscripcionCursadoRepository inscripcionCursadoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.carreraRepository = carreraRepository;
        this.materiaRepository = materiaRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
    }

    public long contarAlumnosInscriptosTotal() {
        return inscripcionCursadoRepository.count();
    }

    public long contarAlumnosPorMateria(UUID idMateria) {
        return inscripcionCursadoRepository.findByMateriaId(idMateria).size();
    }

    public long contarAlumnosRegularesTotal() {
        return inscripcionCursadoRepository.findAll().stream()
                .filter(i -> i.getEstado().toString().equals("REGULAR"))
                .count();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> obtenerResumenGeneral() {
        Map<String, Long> estadisticas = new HashMap<>();

        // Conteos básicos
        estadisticas.put("total_alumnos", (long) usuarioRepository.findByRol(RolUsuario.ESTUDIANTE).size());
        estadisticas.put("total_profesores", (long) usuarioRepository.findByRol(RolUsuario.PROFESOR).size());
        estadisticas.put("total_carreras", carreraRepository.count());
        estadisticas.put("total_materias", materiaRepository.count());
        estadisticas.put("total_inscripciones_cursado", inscripcionCursadoRepository.count());

        return estadisticas;
    }
}
