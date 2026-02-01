package com.sysacad.backend.controller;

import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoRequest;
import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoResponse;
import com.sysacad.backend.dto.calificacion_cursada.CalificacionCursadaRequest;
import com.sysacad.backend.dto.calificacion_cursada.CalificacionCursadaResponse;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.InscripcionCursadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inscripciones-cursado")
@CrossOrigin(origins = "*")
public class InscripcionCursadoController {

    @Autowired
    private InscripcionCursadoService inscripcionCursadoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<InscripcionCursadoResponse> inscribir(@RequestBody InscripcionCursadoRequest request,
            Authentication auth) {
                
        if (request.getIdUsuario() == null && auth != null) {
            String identifier = auth.getName();
            Usuario usuario = usuarioRepository.findByLegajoOrMail(identifier, identifier)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + identifier));
            request.setIdUsuario(usuario.getId());
        }
        return ResponseEntity.ok(inscripcionCursadoService.inscribir(request));
    }

    @GetMapping("/mis-cursadas")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<List<InscripcionCursadoResponse>> getMisCursadas(Authentication auth) {
        String identifier = auth.getName();
        Usuario usuario = usuarioRepository.findByLegajoOrMail(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + identifier));

        return ResponseEntity.ok(inscripcionCursadoService.obtenerHistorial(usuario.getId()));
    }

    @PostMapping("/{id}/notas")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<CalificacionCursadaResponse> cargarNota(@PathVariable UUID id,
            @RequestBody CalificacionCursadaRequest request) {
        return ResponseEntity.ok(inscripcionCursadoService.cargarNota(id, request));
    }

    @PutMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<InscripcionCursadoResponse> finalizarCursada(@PathVariable UUID id,
                                                                       @RequestParam java.math.BigDecimal nota,
                                                                       @RequestParam com.sysacad.backend.modelo.enums.EstadoCursada estado) {
        return ResponseEntity.ok(inscripcionCursadoService.finalizarCursada(id, nota, estado));
    }
    @GetMapping("/actuales")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<List<InscripcionCursadoResponse>> getCursadasActuales(Authentication auth, @RequestParam(required = false) UUID idUsuario) {
        UUID id = idUsuario;
        if (id == null) {
            String identifier = auth.getName();
            Usuario usuario = usuarioRepository.findByLegajoOrMail(identifier, identifier)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + identifier));
            id = usuario.getId();
        }
        return ResponseEntity.ok(inscripcionCursadoService.obtenerCursadasActuales(id));
    }

    @GetMapping("/materias/{idMateria}/disponibles")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<List<com.sysacad.backend.dto.inscripcion_cursado.ComisionDisponibleDTO>> getComisionesDisponibles(
            @PathVariable UUID idMateria, 
            @RequestParam(required = false) UUID usuarioId,
            Authentication auth) {
        
        UUID id = usuarioId;
        if (id == null) {
            String identifier = auth.getName();
            Usuario usuario = usuarioRepository.findByLegajoOrMail(identifier, identifier)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + identifier));
            id = usuario.getId();
        }
        return ResponseEntity.ok(inscripcionCursadoService.obtenerOpcionesInscripcion(idMateria, id));
    }
}
