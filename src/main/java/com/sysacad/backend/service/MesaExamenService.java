package com.sysacad.backend.service;

import com.sysacad.backend.dto.*;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.MesaExamen;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.repository.DetalleMesaExamenRepository;
import com.sysacad.backend.repository.MesaExamenRepository;
import com.sysacad.backend.repository.MateriaRepository;
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

    @Transactional
    public MesaExamenResponse createMesa(MesaExamenRequest request) {
        MesaExamen mesa = new MesaExamen();
        mesa.setNombre(request.getNombre());
        mesa.setFechaInicio(request.getFechaInicio());
        mesa.setFechaFin(request.getFechaFin());

        mesa = mesaExamenRepository.save(mesa);
        return mapToResponse(mesa);
    }

    @Transactional(readOnly = true)
    public List<MesaExamenResponse> getAllMesas() {
        return mesaExamenRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DetalleMesaExamenResponse addDetalle(DetalleMesaExamenRequest request) {
        MesaExamen mesa = mesaExamenRepository.findById(request.getIdMesaExamen())
                .orElseThrow(() -> new RuntimeException("Mesa de examen no encontrada"));

        Materia materia = materiaRepository.findById(request.getIdMateria())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        DetalleMesaExamen detalle = new DetalleMesaExamen();
        detalle.setMesaExamen(mesa);
        detalle.setMateria(materia);
        detalle.setDiaExamen(request.getDiaExamen());
        detalle.setHoraExamen(request.getHoraExamen());

        detalle = detalleMesaExamenRepository.save(detalle);
        return mapToResponse(detalle);
    }

    private MesaExamenResponse mapToResponse(MesaExamen mesa) {
        MesaExamenResponse response = new MesaExamenResponse();
        response.setId(mesa.getId());
        response.setNombre(mesa.getNombre());
        response.setFechaInicio(mesa.getFechaInicio());
        response.setFechaFin(mesa.getFechaFin());

        // Cargar detalles
        List<DetalleMesaExamen> detalles = detalleMesaExamenRepository.findByMesaExamenId(mesa.getId());
        response.setDetalles(detalles.stream().map(this::mapToResponse).collect(Collectors.toList()));

        return response;
    }

    private DetalleMesaExamenResponse mapToResponse(DetalleMesaExamen detalle) {
        DetalleMesaExamenResponse response = new DetalleMesaExamenResponse();
        response.setId(detalle.getId());
        response.setNombreMateria(detalle.getMateria().getNombre());
        response.setIdMateria(detalle.getMateria().getId());
        response.setDiaExamen(detalle.getDiaExamen());
        response.setHoraExamen(detalle.getHoraExamen());
        return response;
    }
}
