package com.sysacad.backend.controller;

import com.sysacad.backend.dto.sancion.SancionRequest;
import com.sysacad.backend.dto.sancion.SancionResponse;
import com.sysacad.backend.modelo.Sancion;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.service.SancionService;
import com.sysacad.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sanciones")
@CrossOrigin(origins = "http://localhost:4200")
public class SancionController {

    private final SancionService sancionService;
    private final UsuarioService usuarioService;
    private final com.sysacad.backend.mapper.SancionMapper sancionMapper;

    @Autowired
    public SancionController(SancionService sancionService, UsuarioService usuarioService, com.sysacad.backend.mapper.SancionMapper sancionMapper) {
        this.sancionService = sancionService;
        this.usuarioService = usuarioService;
        this.sancionMapper = sancionMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SancionResponse> aplicarSancion(@RequestBody SancionRequest request) {
        Sancion sancion = sancionMapper.toEntity(request);
        // mapper mapea motivo, fechaInicio, fechaFin si coinciden.
        
        Usuario usuario = usuarioService.obtenerPorId(request.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        sancion.setUsuario(usuario);

        Sancion nueva = sancionService.aplicarSancion(sancion);
        return new ResponseEntity<>(sancionMapper.toDTO(nueva), HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<List<SancionResponse>> listarSancionesUsuario(@PathVariable UUID idUsuario) {
        List<Sancion> sanciones = sancionService.listarSancionesUsuario(idUsuario);
        return ResponseEntity.ok(sancionMapper.toDTOs(sanciones));
    }
}
