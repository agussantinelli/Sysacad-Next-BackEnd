package com.sysacad.backend.controller;

import com.sysacad.backend.dto.LoginRequest;
import com.sysacad.backend.dto.UsuarioResponse;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Autenticar
            Usuario usuario = usuarioService.autenticar(request.getIdentificador(), request.getPassword());

            // Crear respuesta segura
            UsuarioResponse response = new UsuarioResponse(usuario);

            response.setTipoIdentificador(request.getTipoIdentificador());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}