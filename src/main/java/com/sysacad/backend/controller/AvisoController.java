package com.sysacad.backend.controller;

import com.sysacad.backend.dto.aviso.AvisoRequest;
import com.sysacad.backend.dto.aviso.AvisoResponse;
import com.sysacad.backend.modelo.Aviso;
import com.sysacad.backend.service.AvisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/avisos")
@CrossOrigin(origins = "http://localhost:4200")
public class AvisoController {

    private final AvisoService avisoService;
    private final com.sysacad.backend.mapper.AvisoMapper avisoMapper;
    private final com.sysacad.backend.repository.UsuarioRepository usuarioRepository;

    @Autowired
    public AvisoController(AvisoService avisoService, com.sysacad.backend.mapper.AvisoMapper avisoMapper, com.sysacad.backend.repository.UsuarioRepository usuarioRepository) {
        this.avisoService = avisoService;
        this.avisoMapper = avisoMapper;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AvisoResponse> publicarAviso(@RequestBody AvisoRequest request) {
        Aviso aviso = avisoMapper.toEntity(request);
        // El servicio setea fecha y estado por defecto, o usamos el request si aplica
        // El request tiene estado, pero el servicio lo sobreescribe a 'PUBLICADO'.
        Aviso nuevo = avisoService.publicarAviso(aviso);
        return new ResponseEntity<>(avisoMapper.toDTO(nuevo), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AvisoResponse>> obtenerUltimosAvisos(org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            // Podríamos optimizar no buscando el usuario 2 veces si el servicio aceptara nombre, pero el ID es más seguro
            // Sin embargo, para no romper encapsulamiento de "quien es el usuario actual", lo buscamos acá
            // O mejor aún, el servicio ya tiene método para buscar usuario, pero el controller ya lo hacía en marcarComoLeido
            // Reutilizamos lógica de búsqueda
            return usuarioRepository.findByLegajoOrMail(username, username)
                    .map(usuario -> ResponseEntity.ok(avisoService.obtenerUltimosAvisosParaUsuario(usuario.getId())))
                    .orElse(ResponseEntity.ok(avisoMapper.toDTOs(avisoService.obtenerUltimosAvisos())));
        }
        
        List<Aviso> avisos = avisoService.obtenerUltimosAvisos();
        return ResponseEntity.ok(avisoMapper.toDTOs(avisos));
    }

    @PostMapping("/{id}/leido")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> marcarComoLeido(@PathVariable java.util.UUID id,
                                                org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();
        com.sysacad.backend.modelo.Usuario usuario = usuarioRepository.findByLegajoOrMail(username, username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        avisoService.marcarComoLeido(id, usuario.getId());
        return ResponseEntity.ok().build();
    }
}
