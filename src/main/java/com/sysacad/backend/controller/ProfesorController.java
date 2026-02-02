package com.sysacad.backend.controller;

import com.sysacad.backend.dto.materia.MateriaProfesorDTO;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profesores")
public class ProfesorController {

    private final ProfesorService profesorService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ProfesorController(ProfesorService profesorService, UsuarioRepository usuarioRepository) {
        this.profesorService = profesorService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/mis-materias")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<MateriaProfesorDTO>> getMisMaterias(Authentication authentication) {
        String username = authentication.getName();
        Usuario profesor = usuarioRepository.findByLegajo(username) // Asumimos username es legajo o email, verificar Auth config.
                // En AuthController se usa legajo para login. Si authentication.getName() devuelve legajo, esto es correcto.
                // Si devuelve user details object, habría que verlo. 
                // Standard UserDetails implementation returns username.
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + username));
                
        List<MateriaProfesorDTO> materias = profesorService.obtenerMateriasAsignadas(profesor.getId());
        return ResponseEntity.ok(materias);
    }
}
