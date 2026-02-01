package com.sysacad.backend.service;

import com.sysacad.backend.dto.mesa_examen.MesaExamenRequest;
import com.sysacad.backend.dto.mesa_examen.MesaExamenResponse;
import com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenRequest;
import com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenResponse;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.MesaExamen;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.repository.DetalleMesaExamenRepository;
import com.sysacad.backend.repository.MesaExamenRepository;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MesaExamenService {

    @Autowired
    private MesaExamenRepository mesaExamenRepository;

    @Autowired
    private DetalleMesaExamenRepository detalleMesaExamenRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private com.sysacad.backend.repository.UsuarioRepository usuarioRepository;

    @Autowired
    private com.sysacad.backend.mapper.MesaExamenMapper mesaExamenMapper;

    @Autowired
    private com.sysacad.backend.mapper.DetalleMesaExamenMapper detalleMesaExamenMapper;

    @Autowired
    private com.sysacad.backend.repository.InscripcionCursadoRepository inscripcionCursadoRepository;

    @Autowired
    private com.sysacad.backend.repository.InscripcionExamenRepository inscripcionExamenRepository;

    @Transactional
    public MesaExamenResponse createMesa(MesaExamenRequest request) {
        MesaExamen mesa = mesaExamenMapper.toEntity(request);
        mesa = mesaExamenRepository.save(mesa);
        return mesaExamenMapper.toDTO(mesa);
    }

    @Transactional(readOnly = true)
    public List<MesaExamenResponse> getAllMesas() {
        return mesaExamenRepository.findAll().stream()
                .map(mesa -> {
                    MesaExamenResponse dto = mesaExamenMapper.toDTO(mesa);

                    List<DetalleMesaExamen> detalles = detalleMesaExamenRepository.findByMesaExamenId(mesa.getId());
                    dto.setDetalles(detalleMesaExamenMapper.toDTOs(detalles));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public DetalleMesaExamenResponse addDetalle(DetalleMesaExamenRequest request) {
        MesaExamen mesa = mesaExamenRepository.findById(request.getIdMesaExamen())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa de examen no encontrada con ID: " + request.getIdMesaExamen()));

        Materia materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con ID: " + request.getIdMateria()));

        com.sysacad.backend.modelo.Usuario presidente = usuarioRepository.findById(request.getIdPresidente())
                .orElseThrow(() -> new ResourceNotFoundException("Presidente de mesa no encontrado con ID: " + request.getIdPresidente()));

        List<DetalleMesaExamen> existentes = detalleMesaExamenRepository.findByMesaExamenId(mesa.getId());
        int siguienteNro = existentes.stream()
                .mapToInt(d -> d.getId().getNroDetalle())
                .max()
                .orElse(0) + 1;

        DetalleMesaExamen detalle = detalleMesaExamenMapper.toEntity(request);
        
        detalle.setId(new DetalleMesaExamen.DetalleId(mesa.getId(), siguienteNro));
        detalle.setMesaExamen(mesa);
        detalle.setMateria(materia);
        detalle.setPresidente(presidente);
        if(detalle.getDiaExamen() == null) detalle.setDiaExamen(request.getDiaExamen());
        if(detalle.getHoraExamen() == null) detalle.setHoraExamen(request.getHoraExamen());

        detalle = detalleMesaExamenRepository.save(detalle);
        return detalleMesaExamenMapper.toDTO(detalle);
    }

    @Autowired
    private com.sysacad.backend.service.CorrelatividadService correlatividadService;

    @Transactional(readOnly = true)
    public List<DetalleMesaExamenResponse> getExamenesDisponibles(UUID idAlumno) {
        List<MesaExamen> mesas = mesaExamenRepository.findAll();
        
        return mesas.stream()
            .flatMap(mesa -> detalleMesaExamenRepository.findByMesaExamenId(mesa.getId()).stream())
            .filter(detalle -> {
                return correlatividadService.puedeRendir(idAlumno, detalle.getMateria().getId());
            })
            .map(detalle -> detalleMesaExamenMapper.toDTO(detalle))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DetalleMesaExamenResponse getDetalleMesa(UUID idMesa, Integer nroDetalle) {
        DetalleMesaExamen.DetalleId id = new DetalleMesaExamen.DetalleId(idMesa, nroDetalle);
        DetalleMesaExamen detalle = detalleMesaExamenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa de examen no encontrada"));
        return detalleMesaExamenMapper.toDTO(detalle);
    }

    @Transactional(readOnly = true)
    public List<com.sysacad.backend.dto.mesa_examen.MesaExamenDisponibleDTO> obtenerMesasDisponibles(UUID idMateria, UUID idAlumno) {
        // 1. Validar que la materia exista
        Materia materia = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
        
        // 2. Obtener estado académico del alumno en esa materia
        boolean aproboFinal = inscripcionExamenRepository.existsByUsuarioIdAndMateriaIdAndEstado(
                idAlumno, idMateria, com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO
        );
        
        boolean promociono = inscripcionCursadoRepository.existsByUsuarioIdAndMateriaIdAndEstado(
                idAlumno, idMateria, com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO
        );

        // Si ya aprobó, no debería anotarse
        
        // 3. Obtener todas las mesas donde se rinde esta materia (con FETCH de MesaExamen y Presidente)
        List<DetalleMesaExamen> detalles = detalleMesaExamenRepository.findByMateriaIdWithDetails(idMateria);

        // 4. Mapear a DTO con lógica de habilitación
        return detalles.stream().map(detalle -> {
            com.sysacad.backend.dto.mesa_examen.MesaExamenDisponibleDTO dto = new com.sysacad.backend.dto.mesa_examen.MesaExamenDisponibleDTO();
            
            dto.setIdDetalleMesa(detalle.getMesaExamen().getId()); 
            dto.setNombreMesa(detalle.getMesaExamen().getNombre());
            dto.setFecha(detalle.getDiaExamen());
            dto.setHora(detalle.getHoraExamen());
            dto.setPresidente(detalle.getPresidente() != null ? detalle.getPresidente().getNombre() + " " + detalle.getPresidente().getApellido() : "A definir");
            
            // Lógica de habilitación
            boolean inscripcionExistente = inscripcionExamenRepository.findByUsuarioIdAndDetalleMesaExamenId(idAlumno, detalle.getId()).isPresent();
             
             if (aproboFinal || promociono) {
                 dto.setHabilitada(false);
                 dto.setMensaje("Materia ya aprobada");
             } else if (inscripcionExistente) {
                 dto.setHabilitada(false);
                 dto.setMensaje("Ya inscripto en esta mesa");
             } else {
                 // Chequear regularidad y correlativas para RENDIR
                 boolean puedeRendir = correlatividadService.puedeRendir(idAlumno, idMateria);
                 if (puedeRendir) {
                     dto.setHabilitada(true);
                     dto.setMensaje("Disponible");
                 } else {
                     dto.setHabilitada(false);
                     dto.setMensaje("No cumple correlativas o regularidad");
                 }
             }

            return dto;
        }).collect(Collectors.toList());
    }
}
