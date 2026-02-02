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
    private final com.sysacad.backend.repository.AvisoPersonaRepository avisoPersonaRepository;
    private final com.sysacad.backend.repository.UsuarioRepository usuarioRepository;
    private final com.sysacad.backend.mapper.AvisoMapper avisoMapper;

    @Autowired
    public AvisoService(AvisoRepository avisoRepository, 
                        com.sysacad.backend.repository.AvisoPersonaRepository avisoPersonaRepository,
                        com.sysacad.backend.repository.UsuarioRepository usuarioRepository,
                        com.sysacad.backend.mapper.AvisoMapper avisoMapper) {
        this.avisoRepository = avisoRepository;
        this.avisoPersonaRepository = avisoPersonaRepository;
        this.usuarioRepository = usuarioRepository;
        this.avisoMapper = avisoMapper;
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

    @Transactional(readOnly = true)
    public List<com.sysacad.backend.dto.aviso.AvisoResponse> obtenerUltimosAvisosParaUsuario(java.util.UUID idUsuario) {
        List<Aviso> avisos = obtenerUltimosAvisos();
        List<com.sysacad.backend.dto.aviso.AvisoResponse> responses = avisoMapper.toDTOs(avisos);

        responses.forEach(dto -> { // Ahora dto es AvisoResponse
            boolean leido = avisoPersonaRepository.findById(new com.sysacad.backend.modelo.AvisoPersona.AvisoPersonaId(dto.getId(), idUsuario))
                    .map(ap -> ap.getEstado() == com.sysacad.backend.modelo.enums.EstadoAvisoPersona.LEIDO)
                    .orElse(false);
            dto.setVisto(leido);
        });

        return responses;
    }

    @Transactional
    public void marcarComoLeido(java.util.UUID idAviso, java.util.UUID idUsuario) {
        Aviso aviso = avisoRepository.findById(idAviso)
                .orElseThrow(() -> new RuntimeException("Aviso no encontrado"));
        
        com.sysacad.backend.modelo.Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        com.sysacad.backend.modelo.AvisoPersona.AvisoPersonaId id = new com.sysacad.backend.modelo.AvisoPersona.AvisoPersonaId(idAviso, idUsuario);

        com.sysacad.backend.modelo.AvisoPersona avisoPersona = avisoPersonaRepository.findById(id)
                .orElse(new com.sysacad.backend.modelo.AvisoPersona());
        
        if (avisoPersona.getId() == null) {
            avisoPersona.setId(id);
            avisoPersona.setAviso(aviso);
            avisoPersona.setPersona(usuario);
        }
        
        avisoPersona.setEstado(com.sysacad.backend.modelo.enums.EstadoAvisoPersona.LEIDO);
        
        avisoPersonaRepository.save(avisoPersona);
    }
}