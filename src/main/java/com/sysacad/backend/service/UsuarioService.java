package com.sysacad.backend.service;

import com.sysacad.backend.dto.UsuarioRequest;
import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.InscripcionRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    // Directorio donde se guardarán las fotos (se crea en la raíz del proyecto)
    private static final String DIRECTORIO_UPLOAD = "uploads/perfiles/";

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

    @Transactional
    public Usuario actualizarUsuario(UUID id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Seguridad: Se permite que cualquier usuario modifique cualquier perfil (según solicitud)
        // validarPermisoModificacion(usuario.getLegajo());

        // Actualizamos campos permitidos (No tocamos Legajo ni Rol por seguridad)
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setDni(request.getDni());
        usuario.setMail(request.getMail());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setCiudad(request.getCiudad());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setGenero(request.getGenero());

        // Permitir actualizar fotoPerfil si viene en el request (ej: string vacío para borrarla o URL externa)
        if (request.getFotoPerfil() != null) {
            usuario.setFotoPerfil(request.getFotoPerfil());
        }

        // Solo actualizamos password si viene en el request
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void subirFoto(UUID id, MultipartFile archivo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Seguridad: Se permite que cualquier usuario suba foto a cualquier perfil
        // validarPermisoModificacion(usuario.getLegajo());

        if (archivo.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        try {
            // 1. Asegurar que el directorio existe
            Path directorioPath = Paths.get(DIRECTORIO_UPLOAD);
            if (!Files.exists(directorioPath)) {
                Files.createDirectories(directorioPath);
            }

            // 2. Extraer extensión original
            String nombreOriginal = archivo.getOriginalFilename();
            String extension = "";
            if (nombreOriginal != null && nombreOriginal.lastIndexOf(".") > 0) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            } else {
                extension = ".jpg"; // Default por si acaso
            }

            // 3. Generar NOMBRE ÚNICO: {Legajo}-{HashAleatorio}.ext
            String hash = UUID.randomUUID().toString().substring(0, 8); // Tomamos los primeros 8 chars del UUID
            String nombreArchivo = usuario.getLegajo() + "-" + hash + extension;

            Path rutaArchivo = directorioPath.resolve(nombreArchivo);

            // 4. (Opcional) Borrar foto anterior si existe para no llenar el disco
            if (usuario.getFotoPerfil() != null) {
                try {
                    // Cuidado: validar que sea un archivo local nuestro antes de borrar
                    Path fotoAnterior = Paths.get(usuario.getFotoPerfil());
                    // Solo borramos si el nombre coincide con nuestro patrón para no borrar defaults
                    if (Files.exists(fotoAnterior) && fotoAnterior.getParent().equals(directorioPath)) {
                        Files.delete(fotoAnterior);
                    }
                } catch (Exception e) {
                    // Ignoramos error al borrar anterior, no es bloqueante
                    System.out.println("No se pudo borrar la foto anterior: " + e.getMessage());
                }
            }

            // 5. Guardar archivo nuevo
            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // 6. Actualizar ruta en BD
            // Guardamos la ruta relativa o absoluta según convenga. Aquí guardamos relativa.
            usuario.setFotoPerfil(rutaArchivo.toString());
            usuarioRepository.save(usuario);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage());
        }
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

    // --- SEGURIDAD ---
    private void validarPermisoModificacion(String legajoObjetivo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String legajoAutenticado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !legajoAutenticado.equals(legajoObjetivo)) {
            throw new RuntimeException("No tiene permisos para modificar este perfil.");
        }
    }
}