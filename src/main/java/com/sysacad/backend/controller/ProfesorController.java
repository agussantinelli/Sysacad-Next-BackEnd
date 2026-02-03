package com.sysacad.backend.controller;

import com.sysacad.backend.dto.comision.ComisionDetalladaDTO;
import com.sysacad.backend.dto.comision.ComisionHorarioDTO;
import com.sysacad.backend.dto.examen.ProfesorMesaExamenDTO;
import com.sysacad.backend.dto.examen.ProfesorDetalleExamenDTO;
import com.sysacad.backend.dto.examen.AlumnoExamenDTO;
import com.sysacad.backend.dto.examen.CargaNotaItemDTO;
import com.sysacad.backend.dto.materia.MateriaProfesorDTO;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.dto.comision.AlumnoCursadaDTO;
import com.sysacad.backend.dto.comision.CargaNotasCursadaDTO;
import com.sysacad.backend.service.CertificadoService;
import com.sysacad.backend.service.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profesores")
public class ProfesorController {

    private final ProfesorService profesorService;
    private final CertificadoService certificadoService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ProfesorController(ProfesorService profesorService, CertificadoService certificadoService, UsuarioRepository usuarioRepository) {
        this.profesorService = profesorService;
        this.certificadoService = certificadoService;
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
    public ResponseEntity<List<ComisionDetalladaDTO>> getMisComisiones(Authentication authentication) {
        String username = authentication.getName();
        Usuario profesor = usuarioRepository.findByLegajo(username)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + username));

        List<ComisionDetalladaDTO> comisiones = profesorService.obtenerTodasLasComisiones(profesor.getId());
        return ResponseEntity.ok(comisiones);
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

    @GetMapping("/mesas-examen")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<ProfesorMesaExamenDTO>> getMisMesasExamen(Authentication authentication) {
        String username = authentication.getName();
        Usuario profesor = usuarioRepository.findByLegajo(username)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + username));
        
        List<ProfesorMesaExamenDTO> mesas = profesorService.obtenerMesasExamenProfesor(profesor.getId());
        return ResponseEntity.ok(mesas);
    }

    @GetMapping("/mesas-examen/{idMesa}/materias")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<ProfesorDetalleExamenDTO>> getDetallesMesaExamen(
            @PathVariable UUID idMesa,
            Authentication authentication) {
        String username = authentication.getName();
        Usuario profesor = usuarioRepository.findByLegajo(username)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado: " + username));

        List<ProfesorDetalleExamenDTO> detalles = profesorService.obtenerDetallesMesaProfesor(profesor.getId(), idMesa);
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/mesas-examen/{idMesa}/materias/{nroDetalle}/inscriptos")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<AlumnoExamenDTO>> getInscriptosMesaExamen(
            @PathVariable UUID idMesa,
            @PathVariable Integer nroDetalle,
            Authentication authentication) {
        String username = authentication.getName();
        Usuario profesor = usuarioRepository.findByLegajo(username)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        return ResponseEntity.ok(profesorService.obtenerInscriptosExamen(profesor.getId(), idMesa, nroDetalle));
    }

    @PostMapping("/mesas-examen/calificar-lote")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<Void> cargarNotasLote(
            @RequestBody List<CargaNotaItemDTO> notas,
            Authentication authentication) {
        String username = authentication.getName();
        Usuario profesor = usuarioRepository.findByLegajo(username)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        profesorService.cargarNotasLote(profesor.getId(), notas);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/certificado-regular")
    @PreAuthorize("hasRole('PROFESOR') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> descargarCertificadoRegularDocente(Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByLegajo(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        byte[] pdfBytes = certificadoService.generarCertificadoDocente(usuario.getId());

        String filename = "certificado_servicios_" + usuario.getLegajo() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdfBytes);
    }

    @GetMapping("/comisiones/{idComision}/materias/{idMateria}/inscriptos")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<AlumnoCursadaDTO>> getInscriptosComision(
            @PathVariable UUID idComision,
            @PathVariable UUID idMateria,
            Authentication authentication) {
        
        Usuario profesor = usuarioRepository.findByLegajo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        List<AlumnoCursadaDTO> inscriptos = profesorService.obtenerInscriptosCursada(profesor.getId(), idComision, idMateria);
        return ResponseEntity.ok(inscriptos);
    }

    @PostMapping("/comisiones/{idComision}/materias/{idMateria}/calificar")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<Void> cargarNotasComision(
            @PathVariable UUID idComision,
            @PathVariable UUID idMateria,
            @RequestBody CargaNotasCursadaDTO notasDTO,
            Authentication authentication) {

        Usuario profesor = usuarioRepository.findByLegajo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        profesorService.cargarNotasCursada(profesor.getId(), idComision, idMateria, notasDTO);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/estadisticas/general")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<com.sysacad.backend.dto.profesor.ProfesorEstadisticasDTO> getEstadisticasGenerales(
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer anio,
            Authentication authentication) {
        
        Usuario profesor = usuarioRepository.findByLegajo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        return ResponseEntity.ok(profesorService.obtenerEstadisticasGenerales(profesor.getId(), anio));
    }

    @GetMapping("/estadisticas/materias/{idMateria}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<com.sysacad.backend.dto.profesor.ProfesorEstadisticasDTO> getEstadisticasPorMateria(
            @PathVariable UUID idMateria,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer anio,
            Authentication authentication) {
        
        Usuario profesor = usuarioRepository.findByLegajo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        return ResponseEntity.ok(profesorService.obtenerEstadisticasPorMateria(profesor.getId(), idMateria, anio));
    }

    @GetMapping("/estadisticas/anios")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<Integer>> getAniosDisponibles(Authentication authentication) {
        Usuario profesor = usuarioRepository.findByLegajo(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        return ResponseEntity.ok(profesorService.obtenerAniosConActividad(profesor.getId()));
    }
}
