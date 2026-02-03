package com.sysacad.backend.dto.examen;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ProfesorDetalleExamenDTO(
        UUID idMesaExamen,
        Integer nroDetalle,
        UUID idMateria,
        String nombreMateria,
        String anioMateria,
        LocalDate fecha,
        LocalTime hora,
        Long cantidadInscriptos,
        boolean todosCorregidos,
        List<MiembroTribunalDTO> tribunal
) {}
