package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Sancion;
import com.sysacad.backend.repository.SancionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SancionService {

    private final SancionRepository sancionRepository;

    @Autowired
    public SancionService(SancionRepository sancionRepository) {
        this.sancionRepository = sancionRepository;
    }

    @Transactional
    public Sancion aplicarSancion(Sancion sancion) {
        return sancionRepository.save(sancion);
    }

    @Transactional(readOnly = true)
    public List<Sancion> listarSancionesUsuario(UUID idUsuario) {
        return sancionRepository.findByUsuarioId(idUsuario);
    }
}