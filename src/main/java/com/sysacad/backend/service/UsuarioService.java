package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByMail(usuario.getMail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.existsByLegajo(usuario.getLegajo())) {
            throw new RuntimeException("El legajo ya existe");
        }
        // Aquí iría la encriptación de contraseña en el futuro
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerPorLegajo(String legajo) {
        return usuarioRepository.findByLegajo(legajo);
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Transactional
    public void eliminarUsuario(UUID id) {
        usuarioRepository.deleteById(id);
    }
}