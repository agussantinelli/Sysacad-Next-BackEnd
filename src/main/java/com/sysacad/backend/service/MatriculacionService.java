package com.sysacad.backend.service;

import com.sysacad.backend.dto.carrera_materias.CarreraMateriasDTO;
import com.sysacad.backend.dto.estudiante_materia.EstudianteMateriaDTO;
import com.sysacad.backend.modelo.Matriculacion;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.repository.MatriculacionRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sysacad.backend.modelo.PlanMateria;


import java.time.LocalDate;
import java.util.*;

@Service
public class MatriculacionService {

    private final MatriculacionRepository matriculacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final com.sysacad.backend.repository.InscripcionCursadoRepository inscripcionCursadoRepository;
    private final com.sysacad.backend.repository.InscripcionExamenRepository inscripcionExamenRepository;
    private final EquivalenciaService equivalenciaService;

    private final com.sysacad.backend.repository.MateriaRepository materiaRepository;

    @Autowired
    public MatriculacionService(MatriculacionRepository matriculacionRepository,
            UsuarioRepository usuarioRepository,
            com.sysacad.backend.repository.InscripcionCursadoRepository inscripcionCursadoRepository,
            com.sysacad.backend.repository.InscripcionExamenRepository inscripcionExamenRepository,
            EquivalenciaService equivalenciaService,
            com.sysacad.backend.repository.MateriaRepository materiaRepository) {
        this.matriculacionRepository = matriculacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
        this.equivalenciaService = equivalenciaService;
        this.materiaRepository = materiaRepository;
    }

