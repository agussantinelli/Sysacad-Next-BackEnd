package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.*;
import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.model.enums.DiaSemana;
import com.sysacad.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminComisionService {

    private final ComisionRepository comisionRepository;
    private final MateriaRepository materiaRepository;
    private final SalonRepository salonRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsignacionMateriaRepository asignacionMateriaRepository;
    private final InscripcionCursadoRepository inscripcionCursadoRepository;
    private final HorarioCursadoRepository horarioCursadoRepository;

    @Autowired
    public AdminComisionService(ComisionRepository comisionRepository,
                                MateriaRepository materiaRepository,
                                SalonRepository salonRepository,
                                UsuarioRepository usuarioRepository,
                                AsignacionMateriaRepository asignacionMateriaRepository,
                                InscripcionCursadoRepository inscripcionCursadoRepository,
                                HorarioCursadoRepository horarioCursadoRepository) {
        this.comisionRepository = comisionRepository;
        this.materiaRepository = materiaRepository;
        this.salonRepository = salonRepository;
        this.usuarioRepository = usuarioRepository;
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.horarioCursadoRepository = horarioCursadoRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminComisionDTO> obtenerTodasConDetalle() {
        return comisionRepository.findAll().stream()
                .map(this::mapToAdminComisionDTO)
                .collect(Collectors.toList());
    }

    private AdminComisionDTO mapToAdminComisionDTO(Comision comision) {
        List<AdminMateriaComisionDTO> materiasAdmin = comision.getMaterias().stream()
                .map(materia -> {
                    List<InscripcionCursado> inscriptos = inscripcionCursadoRepository.findByComisionIdAndMateriaId(comision.getId(), materia.getId());
                    
                    List<AlumnoResumenDTO> alumnos = inscriptos.stream()
                            .map(i -> {
                                Usuario u = i.getUsuario();
                                return new AlumnoResumenDTO(u.getId(), u.getNombre(), u.getApellido(), u.getLegajo());
                            })
                            .collect(Collectors.toList());

                    List<ProfesorResumenDTO> profesores = comision.getProfesores().stream()
                            .filter(p -> asignacionMateriaRepository.existsByIdIdUsuarioAndIdIdMateria(p.getId(), materia.getId()))
                            .map(p -> new ProfesorResumenDTO(p.getId(), p.getNombre(), p.getApellido(), p.getLegajo() != null ? p.getLegajo() : ""))
                            .collect(Collectors.toList());

                    return new AdminMateriaComisionDTO(
                            materia.getId(),
                            materia.getNombre(),
                            materia.getNivel(),
                            (long) inscriptos.size(),
                            alumnos,
                            profesores
                    );
                })
                .collect(Collectors.toList());

        return new AdminComisionDTO(
                comision.getId(),
                comision.getNombre(),
                comision.getTurno(),
                comision.getAnio(),
                comision.getSalon() != null ? comision.getSalon().getNombre() : "Sin Salón",
                materiasAdmin
        );
    }

    @Transactional
    public void crearComision(ComisionRequest request) {
        Comision comision = new Comision();
        comision.setNombre(request.getNombre());
        comision.setTurno(request.getTurno());
        comision.setAnio(request.getAnio());

        if (request.getIdSalon() != null) {
            Salon salon = salonRepository.findById(request.getIdSalon())
                    .orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado"));
            comision.setSalon(salon);
        }

        comisionRepository.save(comision);
    }

    private java.util.Set<UUID> obtenerProfesoresOcupados(List<AsignarMateriaComisionRequest.HorarioRequestDTO> horarios) {
        java.util.Set<UUID> busyProfessors = new java.util.HashSet<>();

        for (AsignarMateriaComisionRequest.HorarioRequestDTO horario : horarios) {
            List<HorarioCursado> solapamientos = horarioCursadoRepository.encontrarSolapamientosGlobal(
                    com.sysacad.backend.modelo.enums.DiaSemana.valueOf(horario.getDia().name()),
                    horario.getHoraDesde(),
                    horario.getHoraHasta()
            );

            for (HorarioCursado h : solapamientos) {
                // If a schedule overlaps, logic says: professors in that commission teaching that subject are busy.
                Comision c = h.getComision();
                Materia m = h.getMateria();

                // Get professors in that commission who are assigned to that subject
                c.getProfesores().stream()
                        .filter(p -> asignacionMateriaRepository.existsByIdIdUsuarioAndIdIdMateria(p.getId(), m.getId()))
                        .forEach(p -> busyProfessors.add(p.getId()));
            }
        }
        return busyProfessors;
    }

    @Transactional(readOnly = true)
    public List<ProfesorDisponibleDTO> obtenerProfesoresDisponibles(UUID idMateria, List<AsignarMateriaComisionRequest.HorarioRequestDTO> horarios) {
        // 1. Get all qualified professors
        List<AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdMateria(idMateria);
        List<Usuario> candidatos = asignaciones.stream().map(AsignacionMateria::getProfesor).collect(Collectors.toList());

        // 2. Identify busy professors
        java.util.Set<UUID> busyProfessors = obtenerProfesoresOcupados(horarios);

        // 3. Filter candidates
        return candidatos.stream()
                .filter(p -> !busyProfessors.contains(p.getId()))
                .map(p -> new ProfesorDisponibleDTO(p.getId(), p.getNombre(), p.getApellido(), p.getLegajo()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void asignarMateria(UUID idComision, AsignarMateriaComisionRequest request) {
        Comision comision = comisionRepository.findById(idComision)
                .orElseThrow(() -> new ResourceNotFoundException("Comisión no encontrada"));

        Materia materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        
        // Add Materia to Comision if not present
        if (!comision.getMaterias().contains(materia)) {
             comision.getMaterias().add(materia);
        }

        // Add Professors
        for (UUID idProfesor : request.getIdsProfesores()) {
            Usuario profesor = usuarioRepository.findById(idProfesor)
                    .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado: " + idProfesor));
            
            // Validate qualification
            if (!asignacionMateriaRepository.existsByIdIdUsuarioAndIdIdMateria(idProfesor, materia.getId())) {
                 throw new RuntimeException("El profesor " + profesor.getApellido() + " no está habilitado para dar esta materia.");
            }

            // Validar disponibilidad (Check availability again closely)
            // Ideally we repeat the logic from obtenerProfesoresDisponibles for these specific professors.
            java.util.Set<UUID> busyProfessors = obtenerProfesoresOcupados(request.getHorarios());
            if (busyProfessors.contains(idProfesor)) {
                 throw new RuntimeException("El profesor " + profesor.getNombre() + " " + profesor.getApellido() + " tiene superposición horaria.");
            }
            
            if (!comision.getProfesores().contains(profesor)) {
                 comision.getProfesores().add(profesor);
            }
        }
        
        comisionRepository.save(comision);

        // Save Schedules
        for (AsignarMateriaComisionRequest.HorarioRequestDTO hDTO : request.getHorarios()) {
            HorarioCursado.HorarioCursadoId hId = new HorarioCursado.HorarioCursadoId();
            hId.setIdComision(comision.getId());
            hId.setIdMateria(materia.getId());
            hId.setDia(com.sysacad.backend.modelo.enums.DiaSemana.valueOf(hDTO.getDia().name()));
            hId.setHoraDesde(hDTO.getHoraDesde());

            HorarioCursado horario = new HorarioCursado();
            horario.setId(hId);
            horario.setHoraHasta(hDTO.getHoraHasta());
            horario.setComision(comision);
            horario.setMateria(materia);
            
            horarioCursadoRepository.save(horario);
        }
    }

}
