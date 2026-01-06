package com.sysacad.backend.service;

import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.InscripcionRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AsignacionMateriaRepository asignacionMateriaRepository;
    private final InscripcionRepository inscripcionRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          AsignacionMateriaRepository asignacionMateriaRepository,
                          InscripcionRepository inscripcionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    public Usuario autenticar(String identificador, String password) {
        Usuario usuario = usuarioRepository.findByLegajoOrMail(identificador, identificador)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        return usuario;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByMail(usuario.getMail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.existsByLegajo(usuario.getLegajo())) {
            throw new RuntimeException("El legajo ya existe");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
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

    @Transactional(readOnly = true)
    public List<Usuario> obtenerDocentesPorMateria(UUID idMateria) {
        return asignacionMateriaRepository.findByIdIdMateria(idMateria).stream()
                .map(AsignacionMateria::getProfesor)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerAlumnosInscriptosPorMateria(UUID idMateria) {
        return inscripcionRepository.findAlumnosByMateria(idMateria);
    }

    @Transactional
    public void eliminarUsuario(UUID id) {
        usuarioRepository.deleteById(id);
    }
}