    @Transactional(readOnly = true)
    public com.sysacad.backend.dto.historial.HistorialMateriaDTO obtenerHistorialMateria(String legajo, UUID idMateria) {
        Usuario alumno = usuarioRepository.findByLegajo(legajo)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        Materia materia = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));

        // Obtener historial de Cursadas
        var todasCursadas = inscripcionCursadoRepository.findByUsuarioId(alumno.getId());
        List<com.sysacad.backend.dto.historial.DetalleCursadaDTO> cursadasDTO = todasCursadas.stream()
                .filter(ic -> ic.getMateria().getId().equals(idMateria))
                .sorted(Comparator.comparing(com.sysacad.backend.modelo.InscripcionCursado::getFechaInscripcion).reversed())
                .map(ic -> new com.sysacad.backend.dto.historial.DetalleCursadaDTO(
                        ic.getFechaInscripcion().toLocalDate(),
                        ic.getComision() != null ? ic.getComision().getNombre() + " (" + ic.getComision().getAnio() + "°)" : "-",
                        ic.getEstado().toString(),
                        ic.getNotaFinal() != null ? ic.getNotaFinal().toString() : "-",
                        ic.getTomo() != null ? ic.getTomo() : "-",
                        ic.getFolio() != null ? ic.getFolio() : "-"
                ))
                .collect(java.util.stream.Collectors.toList());

        // Obtener historial de Exámenes
        var todosExamenes = inscripcionExamenRepository.findByUsuarioId(alumno.getId());
        List<com.sysacad.backend.dto.historial.DetalleFinalDTO> finalesDTO = todosExamenes.stream()
                .filter(ie -> ie.getDetalleMesaExamen().getMateria().getId().equals(idMateria))
                .sorted(Comparator.comparing(com.sysacad.backend.modelo.InscripcionExamen::getFechaInscripcion).reversed())
                .map(ie -> {
                    String turno = ie.getDetalleMesaExamen().getMesaExamen().getNombre();

                    LocalDate fechaExamen = ie.getDetalleMesaExamen().getDiaExamen();
                    
                    return new com.sysacad.backend.dto.historial.DetalleFinalDTO(
                        fechaExamen,
                        turno,
                        ie.getEstado().toString(),
                        ie.getNota() != null ? ie.getNota().toString() : "-",
                        ie.getTomo() != null ? ie.getTomo() : "-",
                        ie.getFolio() != null ? ie.getFolio() : "-"
                    );
                })
                .collect(java.util.stream.Collectors.toList());

        return new com.sysacad.backend.dto.historial.HistorialMateriaDTO(
                materia.getNombre(),
                cursadasDTO,
                finalesDTO
        );
    }

    @Transactional
    public Matriculacion matricularAlumno(Matriculacion matricula) {
        Usuario alumno = usuarioRepository.findById(matricula.getId().getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + matricula.getId().getIdUsuario()));

        if (alumno.getRol() != RolUsuario.ESTUDIANTE) {
            throw new BusinessLogicException("Solo se pueden matricular usuarios con rol ESTUDIANTE");
        }

        matricula.setFechaInscripcion(LocalDate.now());
        matricula.setEstado("ACTIVO");
        Matriculacion guardada = matriculacionRepository.save(matricula);
        
        // Procesar equivalencias automáticas
        try {
            equivalenciaService.procesarEquivalencias(alumno, guardada);
        } catch (Exception e) {
            System.err.println("Error procesando equivalencias para el alumno " + alumno.getId() + ": " + e.getMessage());
        }

        return guardada;
    }

    @Transactional(readOnly = true)
    public List<Matriculacion> obtenerCarrerasPorAlumno(UUID idAlumno) {
        return matriculacionRepository.findByIdIdUsuario(idAlumno);
    }

    @Transactional(readOnly = true)
    public List<CarreraMateriasDTO> obtenerMateriasPorCarreraDelAlumno(String legajo) {
        // Obtener usuario real
        Usuario alumno = usuarioRepository.findByLegajo(legajo)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado con legajo: " + legajo));

        // Obtener inscripciones (Matriculacion - Matriculación en carreras)
        List<Matriculacion> matriculaciones = matriculacionRepository.findByIdIdUsuario(alumno.getId());
        List<CarreraMateriasDTO> resultado = new ArrayList<>();

        // Pre-cargar el historial académico completo (NUEVO SISTEMA)
        var cursadas = inscripcionCursadoRepository.findByUsuarioId(alumno.getId());
        var examenes = inscripcionExamenRepository.findByUsuarioId(alumno.getId());

        // Construir mapa de ESTADO por Materia ID
        Map<UUID, EstadoMateria> mapaEstadoMaterias = construirMapaEstadoMaterias(cursadas, examenes);

        // Iterar sobre cada carrera
        for (Matriculacion matricula : matriculaciones) {
            PlanDeEstudio plan = matricula.getPlan();

            if (plan != null) {

                java.util.UUID idCarrera = plan.getCarrera().getId();
                String nombreCarrera = plan.getCarrera().getNombre();
                
                java.util.UUID idFacultad = matricula.getId().getIdFacultad();
                String nombreFacultad = "Desconocida";
                if(matricula.getFacultad() != null) {
                    nombreFacultad = matricula.getFacultad().getCiudad();
                }

                String nombrePlan = plan.getNombre();

                // Obtener las materias del plan
                List<PlanMateria> planMaterias = plan.getPlanMaterias();

                List<EstudianteMateriaDTO> materiasDTO = new ArrayList<>();

                for (PlanMateria pm : planMaterias) {
                    Materia materia = pm.getMateria();

                    // Obtener estado actual
                    EstadoMateria estadoActual = mapaEstadoMaterias.getOrDefault(materia.getId(),
                            new EstadoMateria("PENDIENTE", "-"));

                    // Verificar correlativas para saber si se puede inscribir
                    boolean sePuedeInscribir = false;
                    // Solo si está PENDIENTE o LIBRE verificamos si puede cursar
                    if (estadoActual.estado.equals("PENDIENTE") || estadoActual.estado.equals("LIBRE")) {
                        sePuedeInscribir = verificarCorrelativas(materia, mapaEstadoMaterias);
                    }

                    EstudianteMateriaDTO dto = new EstudianteMateriaDTO(
                            materia.getId(),
                            materia.getNombre(),
                            pm.getNivel(),
                            estadoActual.estado,
                            estadoActual.nota,
                            sePuedeInscribir,
                            materia.getOptativa(),
                            materia.getHorasCursado(),
                            materia.getCuatrimestreDictado() != null ? materia.getCuatrimestreDictado().name() : null,
                            materia.getCorrelativas().stream().map(com.sysacad.backend.modelo.Materia::getNombre).collect(java.util.stream.Collectors.toList()));
                    materiasDTO.add(dto);
                }

                // Ordenar por nivel y luego nombre
                materiasDTO.sort(Comparator.comparing(EstudianteMateriaDTO::getNivel)
                        .thenComparing(EstudianteMateriaDTO::getNombre));

                resultado.add(new CarreraMateriasDTO(idCarrera, nombreCarrera, idFacultad, nombreFacultad, nombrePlan, materiasDTO));
            }
        }

        return resultado;
    }

    // Helpers de Lógica de Negocio 

    private Map<UUID, EstadoMateria> construirMapaEstadoMaterias(
            List<com.sysacad.backend.modelo.InscripcionCursado> cursadas,
            List<com.sysacad.backend.modelo.InscripcionExamen> examenes) {

        Map<UUID, EstadoMateria> mapa = new HashMap<>();

        // Procesar Cursadas
        for (com.sysacad.backend.modelo.InscripcionCursado cur : cursadas) {
            String estadoStr = cur.getEstado().toString();
            // CURSANDO, REGULAR, PROMOCIONADO, LIBRE, APROBADO
            String notaStr = cur.getNotaFinal() != null ? cur.getNotaFinal().toString() : "-";

            String estadoFinal = estadoStr;
            if (cur.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO) {
                estadoFinal = "APROBADA";
            }

            mapa.put(cur.getMateria().getId(), new EstadoMateria(estadoFinal, notaStr));
        }

        // Procesar Exámenes (Sobreescribe si aprobó final)
        for (com.sysacad.backend.modelo.InscripcionExamen ex : examenes) {
            if (ex.getEstado() == com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO) {
                // Aprobó final -> APROBADA
                mapa.put(ex.getDetalleMesaExamen().getMateria().getId(),
                        new EstadoMateria("APROBADA", ex.getNota() != null ? ex.getNota().toString() : "-"));
            }
        }

        return mapa;
    }

    private boolean verificarCorrelativas(Materia materia, Map<UUID, EstadoMateria> historial) {
        if (materia.getCorrelativas() == null || materia.getCorrelativas().isEmpty()) {
            return true;
        }

        for (Materia correlativa : materia.getCorrelativas()) {
            EstadoMateria estadoCorr = historial.get(correlativa.getId());

            boolean correlativaOk = estadoCorr != null &&
                    (estadoCorr.estado.equals("REGULAR") || estadoCorr.estado.equals("APROBADA"));

            if (!correlativaOk) {
                return false;
            }
        }
        return true;
    }

    private static class EstadoMateria {
        String estado;
        String nota;

        public EstadoMateria(String estado, String nota) {
            this.estado = estado;
            this.nota = nota;
        }
    }
}
