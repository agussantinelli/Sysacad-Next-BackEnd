package com.sysacad.backend.controller;

import com.sysacad.backend.dto.UsuarioResponse;
import com.sysacad.backend.modelo.EstudioUsuario;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.service.MatriculacionService;
import com.sysacad.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UsuarioResponse> crearUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
        return new ResponseEntity<>(new UsuarioResponse(nuevoUsuario), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> obtenerTodos(@RequestParam(required = false) RolUsuario rol) {
        List<Usuario> usuarios;

        if (rol != null) {
            usuarios = usuarioService.obtenerPorRol(rol);
        } else {
            usuarios = usuarioService.obtenerTodos();
        }

        List<UsuarioResponse> response = usuarios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable UUID id) {
        return usuarioService.obtenerPorId(id)
                .map(u -> ResponseEntity.ok(convertirADTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/legajo/{legajo}")
    public ResponseEntity<UsuarioResponse> obtenerPorLegajo(@PathVariable String legajo) {
        return usuarioService.obtenerPorLegajo(legajo)
                .map(u -> ResponseEntity.ok(convertirADTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // --- LÓGICA DE ENRIQUECIMIENTO ---
    private UsuarioResponse convertirADTO(Usuario usuario) {
        UsuarioResponse dto = new UsuarioResponse(usuario);

        if (usuario.getRol() == RolUsuario.ESTUDIANTE) {
            // Traemos todas las carreras que cursa
            List<EstudioUsuario> estudios = matriculacionService.obtenerCarrerasPorAlumno(usuario.getId());

            // 1. Mapeamos info visual
            List<UsuarioResponse.InfoCarrera> carrerasInfo = estudios.stream()
                    .map(e -> {
                        String nombreCarrera = e.getPlan().getCarrera().getNombre();
                        String facultad = e.getPlan().getCarrera().getFacultad().getCiudad() + ", " +
                                e.getPlan().getCarrera().getFacultad().getProvincia();
                        return new UsuarioResponse.InfoCarrera(nombreCarrera, facultad);
                    })
                    .collect(Collectors.toList());
            dto.setCarreras(carrerasInfo);

            // 2. Calculamos el Año de Ingreso (el menor de todas las inscripciones)
            estudios.stream()
                    .map(EstudioUsuario::getFechaInscripcion) // Obtenemos fechas
                    .min(LocalDate::compareTo)                // Buscamos la más vieja
                    .ifPresent(fecha -> dto.setAnioIngreso(fecha.getYear())); // Seteamos el año
        }

        return dto;
    }
}