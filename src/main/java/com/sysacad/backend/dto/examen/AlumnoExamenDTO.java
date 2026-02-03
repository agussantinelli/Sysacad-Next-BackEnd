package com.sysacad.backend.dto.examen;

import com.sysacad.backend.modelo.enums.EstadoExamen;
import java.math.BigDecimal;
import java.util.UUID;

public record AlumnoExamenDTO(
        UUID idInscripcion,
        String nombre,
        String apellido,
        Long legajo,
        EstadoExamen estado,
        BigDecimal nota
) {}
