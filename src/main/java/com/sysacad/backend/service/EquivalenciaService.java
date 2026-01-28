package com.sysacad.backend.service;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.EstadoExamen;
import com.sysacad.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class EquivalenciaService {

    private final EquivalenciaRepository equivalenciaRepository;
    private final InscripcionExamenRepository inscripcionExamenRepository;
    private final InscripcionCursadoRepository inscripcionCursadoRepository;
    private final MateriaRepository materiaRepository;

    @Autowired
    public EquivalenciaService(EquivalenciaRepository equivalenciaRepository,
                               InscripcionExamenRepository inscripcionExamenRepository,
                               InscripcionCursadoRepository inscripcionCursadoRepository,
                               MateriaRepository materiaRepository) {
        this.equivalenciaRepository = equivalenciaRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.materiaRepository = materiaRepository;
    }

    @Transactional
    public void procesarEquivalencias(Usuario alumno, Matriculacion nuevaMatricula) {
        if (nuevaMatricula.getPlan() == null) return;

        UUID idCarreraDestino = nuevaMatricula.getPlan().getCarrera().getId();
        Integer nroPlanDestino = nuevaMatricula.getPlan().getId().getNroPlan();

        // Obtener todas las materias APROBADAS por el alumno en CUALQUIER plan
        // AGREGAR LA LOGICA DE PROMOCIONES EN CURSADA -> FUNDAMENTAL
        List<InscripcionExamen> finalesAprobados = inscripcionExamenRepository.findByUsuarioId(alumno.getId());
                
        // Buscar reglas de equivalencia para este Plan de Destino
        List<Equivalencia> reglasDestino = equivalenciaRepository.findByIdCarreraDestinoAndNroPlanDestino(
                idCarreraDestino, nroPlanDestino);

        for (InscripcionExamen examen : finalesAprobados) {
            if (examen.getEstado() != EstadoExamen.APROBADO) continue;

            UUID idMateriaAprobada;
            // Verificar si es un examen regular (con detalle) o una equivalencia previa (con materia directa)
            if (examen.getDetalleMesaExamen() != null) {
                idMateriaAprobada = examen.getDetalleMesaExamen().getMateria().getId();
            } else if (examen.getMateria() != null) {
                idMateriaAprobada = examen.getMateria().getId();
            } else {
                continue;
            }

            for (Equivalencia regla : reglasDestino) {
                if (regla.getIdMateriaOrigen().equals(idMateriaAprobada)) {
                    // Match encontrado: Materia Aprobada == Materia Origen de la Regla
                    
                    // Verificar si ya tiene la materia destino aprobada
                    boolean yaAprobada = tieneMateriaAprobada(alumno.getId(), regla.getIdMateriaDestino());
                    
                    if (!yaAprobada) {
                        generarEquivalencia(alumno, regla);
                    }
                }
            }
        }
    }

    private boolean tieneMateriaAprobada(UUID idAlumno, UUID idMateria) {
        // Buscar en examenes aprobados o equivalencias
         List<InscripcionExamen> examenes = inscripcionExamenRepository.findByUsuarioId(idAlumno);
         return examenes.stream().anyMatch(e -> {
             UUID matId = null;
             if(e.getDetalleMesaExamen() != null) matId = e.getDetalleMesaExamen().getMateria().getId();
             else if(e.getMateria() != null) matId = e.getMateria().getId();
             
             return matId != null && matId.equals(idMateria) && 
                 (e.getEstado() == EstadoExamen.APROBADO || e.getEstado() == EstadoExamen.EQUIVALENCIA);
         });
    }

    private void generarEquivalencia(Usuario alumno, Equivalencia regla) {
        InscripcionExamen equiv = new InscripcionExamen();
        equiv.setUsuario(alumno);
        equiv.setFechaInscripcion(java.time.LocalDateTime.now());
        equiv.setEstado(EstadoExamen.EQUIVALENCIA);
        equiv.setNota(null); 
        
        // Asignación directa de materia (gracias a la modificación en InscripcionExamen)
        Materia materiaDestino = materiaRepository.findById(regla.getIdMateriaDestino())
            .orElseThrow(() -> new RuntimeException("Materia destino de equivalencia no encontrada"));
            
        equiv.setMateria(materiaDestino);
        equiv.setDetalleMesaExamen(null); 

        inscripcionExamenRepository.save(equiv);
    }
}
