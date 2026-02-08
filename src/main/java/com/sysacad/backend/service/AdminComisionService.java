package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.*;
import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.dto.salon.SalonResponse;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.modelo.*;
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
    private final CarreraRepository carreraRepository;
    private final HorarioCursadoRepository horarioCursadoRepository;
    private final PlanMateriaRepository planMateriaRepository;
    private final GrupoService grupoService;

    @Autowired
    public AdminComisionService(ComisionRepository comisionRepository,
                                MateriaRepository materiaRepository,
                                SalonRepository salonRepository,
                                UsuarioRepository usuarioRepository,
                                AsignacionMateriaRepository asignacionMateriaRepository,
                                InscripcionCursadoRepository inscripcionCursadoRepository,
                                CarreraRepository carreraRepository,
                                HorarioCursadoRepository horarioCursadoRepository,
                                PlanMateriaRepository planMateriaRepository,
                                GrupoService grupoService) {
        this.comisionRepository = comisionRepository;
        this.materiaRepository = materiaRepository;
        this.salonRepository = salonRepository;
        this.usuarioRepository = usuarioRepository;
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.carreraRepository = carreraRepository;
        this.horarioCursadoRepository = horarioCursadoRepository;
        this.planMateriaRepository = planMateriaRepository;
        this.grupoService = grupoService;
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
                            null, 
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
                comision.getNivel(),
                comision.getCarrera() != null ? comision.getCarrera().getId() : null,
                comision.getCarrera() != null ? comision.getCarrera().getNombre() : null,
                comision.getSalon() != null ? comision.getSalon().getNombre() : "Sin Salón",
                materiasAdmin
        );
    }

    @Transactional
    public void crearComision(ComisionRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la comisión es obligatorio");
        }
        if (request.getTurno() == null || request.getTurno().isBlank()) {
            throw new IllegalArgumentException("El turno de la comisión es obligatorio");
        }
        if (request.getAnio() == null) {
            throw new IllegalArgumentException("El año de la comisión es obligatorio");
        }
        if (request.getNivel() == null) {
            throw new IllegalArgumentException("El nivel de la comisión es obligatorio");
        }
        if (request.getIdCarrera() == null) {
            throw new IllegalArgumentException("La carrera de la comisión es obligatoria");
        }

        String nombre = request.getNombre().trim();
        
        
        if (nombre.matches("^\\d[a-zA-Z]\\d$")) {
            nombre = nombre.toUpperCase();
        }
        
        
        if (comisionRepository.existsByNombre(nombre)) {
            throw new com.sysacad.backend.exception.BusinessLogicException("Ya existe una comisión con el nombre '" + nombre + "'.");
        }

        Comision comision = new Comision();
        comision.setNombre(nombre);
        comision.setTurno(request.getTurno());
        comision.setAnio(request.getAnio());
        comision.setNivel(request.getNivel());
        
        Carrera carrera = carreraRepository.findById(request.getIdCarrera())
                .orElseThrow(() -> new ResourceNotFoundException("Carrera no encontrada con ID: " + request.getIdCarrera()));
        comision.setCarrera(carrera);

        if (request.getIdSalon() != null) {
            Salon salon = salonRepository.findById(request.getIdSalon())
                    .orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado por ID"));
            comision.setSalon(salon);
        } else if (request.getSalon() != null && !request.getSalon().isBlank()) {
             Salon salon = salonRepository.findByNombre(request.getSalon())
                     .orElseThrow(() -> new ResourceNotFoundException("Salón no encontrado por nombre: " + request.getSalon()));
             comision.setSalon(salon);
        } else {
            comision.setSalon(null);
        }

        comisionRepository.save(comision);
        grupoService.crearGruposParaComision(comision);
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
                
                Comision c = h.getComision();
                Materia m = h.getMateria();

                
                c.getProfesores().stream()
                        .filter(p -> asignacionMateriaRepository.existsByIdIdUsuarioAndIdIdMateria(p.getId(), m.getId()))
                        .forEach(p -> busyProfessors.add(p.getId()));
            }
        }
        return busyProfessors;
    }



    @Transactional(readOnly = true)
    public List<ProfesorDisponibleDTO> obtenerProfesoresDisponibles(UUID idMateria, List<AsignarMateriaComisionRequest.HorarioRequestDTO> horarios) {
        
        List<AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdMateria(idMateria);
        List<Usuario> candidatos = asignaciones.stream().map(AsignacionMateria::getProfesor).collect(Collectors.toList());

        
        java.util.Set<UUID> busyProfessors = obtenerProfesoresOcupados(horarios);

        
        return candidatos.stream()
                .filter(p -> !busyProfessors.contains(p.getId()))
                .map(p -> new ProfesorDisponibleDTO(p.getId(), p.getNombre(), p.getApellido(), p.getLegajo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalonResponse> obtenerSalonesDisponibles(String turno, Integer anio) {
        List<Salon> todosSalones = salonRepository.findAll();
        List<Comision> comisionesOcupadas = comisionRepository.findByTurnoIgnoreCaseAndAnio(turno, anio);
        
        java.util.Set<UUID> salonesOcupados = comisionesOcupadas.stream()
                .filter(c -> c.getSalon() != null)
                .map(c -> c.getSalon().getId())
                .collect(Collectors.toSet());
        
        return todosSalones.stream()
                .filter(s -> !salonesOcupados.contains(s.getId()))
                .map(SalonResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MateriaDisponibleDTO> obtenerMateriasDisponibles(UUID idComision) {
        Comision comision = comisionRepository.findById(idComision)
                .orElseThrow(() -> new ResourceNotFoundException("Comisión no encontrada"));
        
        
        List<PlanMateria> planMaterias = planMateriaRepository.findByIdIdCarreraAndNivel(
                comision.getCarrera().getId(), 
                comision.getNivel().shortValue()
        );
        
        
        java.util.Set<UUID> assignedMateriaIds = comision.getMaterias().stream()
                .map(Materia::getId)
                .collect(Collectors.toSet());
        
        
        return planMaterias.stream()
                .map(pm -> materiaRepository.findById(pm.getId().getIdMateria()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .filter(m -> !assignedMateriaIds.contains(m.getId()))
                .map(m -> new MateriaDisponibleDTO(
                        m.getId(),
                        m.getNombre(),
                        null, 
                        planMaterias.stream()
                                .filter(pm -> pm.getId().getIdMateria().equals(m.getId()))
                                .findFirst()
                                .map(PlanMateria::getNivel)
                                .orElse(null),
                        m.getHorasCursado(),
                        m.getCuatrimestreDictado() != null ? m.getCuatrimestreDictado().name() : null,
                        m.getOptativa()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void asignarMateria(UUID idComision, AsignarMateriaComisionRequest request) {
        Comision comision = comisionRepository.findById(idComision)
                .orElseThrow(() -> new ResourceNotFoundException("Comisión no encontrada"));

        Materia materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        
        
        if (comision.getMaterias().contains(materia)) {
            throw new com.sysacad.backend.exception.BusinessLogicException(
                "La materia '" + materia.getNombre() + "' ya está asignada a esta comisión."
            );
        }
        
        
        List<PlanMateria> planMaterias = planMateriaRepository.findByIdIdCarreraAndNivel(
                comision.getCarrera().getId(), 
                comision.getNivel().shortValue()
        );
        boolean isValidSubject = planMaterias.stream()
                .anyMatch(pm -> pm.getId().getIdMateria().equals(materia.getId()));
        
        if (!isValidSubject) {
            throw new com.sysacad.backend.exception.BusinessLogicException(
                "La materia '" + materia.getNombre() + "' no corresponde al nivel " + 
                comision.getNivel() + " de la carrera " + comision.getCarrera().getNombre() + "."
            );
        }
        
        comision.getMaterias().add(materia);
        
        
        if (request.getHorarios() != null && !request.getHorarios().isEmpty()) {
            long totalMinutes = request.getHorarios().stream()
                    .mapToLong(h -> java.time.Duration.between(h.getHoraDesde(), h.getHoraHasta()).toMinutes())
                    .sum();
            
            long totalHours = totalMinutes / 60;
            long requiredHours = materia.getHorasCursado();
            
            if (totalHours != requiredHours) {
                throw new com.sysacad.backend.exception.BusinessLogicException(
                    "La materia '" + materia.getNombre() + "' requiere " + requiredHours + 
                    " horas de cursado, pero se asignaron " + totalHours + " horas en los horarios."
                );
            }
        }
        
        for (UUID idProfesor : request.getIdsProfesores()) {
            Usuario profesor = usuarioRepository.findById(idProfesor)
                    .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado: " + idProfesor));
            
            if (!asignacionMateriaRepository.existsByIdIdUsuarioAndIdIdMateria(idProfesor, materia.getId())) {
                 throw new RuntimeException("El profesor " + profesor.getApellido() + " no está habilitado para dar esta materia.");
            }

            java.util.Set<UUID> busyProfessors = obtenerProfesoresOcupados(request.getHorarios());
            if (busyProfessors.contains(idProfesor)) {
                 throw new RuntimeException("El profesor " + profesor.getNombre() + " " + profesor.getApellido() + " tiene superposición horaria.");
            }
            
            if (!comision.getProfesores().contains(profesor)) {
                 comision.getProfesores().add(profesor);
            }
        }
        
        comisionRepository.save(comision);
        grupoService.crearGruposParaComision(comision);

        
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
