package com.sysacad.backend.service;

import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FacultadService {

    private final FacultadRegionalRepository facultadRepository;

    @Autowired
    public FacultadService(FacultadRegionalRepository facultadRepository) {
        this.facultadRepository = facultadRepository;
    }

    @Transactional
    public FacultadRegional crearFacultad(FacultadRegional facultad) {
        return facultadRepository.save(facultad);
    }

    @Transactional(readOnly = true)
    public List<FacultadRegional> listarTodas() {
        return facultadRepository.findAll();
    }
}