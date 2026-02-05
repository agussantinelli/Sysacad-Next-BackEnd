package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.AdminInscripcionRequest;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.dto.inscripcion_cursado.ComisionDisponibleDTO;
import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoRequest;
import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenRequest;
import com.sysacad.backend.dto.materia.MateriaResponse;
import com.sysacad.backend.dto.mesa_examen.MesaExamenDisponibleDTO;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminInscripcionService {

    private final UsuarioRepository usuarioRepository;
    private final MatriculacionRepository matriculacionRepository;
    private final MateriaRepository materiaRepository;
    private final InscripcionCursadoRepository inscripcionCursadoRepository;
    private final InscripcionExamenRepository inscripcionExamenRepository;
    private final MesaExamenRepository mesaExamenRepository;
    private final DetalleMesaExamenRepository detalleMesaExamenRepository;
    private final CorrelatividadService correlatividadService;
    private final InscripcionCursadoService inscripcionCursadoService;
    private final InscripcionExamenService inscripcionExamenService;
    private final MesaExamenService mesaExamenService;

    @Autowired
    public AdminInscripcionService(UsuarioRepository usuarioRepository, 
                                   MatriculacionRepository matriculacionRepository,
                                   MateriaRepository materiaRepository,
                                   InscripcionCursadoRepository inscripcionCursadoRepository,
                                   InscripcionExamenRepository inscripcionExamenRepository,
                                   MesaExamenRepository mesaExamenRepository,
                                   DetalleMesaExamenRepository detalleMesaExamenRepository,
                                   CorrelatividadService correlatividadService,
                                   InscripcionCursadoService inscripcionCursadoService,
                                   InscripcionExamenService inscripcionExamenService,
                                   MesaExamenService mesaExamenService) {
        this.usuarioRepository = usuarioRepository;
        this.matriculacionRepository = matriculacionRepository;
        this.materiaRepository = materiaRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
        this.mesaExamenRepository = mesaExamenRepository;
        this.detalleMesaExamenRepository = detalleMesaExamenRepository;
        this.correlatividadService = correlatividadService;
        this.inscripcionCursadoService = inscripcionCursadoService;
        this.inscripcionExamenService = inscripcionExamenService;
        this.mesaExamenService = mesaExamenService;
    }

    // 1. Materias para Cursada
    @Transactional(readOnly = true)
    public List<MateriaResponse> obtenerMateriasParaCursado(UUID idAlumno) {
        Usuario alumno = usuarioRepository.findById(idAlumno)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        // Obtener historial completo de cursadas y exámenes una sola vez para evitar N+1
        var cursadas = inscripcionCursadoRepository.findByUsuarioId(idAlumno);
        // Mapa de MateriaID -> EstadoCursada de la cursada más relevante (activo o la ultima)
        // Como puede haber recurrentes, preferimos cualquier estado ACTIVO (Cursando, Regular) o Aprobado.
        // Si tiene varias, y una es Regular, es Regular.
        
        java.util.Set<UUID> materiasConCursadaActivaOAprobada = new java.util.HashSet<>();
            cursadas.stream()
            .filter(i -> i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO ||
                         i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR ||
                         i.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO)
            .forEach(i -> materiasConCursadaActivaOAprobada.add(i.getMateria().getId()));


        List<Matriculacion> matriculaciones = matriculacionRepository.findByIdIdUsuario(idAlumno);

        // Obtener todas las materias de esos planes
        return matriculaciones.stream()
                .flatMap(m -> m.getPlan().getPlanMaterias().stream().map(PlanMateria::getMateria))
                .distinct()
                .filter(m -> {
                    // 1. Filtrar si ya cursa o aprobó
                    if (materiasConCursadaActivaOAprobada.contains(m.getId())) {
                        return false;
                    }
                    // 2. Correlativas (Implementacion eficiente sería pre-calc, pero correlatividadService lo hace interno. 
                    //    Optimizacion futura: CorrelatividadService.puedeCursarBatch)
                    //    Por ahora lo mantenemos individual pero limpio.
                    return correlatividadService.puedeCursar(idAlumno, m.getId());
                })
                .map(MateriaResponse::new)
                .collect(Collectors.toList());
    }

    // 2. Comisiones para Cursada
    @Transactional(readOnly = true)
    public List<ComisionDisponibleDTO> obtenerComisionesParaCursado(UUID idAlumno, UUID idMateria) {
        return inscripcionCursadoService.obtenerOpcionesInscripcion(idMateria, idAlumno);
    }

    // 3. Materias para Examen
    @Transactional(readOnly = true)
    public List<MateriaResponse> obtenerMateriasParaExamen(UUID idAlumno) {
         Usuario alumno = usuarioRepository.findById(idAlumno)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        // Pre-fetch para evitar N+1
        var examenesAprobados = inscripcionExamenRepository.findByUsuarioIdAndEstado(idAlumno, com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO)
                .stream().map(e -> e.getDetalleMesaExamen().getMateria().getId()).collect(Collectors.toSet());
        var cursadasPromocionadas = inscripcionCursadoRepository.findByUsuarioIdAndEstado(idAlumno, com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO)
                .stream().map(c -> c.getMateria().getId()).collect(Collectors.toSet());

        List<Matriculacion> matriculaciones = matriculacionRepository.findByIdIdUsuario(idAlumno);

        return matriculaciones.stream()
                .flatMap(m -> m.getPlan().getPlanMaterias().stream().map(PlanMateria::getMateria))
                .distinct()
                .filter(m -> {
                    // 1. Ya aprobadas
                    if (examenesAprobados.contains(m.getId()) || cursadasPromocionadas.contains(m.getId())) {
                        return false;
                    }
                    // 2. Correlativas para rendir
                    return correlatividadService.puedeRendir(idAlumno, m.getId());
                })
                .map(MateriaResponse::new)
                .collect(Collectors.toList());
    }

    // 4. Mesas para Examen
    @Transactional(readOnly = true)
    public List<MesaExamenDisponibleDTO> obtenerMesasParaExamen(UUID idAlumno, UUID idMateria) {
        return mesaExamenService.obtenerMesasDisponiblesPorMateria(idMateria, idAlumno);
    }

    // 5. Inscribir
    @Transactional
    public void inscribir(AdminInscripcionRequest request) {
        if ("CURSADA".equalsIgnoreCase(request.getTipo())) {
             if (request.getIdMateria() == null) {
                 throw new BusinessLogicException("El ID de la materia es requerido para inscripción a cursada.");
             }
             // Validación extra: Verificar si ya existe inscripción activa para evitar duplicados / errores no controlados
             var existentes = inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(request.getIdAlumno(), request.getIdMateria());
             if (existentes.isPresent()) {
                 var estado = existentes.get().getEstado();
                    if (estado == com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO ||
                        estado == com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR ||
                        estado == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO) {
                     throw new BusinessLogicException("El alumno ya tiene una cursada activa o aprobada para esta materia.");
                 }
             }

             InscripcionCursadoRequest req = new InscripcionCursadoRequest();
             req.setIdUsuario(request.getIdAlumno());
             req.setIdComision(request.getIdReferencia());
             req.setIdMateria(request.getIdMateria());
             
             inscripcionCursadoService.inscribir(req);

        } else if ("EXAMEN".equalsIgnoreCase(request.getTipo())) {
             if (request.getNroDetalle() == null) {
                 throw new BusinessLogicException("El número de detalle es requerido para inscripción a examen.");
             }
             
             // Validar unicidad examen pendiente
             // Se valida dentro de inscripcionExamenService.inscribirAlumno, reutilizamos esa lógica.

             InscripcionExamenRequest req = new InscripcionExamenRequest();
             req.setIdUsuario(request.getIdAlumno());
             req.setIdDetalleMesa(request.getIdReferencia()); 
             req.setNroDetalle(request.getNroDetalle());
             
             inscripcionExamenService.inscribirAlumno(req);
        } else {
            throw new BusinessLogicException("Tipo de inscripción inválido: " + request.getTipo());
        }
    }
}
