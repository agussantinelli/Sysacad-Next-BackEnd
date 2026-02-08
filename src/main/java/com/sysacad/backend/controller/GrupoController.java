package com.sysacad.backend.controller;

import com.sysacad.backend.dto.grupo.GrupoRequest;
import com.sysacad.backend.dto.grupo.GrupoResponse;
import com.sysacad.backend.dto.grupo.MensajeGrupoRequest;
import com.sysacad.backend.dto.grupo.MensajeGrupoResponse;
import com.sysacad.backend.dto.grupo.MiembroGrupoRequest;
import com.sysacad.backend.dto.grupo.MiembroGrupoResponse;
import com.sysacad.backend.modelo.Grupo;
import com.sysacad.backend.modelo.MensajeGrupo;
import com.sysacad.backend.modelo.MiembroGrupo;
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
    private final UsuarioService usuarioService;
    private final com.sysacad.backend.mapper.GrupoMapper grupoMapper;
    private final com.sysacad.backend.mapper.MensajeGrupoMapper mensajeGrupoMapper;

    
    private UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String legajo = auth.getName();
         return usuarioService.obtenerPorLegajo(legajo)
                 .map(Usuario::getId)
                 .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    @PostMapping
    public ResponseEntity<GrupoResponse> crearGrupo(@RequestBody GrupoRequest request) {
        UUID idUsuario = getAuthenticatedUserId();
        Grupo grupo = grupoService.crearGrupo(request, idUsuario);
        return new ResponseEntity<>(grupoMapper.toDTO(grupo), HttpStatus.CREATED);
    }

    @GetMapping("/alumno")
    public ResponseEntity<List<GrupoResponse>> listarGruposAlumno() {
        UUID idUsuario = getAuthenticatedUserId();
        return ResponseEntity.ok(grupoService.obtenerGruposAlumno(idUsuario));
    }

    @GetMapping("/profesor")
    public ResponseEntity<List<GrupoResponse>> listarGruposProfesor() {
        UUID idUsuario = getAuthenticatedUserId();
        return ResponseEntity.ok(grupoService.obtenerGruposDocente(idUsuario));
    }

    @GetMapping("/mis-grupos")
    public ResponseEntity<List<GrupoResponse>> listarMisGrupos() {
        UUID idUsuario = getAuthenticatedUserId();
        return ResponseEntity.ok(grupoService.obtenerMisGrupos(idUsuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponse> obtenerGrupo(@PathVariable UUID id) {
        Grupo grupo = grupoService.obtenerPorId(id);
        return ResponseEntity.ok(grupoMapper.toDTO(grupo));
    }

    @PostMapping("/{id}/miembros")
    public ResponseEntity<Void> agregarMiembro(@PathVariable UUID id, @RequestBody MiembroGrupoRequest request) {
        grupoService.agregarMiembro(id, request.getIdUsuario(), request.getRol());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/miembros/{idUsuario}")
    public ResponseEntity<Void> eliminarMiembro(@PathVariable UUID id, @PathVariable UUID idUsuario) {
        grupoService.eliminarMiembro(id, idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/mensajes")
    public ResponseEntity<MensajeGrupoResponse> enviarMensaje(@PathVariable UUID id, @RequestBody MensajeGrupoRequest request) {
        UUID idRemitente = getAuthenticatedUserId();
        MensajeGrupo mensaje = grupoService.enviarMensaje(id, request, idRemitente);
        return ResponseEntity.ok(mensajeGrupoMapper.toDTO(mensaje));
    }

    @PostMapping("/mensajes")
    public ResponseEntity<MensajeGrupoResponse> enviarMensajeSinId(@RequestBody MensajeGrupoRequest request) {
        UUID idRemitente = getAuthenticatedUserId();
        MensajeGrupo mensaje = grupoService.enviarMensaje(null, request, idRemitente);
        return ResponseEntity.ok(mensajeGrupoMapper.toDTO(mensaje));
    }

    @GetMapping("/mensajes/sin-leer/total")
    public ResponseEntity<Long> contarMensajesSinLeer() {
        UUID idUsuario = getAuthenticatedUserId();
        return ResponseEntity.ok(grupoService.contarMensajesSinLeerTotales(idUsuario));
    }

    @GetMapping("/{id}/mensajes")
    public ResponseEntity<Page<MensajeGrupoResponse>> obtenerMensajes(@PathVariable UUID id, Pageable pageable) {
        UUID idUsuario = getAuthenticatedUserId();
        Page<MensajeGrupoResponse> mensajes = grupoService.obtenerMensajesConEstado(id, idUsuario, pageable);
        return ResponseEntity.ok(mensajes);
    }

    @PostMapping("/{id}/marcar-leido")
    public ResponseEntity<Void> marcarComoLeido(@PathVariable UUID id) {
        UUID idUsuario = getAuthenticatedUserId();
        grupoService.marcarComoLeido(id, idUsuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/miembros")
    public ResponseEntity<List<MiembroGrupoResponse>> obtenerMiembros(@PathVariable UUID id) {
        List<MiembroGrupo> miembros = grupoService.obtenerMiembros(id);
        return ResponseEntity.ok(miembros.stream()
                .map(grupoMapper::toMiembroDTO)
                .collect(Collectors.toList()));
    }
}
