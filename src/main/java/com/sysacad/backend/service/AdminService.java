package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.AdminEstadisticasDTO;
import com.sysacad.backend.dto.admin.AdminInscripcionDTO;
import com.sysacad.backend.modelo.InscripcionCursado;
import com.sysacad.backend.modelo.InscripcionExamen;
import com.sysacad.backend.modelo.enums.EstadoCursada;
import com.sysacad.backend.modelo.enums.EstadoExamen;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final InscripcionCursadoRepository inscripcionCursadoRepository;
    private final InscripcionExamenRepository inscripcionExamenRepository;

    @Autowired
    public AdminService(InscripcionCursadoRepository inscripcionCursadoRepository,
                        InscripcionExamenRepository inscripcionExamenRepository) {
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminInscripcionDTO> obtenerTodasInscripciones() {
        List<AdminInscripcionDTO> resultado = new ArrayList<>();

        // Cursadas
        List<InscripcionCursado> cursadas = inscripcionCursadoRepository.findAll();
        resultado.addAll(cursadas.stream().map(i -> new AdminInscripcionDTO(
                i.getId(),
                "CURSADA",
                i.getUsuario().getNombre(),
                i.getUsuario().getApellido(),
                i.getUsuario().getFotoPerfil(),
                i.getUsuario().getLegajo(),
                i.getMateria().getNombre(),
                i.getComision() != null ? i.getComision().getNombre() : "Sin Comisión",
                i.getFechaInscripcion(),
                i.getEstado().toString()
        )).collect(Collectors.toList()));

        // Examenes
        List<InscripcionExamen> examenes = inscripcionExamenRepository.findAll();
        resultado.addAll(examenes.stream().map(i -> new AdminInscripcionDTO(
                i.getId(),
                "EXAMEN",
                i.getUsuario().getNombre(),
                i.getUsuario().getApellido(),
                i.getUsuario().getFotoPerfil(),
                i.getUsuario().getLegajo(),
                i.getDetalleMesaExamen().getMateria().getNombre(),
                "N/A",
                i.getFechaInscripcion(),
                i.getEstado().toString()
        )).collect(Collectors.toList()));

        // Ordenar por fecha descendente
        resultado.sort(Comparator.comparing(AdminInscripcionDTO::getFechaInscripcion).reversed());

        return resultado;
    }

    @Transactional(readOnly = true)
    public AdminEstadisticasDTO obtenerEstadisticas(Integer anio, UUID facultadId, UUID carreraId) {
        AdminEstadisticasDTO dto = new AdminEstadisticasDTO();

        // Cursadas
        dto.setCantidadTotalAlumnos(inscripcionCursadoRepository.countAlumnosAdmin(anio, facultadId, carreraId, null));
        dto.setCantidadPromocionados(inscripcionCursadoRepository.countAlumnosAdmin(anio, facultadId, carreraId, EstadoCursada.PROMOCIONADO));
        dto.setCantidadRegulares(inscripcionCursadoRepository.countAlumnosAdmin(anio, facultadId, carreraId, EstadoCursada.REGULAR));
        dto.setCantidadLibres(inscripcionCursadoRepository.countAlumnosAdmin(anio, facultadId, carreraId, EstadoCursada.LIBRE));
        
        Double promedio = inscripcionCursadoRepository.calculateAverageGradeAdmin(anio, facultadId, carreraId);
        dto.setNotaPromedio(promedio != null ? BigDecimal.valueOf(promedio) : BigDecimal.ZERO);

        // Examenes
        dto.setCantidadTotalInscriptosExamen(inscripcionExamenRepository.countExamenesAdmin(anio, facultadId, carreraId, null));
        dto.setCantidadAprobadosExamen(inscripcionExamenRepository.countExamenesAdmin(anio, facultadId, carreraId, EstadoExamen.APROBADO));
        dto.setCantidadDesaprobadosExamen(inscripcionExamenRepository.countExamenesAdmin(anio, facultadId, carreraId, EstadoExamen.DESAPROBADO));
        dto.setCantidadAusentesExamen(inscripcionExamenRepository.countExamenesAdmin(anio, facultadId, carreraId, EstadoExamen.AUSENTE));

        return dto;
    }

    @Transactional
    public void eliminarInscripcion(UUID id, String tipo) {
        if ("CURSADA".equalsIgnoreCase(tipo)) {
            inscripcionCursadoRepository.deleteById(id);
        } else if ("EXAMEN".equalsIgnoreCase(tipo)) {
            inscripcionExamenRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Tipo de inscripción no válido: " + tipo);
        }
    }
}
