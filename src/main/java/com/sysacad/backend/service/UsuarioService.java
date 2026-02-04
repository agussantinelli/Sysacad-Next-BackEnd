package com.sysacad.backend.service;

import com.sysacad.backend.dto.usuario.UsuarioRequest;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AsignacionMateriaRepository asignacionMateriaRepository;
    private final InscripcionCursadoRepository inscripcionCursadoRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AsignacionMateriaRepository asignacionMateriaRepository,
            InscripcionCursadoRepository inscripcionCursadoRepository,
            FileStorageService fileStorageService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.fileStorageService = fileStorageService;
    }

    public Usuario autenticar(String identificador, String password) {
        Usuario usuario = usuarioRepository.findByLegajoOrMail(identificador, identificador)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con identificador: " + identificador));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new BusinessLogicException("Contraseña incorrecta");
        }
        return usuario;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByMail(usuario.getMail())) {
            throw new BusinessLogicException("El email ya está registrado");
        }
        if (usuarioRepository.existsByLegajo(usuario.getLegajo())) {
            throw new BusinessLogicException("El legajo ya existe");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuario(UUID id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setDni(request.getDni());
        usuario.setMail(request.getMail());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setCiudad(request.getCiudad());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setGenero(request.getGenero());

        if (request.getFotoPerfil() != null) {
            // Si viene una URL externa o vacío, actualizamos.
            usuario.setFotoPerfil(request.getFotoPerfil());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void subirFoto(UUID id, MultipartFile archivo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Delegamos TODA la lógica de archivos al servicio especializado
        String rutaGuardada = fileStorageService.guardarFotoPerfil(
                archivo,
                usuario.getLegajo(),
                usuario.getFotoPerfil());

        usuario.setFotoPerfil(rutaGuardada);
        usuarioRepository.save(usuario);
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
        // Obtenemos inscripciones cursado para la materia
        return inscripcionCursadoRepository.findByMateriaId(idMateria).stream()
                .map(com.sysacad.backend.modelo.InscripcionCursado::getUsuario)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Antes de eliminar el usuario, limpiamos su foto del disco para no dejar basura
        fileStorageService.borrarArchivo(usuario.getFotoPerfil());
        usuarioRepository.deleteById(id);
    }

    private void validarPermisoModificacion(String legajoObjetivo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String legajoAutenticado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !legajoAutenticado.equals(legajoObjetivo)) {
            throw new BusinessLogicException("No tiene permisos para modificar este perfil.");
        }
    }
    @Transactional
    public void cambiarPassword(UUID idUsuario, String oldPassword, String newPassword) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
            throw new BusinessLogicException("La contraseña actual es incorrecta.");
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }
}