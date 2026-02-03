package com.sysacad.backend.controller;

import com.sysacad.backend.dto.comision.ComisionHorarioDTO;
import com.sysacad.backend.dto.materia.MateriaProfesorDTO;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/mis-comisiones")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<ComisionHorarioDTO>> getMisComisiones(Authentication authentication) {
        String username = authentication.getName();
        Usuario profesor = usuarioRepository.findByLegajo(username)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + username));

        List<ComisionHorarioDTO> comisiones = profesorService.obtenerTodasLasComisiones(profesor.getId());
        return ResponseEntity.ok(comisiones);
    }

    @GetMapping("/comisiones/{idComision}/materias")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<MateriaProfesorDTO>> getMateriasEnComision(
            @PathVariable UUID idComision,
            Authentication authentication) {
        String username = authentication.getName();
        Usuario profesor = usuarioRepository.findByLegajo(username)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + username));

        List<MateriaProfesorDTO> materias = profesorService.obtenerMateriasEnComision(profesor.getId(), idComision);
        return ResponseEntity.ok(materias);
    }

    @GetMapping("/materias/{idMateria}/comisiones")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<ComisionHorarioDTO>> getComisionesDeMateria(
            @PathVariable UUID idMateria,
            Authentication authentication) {
        String username = authentication.getName();
        Usuario profesor = usuarioRepository.findByLegajo(username)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + username));

        List<ComisionHorarioDTO> comisiones = profesorService.obtenerComisionesDeMateria(profesor.getId(), idMateria);
        return ResponseEntity.ok(comisiones);
    }
}
