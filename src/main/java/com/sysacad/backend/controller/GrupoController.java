package com.sysacad.backend.controller;

import com.sysacad.backend.dto.grupo.GrupoRequest;
import com.sysacad.backend.dto.grupo.GrupoResponse;
import com.sysacad.backend.dto.grupo.MensajeGrupoRequest;
import com.sysacad.backend.dto.grupo.MensajeGrupoResponse;
import com.sysacad.backend.dto.grupo.MiembroGrupoRequest;
import com.sysacad.backend.modelo.Grupo;
import com.sysacad.backend.modelo.MensajeGrupo;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.service.GrupoService;
import com.sysacad.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupoService;
    private final UsuarioService usuarioService; // Para buscar ID por legajo si hiciera falta en utility

    // Helper para obtener ID del usuario autenticado
    private UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String legajo = auth.getName();
        // Asumiendo que tenemos un método rápido o el servicio lo cachea. 
        // Idealmente el ID vendría en el Principal, pero por ahora buscamos por legajo.
         return usuarioService.obtenerPorLegajo(legajo)
                 .map(Usuario::getId)
                 .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    @PostMapping
    public ResponseEntity<GrupoResponse> crearGrupo(@RequestBody GrupoRequest request) {
        UUID idUsuario = getAuthenticatedUserId();
        Grupo grupo = grupoService.crearGrupo(request, idUsuario);
        return new ResponseEntity<>(toDTO(grupo), HttpStatus.CREATED);
    }

    @GetMapping("/mis-grupos")
    public ResponseEntity<List<GrupoResponse>> listarMisGrupos() {
        UUID idUsuario = getAuthenticatedUserId();
        List<Grupo> grupos = grupoService.obtenerMisGrupos(idUsuario);
        return ResponseEntity.ok(grupos.stream().map(this::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponse> obtenerGrupo(@PathVariable UUID id) {
        Grupo grupo = grupoService.obtenerPorId(id);
        // TODO: Validar que el usuario sea miembro si es privado
        return ResponseEntity.ok(toDTO(grupo));
    }

    @PostMapping("/{id}/miembros")
    public ResponseEntity<Void> agregarMiembro(@PathVariable UUID id, @RequestBody MiembroGrupoRequest request) {
        // TODO: Validar permisos (solo admin del grupo debería poder agregar)
        grupoService.agregarMiembro(id, request.getIdUsuario(), request.getRol());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/miembros/{idUsuario}")
    public ResponseEntity<Void> eliminarMiembro(@PathVariable UUID id, @PathVariable UUID idUsuario) {
        // TODO: Validar permisos
        grupoService.eliminarMiembro(id, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/mensajes")
    public ResponseEntity<MensajeGrupoResponse> enviarMensaje(@PathVariable UUID id, @RequestBody MensajeGrupoRequest request) {
        UUID idRemitente = getAuthenticatedUserId();
        MensajeGrupo mensaje = grupoService.enviarMensaje(id, request, idRemitente);
        return ResponseEntity.ok(toDTO(mensaje));
    }

    @GetMapping("/{id}/mensajes")
    public ResponseEntity<Page<MensajeGrupoResponse>> obtenerMensajes(@PathVariable UUID id, Pageable pageable) {
        // TODO: Validar que el usuario sea miembro
        Page<MensajeGrupo> mensajes = grupoService.obtenerMensajes(id, pageable);
        return ResponseEntity.ok(mensajes.map(this::toDTO));
    }

    // --- Mappers simples ---
    private GrupoResponse toDTO(Grupo grupo) {
        return new GrupoResponse(
                grupo.getId(),
                grupo.getNombre(),
                grupo.getDescripcion(),
                grupo.getTipo(),
                grupo.getEstado().name(),
                grupo.getFechaCreacion()
        );
    }

    private MensajeGrupoResponse toDTO(MensajeGrupo mensaje) {
        return new MensajeGrupoResponse(
                mensaje.getId(),
                mensaje.getGrupo().getId(),
                mensaje.getUsuario().getId(),
                mensaje.getUsuario().getNombre() + " " + mensaje.getUsuario().getApellido(),
                mensaje.getContenido(),
                mensaje.getEditado(),
                mensaje.getFechaEnvio()
        );
    }
}
