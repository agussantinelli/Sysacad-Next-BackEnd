package com.sysacad.backend.dto.inscripcion_examen;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class InscripcionExamenResponse {
    private UUID id;
    private String nombreAlumno;
    private String legajoAlumno;
    private String nombreMateria;
    private LocalDate fechaExamen;
    private LocalTime horaExamen;
    private LocalDateTime fechaInscripcion;
    private String estado;
    private java.math.BigDecimal nota;
}
