package com.sysacad.backend.controller;

import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenRequest;
import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenResponse;
import com.sysacad.backend.dto.inscripcion_examen.CargaNotaExamenRequest;
import com.sysacad.backend.service.InscripcionExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inscripciones-examen")
@CrossOrigin(origins = "*")
public class InscripcionExamenController {

    @Autowired
    private InscripcionExamenService inscripcionExamenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<InscripcionExamenResponse> inscribir(@RequestBody InscripcionExamenRequest request,
            Authentication authentication) {
        // Security check: if user ID is missing, try to get from token
        if (request.getIdUsuario() == null && authentication != null) {
            String email = authentication.getName(); // email from token
            Usuario usuario = usuarioRepository.findByMail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            request.setIdUsuario(usuario.getId());
        }

        return ResponseEntity.ok(inscripcionExamenService.inscribirAlumno(request));
    }

    @GetMapping("/mis-inscripciones")
    public ResponseEntity<List<InscripcionExamenResponse>> getMisInscripciones(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(401).build();

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByMail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(inscripcionExamenService.getInscripcionesByAlumno(usuario.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable UUID id) {
        inscripcionExamenService.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/calificar")
    public ResponseEntity<InscripcionExamenResponse> calificar(@PathVariable UUID id,
            @RequestBody CargaNotaExamenRequest request) {
        return ResponseEntity.ok(inscripcionExamenService.calificarExamen(id, request));
    }
}
