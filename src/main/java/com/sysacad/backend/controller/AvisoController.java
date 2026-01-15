package com.sysacad.backend.controller;

import com.sysacad.backend.dto.AvisoRequest;
import com.sysacad.backend.dto.AvisoResponse;
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

    @Autowired
    public AvisoController(AvisoService avisoService) {
        this.avisoService = avisoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AvisoResponse> publicarAviso(@RequestBody AvisoRequest request) {
        Aviso aviso = new Aviso();
        aviso.setTitulo(request.getTitulo());
        aviso.setDescripcion(request.getDescripcion());
        // El servicio setea fecha y estado por defecto, o usamos el request si aplica
        // El request tiene estado, pero el servicio lo sobreescribe a 'PUBLICADO'.
        // Vamos a dejar que el servicio maneje la lógica por defecto.

        Aviso nuevo = avisoService.publicarAviso(aviso);
        return new ResponseEntity<>(new AvisoResponse(nuevo), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AvisoResponse>> obtenerUltimosAvisos() {
        List<Aviso> avisos = avisoService.obtenerUltimosAvisos();
        List<AvisoResponse> response = avisos.stream()
                .map(AvisoResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
