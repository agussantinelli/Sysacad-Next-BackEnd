package com.sysacad.backend.controller;

import com.sysacad.backend.dto.UsuarioRequest;
import com.sysacad.backend.dto.UsuarioResponse;
import com.sysacad.backend.modelo.EstudioUsuario;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.service.MatriculacionService;
import com.sysacad.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MatriculacionService matriculacionService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, MatriculacionService matriculacionService) {
        this.usuarioService = usuarioService;
        this.matriculacionService = matriculacionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setLegajo(request.getLegajo());
        usuario.setPassword(request.getPassword());
        usuario.setTipoDocumento(request.getTipoDocumento());
        usuario.setDni(request.getDni());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setMail(request.getMail());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setGenero(request.getGenero());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setCiudad(request.getCiudad());
        usuario.setFotoPerfil(request.getFotoPerfil());
        usuario.setFechaIngreso(request.getFechaIngreso());
        usuario.setTituloAcademico(request.getTituloAcademico());
        usuario.setRol(request.getRol());
        usuario.setEstado(request.getEstado());

        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
        return new ResponseEntity<>(convertirADTO(nuevoUsuario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // La validación fina se hace en el servicio
    public ResponseEntity<UsuarioResponse> actualizarUsuario(@PathVariable UUID id,
            @RequestBody UsuarioRequest request) {
        try {
            Usuario actualizado = usuarioService.actualizarUsuario(id, request);
            return ResponseEntity.ok(convertirADTO(actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // O Forbidden según el error
        }
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> subirFotoPerfil(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        try {
            usuarioService.subirFoto(id, file);
            return ResponseEntity.ok("Foto subida exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir foto: " + e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> obtenerTodos(@RequestParam(required = false) RolUsuario rol) {
        List<Usuario> usuarios;
        if (rol != null) {
            usuarios = usuarioService.obtenerPorRol(rol);
        } else {
            usuarios = usuarioService.obtenerTodos();
        }
        return ResponseEntity.ok(usuarios.stream().map(this::convertirADTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Permitimos a cualquiera autenticado intentar, el servicio o lógica UI filtra
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable UUID id) {
        return usuarioService.obtenerPorId(id)
                .map(u -> ResponseEntity.ok(convertirADTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/legajo/{legajo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<UsuarioResponse> obtenerPorLegajo(@PathVariable String legajo) {
        return usuarioService.obtenerPorLegajo(legajo)
                .map(u -> ResponseEntity.ok(convertirADTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/materia/{idMateria}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<List<UsuarioResponse>> obtenerPorMateria(@PathVariable UUID idMateria) {
        List<Usuario> docentes = usuarioService.obtenerDocentesPorMateria(idMateria);
        return ResponseEntity.ok(docentes.stream().map(this::convertirADTO).collect(Collectors.toList()));
    }

    @GetMapping("/alumnos/materia/{idMateria}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<List<UsuarioResponse>> obtenerAlumnosInscriptosPorMateria(@PathVariable UUID idMateria) {
        List<Usuario> alumnos = usuarioService.obtenerAlumnosInscriptosPorMateria(idMateria);
        return ResponseEntity.ok(alumnos.stream().map(this::convertirADTO).collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    private UsuarioResponse convertirADTO(Usuario usuario) {
        UsuarioResponse dto = new UsuarioResponse(usuario);

        // Logica para URL absoluta de la foto
        if (dto.getFotoPerfil() != null && !dto.getFotoPerfil().isEmpty()) {
            String fotoPath = dto.getFotoPerfil();
            // Si no es una URL externa (http/https), asumimos que es local
            if (!fotoPath.startsWith("http")) {
                // Normalizar separadores (Windows usa backslash, URL necesita slash)
                String pathNormalizado = fotoPath.replace("\\", "/");

                // Asegurar que no empiece con slash si vamos a unirlo
                if (pathNormalizado.startsWith("/")) {
                    pathNormalizado = pathNormalizado.substring(1);
                }

                // Obtener URL base actual (ej: http://localhost:8080)
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

                dto.setFotoPerfil(baseUrl + "/" + pathNormalizado);
            }
        }

        if (usuario.getRol() == RolUsuario.ESTUDIANTE) {
            List<EstudioUsuario> estudios = matriculacionService.obtenerCarrerasPorAlumno(usuario.getId());
            List<UsuarioResponse.InfoCarrera> carrerasInfo = estudios.stream()
                    .map(e -> new UsuarioResponse.InfoCarrera(
                            e.getPlan().getCarrera().getNombre(),
                            e.getPlan().getCarrera().getFacultad().getCiudad() + ", " +
                                    e.getPlan().getCarrera().getFacultad().getProvincia()))
                    .collect(Collectors.toList());
            dto.setCarreras(carrerasInfo);
            estudios.stream()
                    .map(EstudioUsuario::getFechaInscripcion)
                    .min(LocalDate::compareTo)
                    .ifPresent(fecha -> dto.setAnioIngreso(fecha.getYear()));
        }
        return dto;
    }
}