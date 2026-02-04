package com.sysacad.backend.controller;

import com.sysacad.backend.dto.usuario.UsuarioRequest;
import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.modelo.Matriculacion;
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
    private final com.sysacad.backend.mapper.UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, MatriculacionService matriculacionService, com.sysacad.backend.mapper.UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.matriculacionService = matriculacionService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody UsuarioRequest request) {

        Usuario usuario = usuarioMapper.toEntity(request);
        
        Usuario usuarioManual = new Usuario();
        usuarioManual.setLegajo(request.getLegajo());
        usuarioManual.setPassword(request.getPassword());
        usuarioManual.setTipoDocumento(request.getTipoDocumento());
        usuarioManual.setDni(request.getDni());
        usuarioManual.setNombre(request.getNombre());
        usuarioManual.setApellido(request.getApellido());
        usuarioManual.setMail(request.getMail());
        usuarioManual.setFechaNacimiento(request.getFechaNacimiento());
        usuarioManual.setGenero(request.getGenero());
        usuarioManual.setTelefono(request.getTelefono());
        usuarioManual.setDireccion(request.getDireccion());
        usuarioManual.setCiudad(request.getCiudad());
        usuarioManual.setFotoPerfil(request.getFotoPerfil());
        usuarioManual.setFechaIngreso(request.getFechaIngreso());
        usuarioManual.setTituloAcademico(request.getTituloAcademico());
        usuarioManual.setRol(request.getRol());
        usuarioManual.setEstado(request.getEstado());

        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuarioManual);
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

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable UUID id, @RequestParam com.sysacad.backend.modelo.enums.EstadoUsuario nuevoEstado) {
        Usuario actualizado = usuarioService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(convertirADTO(actualizado));
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
    @PreAuthorize("isAuthenticated()")
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

    @PostMapping("/{id}/cambiar-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cambiarPassword(@PathVariable UUID id,
                                                @RequestBody com.sysacad.backend.dto.usuario.CambioPasswordRequest request,
                                                org.springframework.security.core.Authentication authentication) {
        
        // Validar que el usuario sea el mismo o sea admin
        String legajoAutenticado = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        Usuario u = usuarioService.obtenerPorId(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!isAdmin && !u.getLegajo().equals(legajoAutenticado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        usuarioService.cambiarPassword(id, request.getPasswordActual(), request.getPasswordNueva());
        return ResponseEntity.ok().build();
    }

    private UsuarioResponse convertirADTO(Usuario usuario) {
        UsuarioResponse dto = usuarioMapper.toDTO(usuario);

        if (dto.getFotoPerfil() != null && !dto.getFotoPerfil().isEmpty()) {
            String fotoPath = dto.getFotoPerfil();

            if (!fotoPath.startsWith("http")) {
                // Normalizo Path
                String pathNormalizado = fotoPath.replace("\\", "/");

                // Asegurar que no empiece con slash si vamos a unirlo
                if (pathNormalizado.startsWith("/")) {
                    pathNormalizado = pathNormalizado.substring(1);
                }

                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

                dto.setFotoPerfil(baseUrl + "/" + pathNormalizado);
            }
        }

        if (usuario.getRol() == RolUsuario.ESTUDIANTE) {
            List<Matriculacion> estudios = matriculacionService.obtenerCarrerasPorAlumno(usuario.getId());
            List<UsuarioResponse.InfoCarrera> carrerasInfo = estudios.stream()
                    .map(e -> new UsuarioResponse.InfoCarrera(
                            e.getPlan().getCarrera().getNombre(),
                            e.getPlan().getCarrera().getFacultades().stream()
                                    .findFirst()
                                    .map(f -> f.getCiudad() + ", " + f.getProvincia())
                                    .orElse("Sin Facultad")))
                    .collect(Collectors.toList());
            dto.setCarreras(carrerasInfo);
            estudios.stream()
                    .map(Matriculacion::getFechaInscripcion)
                    .min(LocalDate::compareTo)
                    .ifPresent(fecha -> dto.setAnioIngreso(fecha.getYear()));
        }
        return dto;
    }
}
