package com.sysacad.backend.service;

import com.sysacad.backend.dto.CarreraMateriasDTO;
import com.sysacad.backend.modelo.Matriculacion;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.MatriculacionRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.dto.EstudianteMateriaDTO;

import java.time.LocalDate;
import java.util.*;

@Service
public class MatriculacionService {

    private final MatriculacionRepository matriculacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final com.sysacad.backend.repository.InscripcionCursadoRepository inscripcionCursadoRepository;
    private final com.sysacad.backend.repository.InscripcionExamenRepository inscripcionExamenRepository;

    @Autowired
    public MatriculacionService(MatriculacionRepository matriculacionRepository,
            UsuarioRepository usuarioRepository,
            com.sysacad.backend.repository.InscripcionCursadoRepository inscripcionCursadoRepository,
            com.sysacad.backend.repository.InscripcionExamenRepository inscripcionExamenRepository) {
        this.matriculacionRepository = matriculacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
    }

    @Transactional
    public Matriculacion matricularAlumno(Matriculacion matricula) {
        Usuario alumno = usuarioRepository.findById(matricula.getId().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (alumno.getRol() != RolUsuario.ESTUDIANTE) {
            throw new RuntimeException("Solo se pueden matricular usuarios con rol ESTUDIANTE");
        }

        matricula.setFechaInscripcion(LocalDate.now());
        matricula.setEstado("ACTIVO");
        return matriculacionRepository.save(matricula);
    }

    @Transactional(readOnly = true)
    public List<Matriculacion> obtenerCarrerasPorAlumno(UUID idAlumno) {
        return matriculacionRepository.findByIdIdUsuario(idAlumno);
    }

    @Transactional(readOnly = true)
    public List<CarreraMateriasDTO> obtenerMateriasPorCarreraDelAlumno(String legajo) {
        // 1. Obtener usuario real
        Usuario alumno = usuarioRepository.findByLegajo(legajo)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con legajo: " + legajo));

        // 2. Obtener inscripciones (Matriculacion - Matriculación en carreras)
        List<Matriculacion> matriculaciones = matriculacionRepository.findByIdIdUsuario(alumno.getId());
        List<CarreraMateriasDTO> resultado = new ArrayList<>();

        // 3. Pre-cargar el historial académico completo (NUEVO SISTEMA)
        var cursadas = inscripcionCursadoRepository.findByUsuarioId(alumno.getId());
        var examenes = inscripcionExamenRepository.findByUsuarioId(alumno.getId());

        // 3.1 Construir mapa de ESTADO por Materia ID
        Map<UUID, EstadoMateria> mapaEstadoMaterias = construirMapaEstadoMaterias(cursadas, examenes);

        // 4. Iterar sobre cada carrera
        for (Matriculacion matricula : matriculaciones) {
            PlanDeEstudio plan = matricula.getPlan();

            if (plan != null) {
                // Integer nroCarrera = plan.getCarrera().getId().getNroCarrera(); // Removed
                java.util.UUID idCarrera = plan.getCarrera().getId();
                String nombreCarrera = plan.getCarrera().getNombre();
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
                            materia.getOptativa());
                    materiasDTO.add(dto);
                }

                // Ordenar por nivel y luego nombre
                materiasDTO.sort(Comparator.comparing(EstudianteMateriaDTO::getNivel)
                        .thenComparing(EstudianteMateriaDTO::getNombre));

                resultado.add(new CarreraMateriasDTO(idCarrera, nombreCarrera, nombrePlan, materiasDTO));
            }
        }

        return resultado;
    }

    // --- Helpers de Lógica de Negocio ---

    private Map<UUID, EstadoMateria> construirMapaEstadoMaterias(
            List<com.sysacad.backend.modelo.InscripcionCursado> cursadas,
            List<com.sysacad.backend.modelo.InscripcionExamen> examenes) {

        Map<UUID, EstadoMateria> mapa = new HashMap<>();

        // 1. Procesar Cursadas
        for (com.sysacad.backend.modelo.InscripcionCursado cur : cursadas) {
            String estadoStr = cur.getEstado().toString();
            // CURSANDO, REGULAR, PROMOCIONADO, LIBRE, APROBADO
            String notaStr = cur.getNotaFinal() != null ? cur.getNotaFinal().toString() : "-";

            // Normalizar estado 'PROMOCIONADO' -> 'APROBADA' visualmente, o mantener
            // logica?
            // El DTO espera: "PENDIENTE", "CURSANDO", "REGULAR", "APROBADA", "LIBRE"
            String estadoFinal = estadoStr;
            if (cur.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO ||
                    cur.getEstado() == com.sysacad.backend.modelo.enums.EstadoCursada.APROBADO) {
                estadoFinal = "APROBADA";
            }

            mapa.put(cur.getMateria().getId(), new EstadoMateria(estadoFinal, notaStr));
        }

        // 2. Procesar Exámenes (Sobreescribe si aprobó final)
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
            // Regla: Para cursar, correlativa debe estar REGULAR o APROBADA (PROMOCIONADA
            // cae en APROBADA)
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
