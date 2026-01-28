package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.repository.CarreraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CarreraService {

    private final CarreraRepository carreraRepository;

    @Autowired
    public CarreraService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Transactional
    public Carrera registrarCarrera(Carrera carrera) {
        return carreraRepository.save(carrera);
    }

    @Transactional(readOnly = true)
    public List<Carrera> listarCarrerasPorFacultad(UUID idFacultad) {
        return carreraRepository.findByFacultades_Id(idFacultad);
    }
}