package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.MesaAdminDTO;
import com.sysacad.backend.dto.admin.MesaRequest;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.MesaExamen;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.MesaExamenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminMesaService {

    private final MesaExamenRepository mesaRepository;
    private final com.sysacad.backend.repository.DetalleMesaExamenRepository detalleMesaRepository;
    private final InscripcionExamenRepository inscripcionExamenRepository;
    private final MateriaRepository materiaRepository;
    private final com.sysacad.backend.repository.UsuarioRepository usuarioRepository;

    @Autowired
    public AdminMesaService(MesaExamenRepository mesaRepository,
                            com.sysacad.backend.repository.DetalleMesaExamenRepository detalleMesaRepository,
                            InscripcionExamenRepository inscripcionExamenRepository,
                            MateriaRepository materiaRepository,
                            com.sysacad.backend.repository.UsuarioRepository usuarioRepository,
                            com.sysacad.backend.repository.AsignacionMateriaRepository asignacionMateriaRepository) {
        this.mesaRepository = mesaRepository;
        this.detalleMesaRepository = detalleMesaRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
        this.materiaRepository = materiaRepository;
        this.usuarioRepository = usuarioRepository;
        this.asignacionMateriaRepository = asignacionMateriaRepository;
    }

    private final com.sysacad.backend.repository.AsignacionMateriaRepository asignacionMateriaRepository;

    // Listar MESAS (Detalles) que es lo que usualmente se administra como "Mesas de Examen por Materia"
    @Transactional(readOnly = true)
    public List<MesaAdminDTO> obtenerTodasConEstadisticas() {
        return detalleMesaRepository.findAll().stream()
                .map(detalle -> {
                    long inscriptos = inscripcionExamenRepository.countByDetalleMesaExamenId(detalle.getId());
                    boolean abierta = LocalDateTime.now().isBefore(detalle.getMesaExamen().getFechaFin().atStartOfDay()); 
                    
                    return new MesaAdminDTO(
                            detalle.getMesaExamen().getId(),
                            detalle.getId().getNroDetalle(),
                            detalle.getMateria().getNombre(),
                            detalle.getMesaExamen().getNombre(),
                            detalle.getDiaExamen().atStartOfDay(),
                            inscriptos,
                            abierta
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void crearMesaExamen(com.sysacad.backend.dto.admin.MesaExamenRequest request) {
        String nombre = request.getNombre();
        if (nombre == null || !nombre.trim().toLowerCase().startsWith("turno")) {
            nombre = "Turno " + (nombre == null ? "" : nombre.trim());
        }

        // Validate overlap
        if (mesaRepository.existsByFechaFinAfterAndFechaInicioBefore(request.getFechaInicio(), request.getFechaFin())) {
             throw new RuntimeException("El rango de fechas se superpone con un Turno de Examen existente.");
        }

        MesaExamen mesa = new MesaExamen();
        mesa.setNombre(nombre);
        mesa.setFechaInicio(request.getFechaInicio());
        mesa.setFechaFin(request.getFechaFin());
        mesaRepository.save(mesa);
    }

    @Transactional
    public void editarMesaExamen(UUID id, com.sysacad.backend.dto.admin.MesaExamenRequest request) {
        MesaExamen mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno de examen no encontrado"));

        String nombre = request.getNombre();
        if (nombre == null || !nombre.trim().toLowerCase().startsWith("turno")) {
            nombre = "Turno " + (nombre == null ? "" : nombre.trim());
        }

        // Validate overlap excluding self
        if (mesaRepository.existsByFechaFinAfterAndFechaInicioBeforeAndIdNot(request.getFechaInicio(), request.getFechaFin(), id)) {
             throw new RuntimeException("El rango de fechas se superpone con otro Turno de Examen existente.");
        }

        mesa.setNombre(nombre);
        mesa.setFechaInicio(request.getFechaInicio());
        mesa.setFechaFin(request.getFechaFin());
        mesaRepository.save(mesa);
    }

    @Transactional
    public void agregarDetalleMesa(com.sysacad.backend.dto.admin.DetalleMesaRequest request) {
        MesaExamen mesa = mesaRepository.findById(request.getIdMesaExamen())
                .orElseThrow(() -> new RuntimeException("Mesa de examen (Turno) no encontrada"));
        
        Materia materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
                
        com.sysacad.backend.modelo.Usuario presidente = usuarioRepository.findById(request.getIdPresidente())
                .orElseThrow(() -> new RuntimeException("Presidente no encontrado"));

        com.sysacad.backend.modelo.DetalleMesaExamen detalle = new com.sysacad.backend.modelo.DetalleMesaExamen();
        com.sysacad.backend.modelo.DetalleMesaExamen.DetalleId id = new com.sysacad.backend.modelo.DetalleMesaExamen.DetalleId(
                mesa.getId(),
                detalleMesaRepository.findMaxNroDetalle(mesa.getId()).orElse(0) + 1 // Auto-increment nro_detalle
        );
        
        // Validate availability of president
        if (detalleMesaRepository.existsByProfesorAndFechaAndHora(request.getIdPresidente(), request.getDiaExamen(), request.getHoraExamen())) {
             throw new RuntimeException("El profesor seleccionado ya tiene una mesa asignada en ese horario.");
        }

        // Validate date within Turn range
        if (request.getDiaExamen().isBefore(mesa.getFechaInicio()) || request.getDiaExamen().isAfter(mesa.getFechaFin())) {
            throw new RuntimeException("La fecha del examen (" + request.getDiaExamen() + ") debe estar dentro del rango del Turno (" + mesa.getFechaInicio() + " - " + mesa.getFechaFin() + ").");
        }

        detalle.setId(id);
        detalle.setMesaExamen(mesa);
        detalle.setMateria(materia);
        detalle.setPresidente(presidente);
        detalle.setDiaExamen(request.getDiaExamen());
        detalle.setHoraExamen(request.getHoraExamen());
        
        detalleMesaRepository.save(detalle);
    }

    @Transactional(readOnly = true)
    public List<com.sysacad.backend.dto.admin.ProfesorDisponibleDTO> obtenerProfesoresDisponibles(UUID idMateria, java.time.LocalDate fecha, java.time.LocalTime hora) {
        // 1. Get all qualified professors
        List<com.sysacad.backend.modelo.AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdMateria(idMateria);
        List<com.sysacad.backend.modelo.Usuario> candidatos = asignaciones.stream().map(com.sysacad.backend.modelo.AsignacionMateria::getProfesor).collect(Collectors.toList());

        // 2. Filter out busy professors
        return candidatos.stream()
                .filter(p -> !detalleMesaRepository.existsByProfesorAndFechaAndHora(p.getId(), fecha, hora))
                .map(p -> new com.sysacad.backend.dto.admin.ProfesorDisponibleDTO(p.getId(), p.getNombre(), p.getApellido(), p.getLegajo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.sysacad.backend.dto.mesa_examen.MesaExamenResponse> obtenerTurnos() {
        return mesaRepository.findAll().stream()
                .map(mesa -> {
                    com.sysacad.backend.dto.mesa_examen.MesaExamenResponse response = new com.sysacad.backend.dto.mesa_examen.MesaExamenResponse();
                    response.setId(mesa.getId());
                    response.setNombre(mesa.getNombre());
                    response.setFechaInicio(mesa.getFechaInicio());
                    response.setFechaFin(mesa.getFechaFin());
                    
                    List<com.sysacad.backend.modelo.DetalleMesaExamen> detalles = detalleMesaRepository.findByMesaExamenId(mesa.getId());
                    
                    long totalInscriptos = detalles.stream()
                            .mapToLong(d -> inscripcionExamenRepository.countByDetalleMesaExamenId(d.getId()))
                            .sum();
                    
                    response.setCantidadInscriptos(totalInscriptos);
                    
                    List<com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenResponse> detalleResponses = detalles.stream()
                            .map(d -> {
                                com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenResponse dr = new com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenResponse();
                                dr.setIdMesaExamen(d.getId().getIdMesaExamen());
                                dr.setNroDetalle(d.getId().getNroDetalle());
                                dr.setIdMateria(d.getMateria().getId());
                                dr.setNombreMateria(d.getMateria().getNombre());
                                dr.setIdPresidente(d.getPresidente().getId());
                                dr.setNombrePresidente(d.getPresidente().getNombre() + " " + d.getPresidente().getApellido());
                                dr.setDiaExamen(d.getDiaExamen());
                                dr.setHoraExamen(d.getHoraExamen());
                                return dr;
                            })
                            .collect(Collectors.toList());

                    response.setDetalles(detalleResponses);
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarDetalleMesa(UUID idMesa, Integer nroDetalle) {
        com.sysacad.backend.modelo.DetalleMesaExamen.DetalleId id = new com.sysacad.backend.modelo.DetalleMesaExamen.DetalleId(idMesa, nroDetalle);
        
        if (inscripcionExamenRepository.countByDetalleMesaExamenId(id) > 0) {
            throw new RuntimeException("No se puede eliminar la mesa porque tiene alumnos inscriptos.");
        }
        
        if (!detalleMesaRepository.existsById(id)) {
            throw new RuntimeException("Detalle de mesa no encontrado.");
        }
        detalleMesaRepository.deleteById(id);
    }

    @Transactional
    public void eliminarTurno(UUID idMesaExamen) {
        MesaExamen mesa = mesaRepository.findById(idMesaExamen)
                .orElseThrow(() -> new RuntimeException("Turno de examen no encontrado"));

        List<com.sysacad.backend.modelo.DetalleMesaExamen> detalles = detalleMesaRepository.findByMesaExamenId(idMesaExamen);

        long totalInscriptos = detalles.stream()
                .mapToLong(d -> inscripcionExamenRepository.countByDetalleMesaExamenId(d.getId()))
                .sum();

        if (totalInscriptos > 0) {
            throw new RuntimeException("No se puede eliminar el turno porque tiene alumnos inscriptos en una o más materias.");
        }

        // Si no hay inscriptos, eliminar todos los detalles primero
        detalleMesaRepository.deleteAll(detalles);

        // Finalmente eliminar el turno
        mesaRepository.delete(mesa);
    }

}
