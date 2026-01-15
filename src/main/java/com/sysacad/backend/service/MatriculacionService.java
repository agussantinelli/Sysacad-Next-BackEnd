package com.sysacad.backend.service;

import com.sysacad.backend.dto.CarreraMateriasDTO;
import com.sysacad.backend.dto.MateriaResponse;
import com.sysacad.backend.modelo.EstudioUsuario;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.EstudioUsuarioRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MatriculacionService {

    private final EstudioUsuarioRepository estudioUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public MatriculacionService(EstudioUsuarioRepository estudioUsuarioRepository,
                                UsuarioRepository usuarioRepository) {
        this.estudioUsuarioRepository = estudioUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public EstudioUsuario matricularAlumno(EstudioUsuario estudio) {
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

    @Transactional(readOnly = true)
    public List<CarreraMateriasDTO> obtenerMateriasPorCarreraDelAlumno(String legajo) {
        // 1. Obtener usuario real
        Usuario alumno = usuarioRepository.findByLegajo(legajo)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con legajo: " + legajo));

        // 2. Obtener inscripciones (EstudioUsuario)
        List<EstudioUsuario> inscripciones = estudioUsuarioRepository.findByIdIdUsuario(alumno.getId());
        List<CarreraMateriasDTO> resultado = new ArrayList<>();

        // 3. Iterar sobre cada carrera en la que está inscripto
        for (EstudioUsuario inscripcion : inscripciones) {

            // Navegación JPA Real: Estudio -> Plan -> Carrera
            PlanDeEstudio plan = inscripcion.getPlan();

            if (plan != null) {
                // Datos de la carrera y plan
                String idCarrera = plan.getCarrera().getId().getIdCarrera();
                String nombreCarrera = plan.getCarrera().getNombre();
                String nombrePlan = plan.getId().getNombre();

                // Navegación JPA Real: Plan -> Materias
                List<Materia> materiasDelPlan = plan.getMaterias();

                // Mapeo a DTO
                List<MateriaResponse.SimpleMateriaDTO> materiasDTO = materiasDelPlan.stream()
                        .map(m -> new MateriaResponse.SimpleMateriaDTO(m.getId(), m.getNombre()))
                        .collect(Collectors.toList());

                resultado.add(new CarreraMateriasDTO(idCarrera, nombreCarrera, nombrePlan, materiasDTO));
            }
        }

        return resultado;
    }
}