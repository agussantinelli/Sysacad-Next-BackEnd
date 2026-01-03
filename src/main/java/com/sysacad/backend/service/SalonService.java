package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Salon;
import com.sysacad.backend.repository.SalonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SalonService {

    private final SalonRepository salonRepository;

    @Autowired
    public SalonService(SalonRepository salonRepository) {
        this.salonRepository = salonRepository;
    }

    @Transactional
    public Salon crearSalon(Salon salon) {
        return salonRepository.save(salon);
    }

    @Transactional(readOnly = true)
    public List<Salon> listarPorFacultad(UUID idFacultad) {
        return salonRepository.findByFacultadId(idFacultad);
    }
}