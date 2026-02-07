package com.sysacad.backend.controller;

import com.sysacad.backend.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "http://localhost:4200")
public class HealthController {

    private final IEmailService emailService;

    @Autowired
    public HealthController(IEmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> checkHealth() {
        return ResponseEntity.ok(Collections.singletonMap("status", "UP"));
    }

    @GetMapping("/test-email")
    public ResponseEntity<Map<String, String>> testEmail(@RequestParam String to) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("titulo", "¡Bienvenido a Sysacad Next!");
        variables.put("mensaje", "Este es un correo de prueba para verificar el nuevo sistema de templates HTML con la identidad visual de la UTN.");
        variables.put("actionText", "Ir al Dashboard");
        variables.put("actionUrl", "http://localhost:4200/dashboard");

        emailService.sendHtmlEmail(to, "Sysacad Next - Prueba de Email", "base-email", variables);

        return ResponseEntity.ok(Collections.singletonMap("result", "Email enviado a " + to));
    }
}
