package com.sysacad.backend.service;

import com.sysacad.backend.modelo.HorarioCursado;
import com.sysacad.backend.modelo.HorarioCursado.HorarioCursadoId;
import com.sysacad.backend.modelo.enums.DiaSemana;
import com.sysacad.backend.repository.HorarioCursadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class HorarioCursadoService {

    private final HorarioCursadoRepository horarioCursadoRepository;

    @Autowired
    public HorarioCursadoService(HorarioCursadoRepository horarioCursadoRepository) {
        this.horarioCursadoRepository = horarioCursadoRepository;
    }

    @Transactional
    public HorarioCursado registrarHorario(HorarioCursado horario) {
        HorarioCursadoId id = horario.getId();

        
        if (!horario.getHoraHasta().isAfter(id.getHoraDesde())) {
            throw new RuntimeException("La hora de finalización debe ser posterior a la hora de inicio");
        }

        
        List<HorarioCursado> solapamientos = horarioCursadoRepository.encontrarSolapamientos(
                id.getIdComision(),
                id.getDia(),
                id.getHoraDesde(),
                horario.getHoraHasta()
        );

        if (!solapamientos.isEmpty()) {
            throw new RuntimeException("Conflicto de horarios: El horario ingresado se superpone con otro existente en esta comisión.");
        }

        return horarioCursadoRepository.save(horario);
    }

    @Transactional(readOnly = true)
    public List<HorarioCursado> obtenerPorComision(UUID idComision) {
        return horarioCursadoRepository.findByIdIdComision(idComision);
    }

    @Transactional(readOnly = true)
    public List<HorarioCursado> obtenerPorMateria(UUID idMateria) {
        return horarioCursadoRepository.findByIdIdMateria(idMateria);
    }

    @Transactional
    public void eliminarHorario(UUID idComision, UUID idMateria, DiaSemana dia, LocalTime horaDesde) {
        
        HorarioCursadoId id = new HorarioCursadoId(idComision, idMateria, dia, horaDesde);

        if (!horarioCursadoRepository.existsById(id)) {
            throw new RuntimeException("El horario que intenta eliminar no existe");
        }
        horarioCursadoRepository.deleteById(id);
    }
}