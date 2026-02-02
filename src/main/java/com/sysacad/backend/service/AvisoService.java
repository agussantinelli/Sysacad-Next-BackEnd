package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Aviso;
import com.sysacad.backend.repository.AvisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;

    @Autowired
    public AvisoService(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    @Transactional
    public Aviso publicarAviso(Aviso aviso) {
        aviso.setFechaEmision(LocalDateTime.now());
        aviso.setEstado(com.sysacad.backend.modelo.enums.EstadoAviso.ACTIVO);
        return avisoRepository.save(aviso);
    }

    @Transactional(readOnly = true)
    public List<Aviso> obtenerUltimosAvisos() {
        // Filtrar solo los activos (no ocultos)
        return avisoRepository.findByEstado(com.sysacad.backend.modelo.enums.EstadoAviso.ACTIVO);
    }
}