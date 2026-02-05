package com.sysacad.backend.service;

import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.dto.materia.SimpleMateriaDTO;
import com.sysacad.backend.mapper.CarreraMapper;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminGeneralService {

    private final CarreraRepository carreraRepository;
    private final MateriaRepository materiaRepository;
    private final CarreraMapper carreraMapper;

    @Autowired
    public AdminGeneralService(CarreraRepository carreraRepository,
                               MateriaRepository materiaRepository,
                               CarreraMapper carreraMapper) {
        this.carreraRepository = carreraRepository;
        this.materiaRepository = materiaRepository;
        this.carreraMapper = carreraMapper;
    }

    @Transactional(readOnly = true)
    public List<CarreraResponse> obtenerTodasLasCarreras() {
        return carreraRepository.findAll().stream()
                .map(carreraMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SimpleMateriaDTO> buscarMateriasPorCarreraYNombre(UUID idCarrera, String nombre) {
        return materiaRepository.findByCarreraIdAndNombreExcluding(idCarrera, nombre).stream()
                .map(m -> new SimpleMateriaDTO(m.getId(), m.getNombre()))
                .collect(Collectors.toList());
    }
}
