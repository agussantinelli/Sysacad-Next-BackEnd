package com.sysacad.backend.dto.examen;

import java.time.LocalDate;
import java.util.UUID;

public record ProfesorMesaExamenDTO(
        UUID id,
        String nombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer cantidadMateriasInvolucradas
) {}
