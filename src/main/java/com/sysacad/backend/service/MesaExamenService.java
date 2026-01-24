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
                    // Cargar detalles manualmente si el mapper no lo hace por FetchType.LAZY o si MesaExamenMapper ignoraba detalles
                    // En mi definicion de mapper, MesaExamenMapper usa DetalleMesaExamenMapper pero la relación en Entity debe estar bien.
                    // Si MesaExamen tiene lista de DetalleMesaExamen, y el mapper lo mapea, genial.
                    // Pero `mapToResponse` anterior hacia una consulta extra: detalleMesaExamenRepository.findByMesaExamenId(mesa.getId())
                    // Si la entidad MesaExamen no tiene la lista mapeada (OneToMany), entonces el mapper no puede hacerlo solo magicamente.
                    // Revisando MesaExamen.java en memoria: no recuerdo si tiene @OneToMany mappedBy.
                    // Si no tiene, debo setear los detalles aqui.
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

        // El mapper para entity ignora ID compuesto y objs.
        // Usamos el entity directo o mapper ignorando esos campos.
        DetalleMesaExamen detalle = detalleMesaExamenMapper.toEntity(request);
        
        detalle.setId(new DetalleMesaExamen.DetalleId(mesa.getId(), request.getNroDetalle()));
        detalle.setMesaExamen(mesa);
        detalle.setMateria(materia);
        detalle.setPresidente(presidente);
        // dia y hora mapeados por mapper si coinciden
        if(detalle.getDiaExamen() == null) detalle.setDiaExamen(request.getDiaExamen());
        if(detalle.getHoraExamen() == null) detalle.setHoraExamen(request.getHoraExamen());

        detalle = detalleMesaExamenRepository.save(detalle);
        return detalleMesaExamenMapper.toDTO(detalle);
    }

}
