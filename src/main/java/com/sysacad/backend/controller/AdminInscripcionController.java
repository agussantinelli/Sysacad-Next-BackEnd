package com.sysacad.backend.controller;

import com.sysacad.backend.dto.admin.AdminInscripcionRequest;
import com.sysacad.backend.dto.materia.MateriaResponse;
import com.sysacad.backend.dto.inscripcion_cursado.ComisionDisponibleDTO;
import com.sysacad.backend.dto.mesa_examen.MesaExamenDisponibleDTO;
import com.sysacad.backend.service.AdminInscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/inscripcion")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminInscripcionController {

    private final AdminInscripcionService adminInscripcionService;

    @Autowired
    public AdminInscripcionController(AdminInscripcionService adminInscripcionService) {
        this.adminInscripcionService = adminInscripcionService;
    }

    

    @GetMapping("/cursado/materias")
    public ResponseEntity<List<MateriaResponse>> obtenerMateriasCursado(@RequestParam UUID idAlumno) {
        return ResponseEntity.ok(adminInscripcionService.obtenerMateriasParaCursado(idAlumno));
    }

    @GetMapping("/cursado/comisiones")
    public ResponseEntity<List<ComisionDisponibleDTO>> obtenerComisionesCursado(
            @RequestParam UUID idAlumno, 
            @RequestParam UUID idMateria) {
        return ResponseEntity.ok(adminInscripcionService.obtenerComisionesParaCursado(idAlumno, idMateria));
    }

    

    @GetMapping("/examen/materias")
    public ResponseEntity<List<MateriaResponse>> obtenerMateriasExamen(@RequestParam UUID idAlumno) {
        return ResponseEntity.ok(adminInscripcionService.obtenerMateriasParaExamen(idAlumno));
    }

    @GetMapping("/examen/mesas")
    public ResponseEntity<List<MesaExamenDisponibleDTO>> obtenerMesasExamen(
            @RequestParam UUID idAlumno, 
            @RequestParam UUID idMateria) {
        return ResponseEntity.ok(adminInscripcionService.obtenerMesasParaExamen(idAlumno, idMateria));
    }

    

    @PostMapping
    public ResponseEntity<Void> inscribir(@RequestBody AdminInscripcionRequest request) {
        adminInscripcionService.inscribir(request);
        return ResponseEntity.ok().build();
    }
}
