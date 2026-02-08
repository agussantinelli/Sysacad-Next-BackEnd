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
    private final IEmailService emailService;

    @Autowired
    public AvisoService(AvisoRepository avisoRepository, 
                        com.sysacad.backend.repository.AvisoPersonaRepository avisoPersonaRepository,
                        com.sysacad.backend.repository.UsuarioRepository usuarioRepository,
                        com.sysacad.backend.mapper.AvisoMapper avisoMapper,
                        IEmailService emailService) {
        this.avisoRepository = avisoRepository;
        this.avisoPersonaRepository = avisoPersonaRepository;
        this.usuarioRepository = usuarioRepository;
        this.avisoMapper = avisoMapper;
        this.emailService = emailService;
    }

    @Transactional
    public Aviso publicarAviso(Aviso aviso) {
        aviso.setFechaEmision(LocalDateTime.now());
        if (aviso.getEstado() == null) {
            aviso.setEstado(com.sysacad.backend.modelo.enums.EstadoAviso.ACTIVO);
        }
        Aviso guardado = avisoRepository.save(aviso);
        
        
        List<com.sysacad.backend.modelo.Usuario> estudiantes = usuarioRepository.findByRol(com.sysacad.backend.modelo.enums.RolUsuario.ESTUDIANTE);
        for (com.sysacad.backend.modelo.Usuario estudiante : estudiantes) {
            String subject = "Nuevo Aviso en Sysacad: " + aviso.getTitulo();
            String body = "Hola " + estudiante.getNombre() + ",\n\n" +
                          "Se ha publicado un nuevo aviso:\n\n" +
                          aviso.getTitulo() + "\n" +
                          aviso.getDescripcion() + "\n\n" +
                          "Saludos,\nSysacad Team";
            
            emailService.sendEmail(estudiante.getMail(), subject, body);
        }
        
        return guardado;
    }

    @Transactional
    public Aviso cambiarEstadoAviso(java.util.UUID idAviso, com.sysacad.backend.modelo.enums.EstadoAviso nuevoEstado) {
        Aviso aviso = avisoRepository.findById(idAviso)
                .orElseThrow(() -> new RuntimeException("Aviso no encontrado"));
        
        aviso.setEstado(nuevoEstado);
        return avisoRepository.save(aviso);
    }

    @Transactional(readOnly = true)
    public List<Aviso> obtenerUltimosAvisos() {
        
        return avisoRepository.findByEstado(com.sysacad.backend.modelo.enums.EstadoAviso.ACTIVO);
    }

    @Transactional(readOnly = true)
    public List<com.sysacad.backend.dto.aviso.AvisoResponse> obtenerUltimosAvisosParaUsuario(java.util.UUID idUsuario) {
        
        com.sysacad.backend.modelo.Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        boolean esAdmin = usuario != null && usuario.getRol() == com.sysacad.backend.modelo.enums.RolUsuario.ADMIN;

        List<Aviso> avisos;
        if (esAdmin) {
            avisos = avisoRepository.findAllByOrderByFechaEmisionDesc();
        } else {
            avisos = avisoRepository.findByEstadoOrderByFechaEmisionDesc(com.sysacad.backend.modelo.enums.EstadoAviso.ACTIVO);
        }

        List<com.sysacad.backend.dto.aviso.AvisoResponse> responses = avisoMapper.toDTOs(avisos);

        responses.forEach(dto -> { 
            if (esAdmin) {
                dto.setVisto(true);
            } else {
                boolean leido = avisoPersonaRepository.findById(new com.sysacad.backend.modelo.AvisoPersona.AvisoPersonaId(dto.getId(), idUsuario))
                        .map(ap -> ap.getEstado() == com.sysacad.backend.modelo.enums.EstadoAvisoPersona.LEIDO)
                        .orElse(false);
                dto.setVisto(leido);
            }
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


    @Transactional(readOnly = true)
    public long contarAvisosNoLeidos(java.util.UUID idUsuario) {
        com.sysacad.backend.modelo.Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario != null && usuario.getRol() == com.sysacad.backend.modelo.enums.RolUsuario.ADMIN) {
            return 0;
        }
        return avisoRepository.countAvisosNoLeidos(idUsuario);
    }
}