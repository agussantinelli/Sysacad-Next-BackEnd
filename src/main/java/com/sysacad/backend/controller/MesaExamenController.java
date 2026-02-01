package com.sysacad.backend.controller;

import com.sysacad.backend.dto.mesa_examen.MesaExamenRequest;
import com.sysacad.backend.dto.mesa_examen.MesaExamenResponse;
import com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenRequest;
import com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenResponse;
import com.sysacad.backend.service.MesaExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*") 
public class MesaExamenController {

    @Autowired
    private MesaExamenService mesaExamenService;

    @PostMapping
    public ResponseEntity<MesaExamenResponse> createMesa(@RequestBody MesaExamenRequest request) {
        return ResponseEntity.ok(mesaExamenService.createMesa(request));
    }

    @GetMapping
    public ResponseEntity<List<MesaExamenResponse>> getAllMesas() {
        return ResponseEntity.ok(mesaExamenService.getAllMesas());
    }

    @PostMapping("/detalles")
    public ResponseEntity<DetalleMesaExamenResponse> addDetalle(@RequestBody DetalleMesaExamenRequest request) {
        return ResponseEntity.ok(mesaExamenService.addDetalle(request));
    }

    @Autowired
    private com.sysacad.backend.repository.UsuarioRepository usuarioRepository;

    @GetMapping("/disponibles")
    public ResponseEntity<List<DetalleMesaExamenResponse>> getExamenesDisponibles(org.springframework.security.core.Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        String legajo = authentication.getName();
        com.sysacad.backend.modelo.Usuario usuario = usuarioRepository.findByLegajo(legajo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(mesaExamenService.getExamenesDisponibles(usuario.getId()));
    }

    @GetMapping("/detalles/{id}/{nroDetalle}")
    public ResponseEntity<DetalleMesaExamenResponse> getDetalleMesa(@PathVariable java.util.UUID id, @PathVariable Integer nroDetalle) {
        return ResponseEntity.ok(mesaExamenService.getDetalleMesa(id, nroDetalle));
    }
    @GetMapping("/materias/{idMateria}/mesas")
    public ResponseEntity<List<com.sysacad.backend.dto.mesa_examen.MesaExamenDisponibleDTO>> getMesasPorMateria(
            @PathVariable java.util.UUID idMateria,
            org.springframework.security.core.Authentication authentication) {
        
        if (authentication == null) return ResponseEntity.status(401).build();

        String legajo = authentication.getName();
        com.sysacad.backend.modelo.Usuario usuario = usuarioRepository.findByLegajo(legajo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(mesaExamenService.obtenerMesasDisponibles(idMateria, usuario.getId()));
    }
}
