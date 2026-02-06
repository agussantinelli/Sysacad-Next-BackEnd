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
        MesaExamen mesa = new MesaExamen();
        mesa.setNombre(request.getNombre());
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
        
        // Validar disponibilidad del presidente
        if (detalleMesaRepository.existsByProfesorAndFechaAndHora(request.getIdPresidente(), request.getDiaExamen(), request.getHoraExamen())) {
             throw new RuntimeException("El profesor seleccionado ya tiene una mesa asignada en ese horario.");
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
                    
                    // We don't necessarily need to populate details list for this high-level view, 
                    // or user might want it. Based on request "Agrega este datos para mesa dto", 
                    // and typically list views are summary. 
                    // But wait, the user said "endpoint que trae la mesa".
                    // If I leave details null, it's fine for a summary list. 
                    // I'll leave details as null or empty list to avoid N+1 payload bloat unless requested.
                    // Actually, let's map details sparsely or just leave null if not needed.
                    // Given the loop, let's keep it efficient.
                    
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

}
