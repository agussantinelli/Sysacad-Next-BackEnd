package com.sysacad.backend.dto.comision;

import java.util.List;
import java.util.UUID;

public record ComisionHorarioDTO(
        UUID idComision,
        String nombre,
        Integer anio,
        String turno,
        String salon,
        List<String> horarios,
        List<String> profesores,
        Long cantidadAlumnos
) {}
