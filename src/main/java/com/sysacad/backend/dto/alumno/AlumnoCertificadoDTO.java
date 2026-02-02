package com.sysacad.backend.dto.alumno;

import java.time.LocalDate;

public record AlumnoCertificadoDTO(
    String nombreCompleto,
    String dni,
    String legajo,
    String carrera,
    String facultad,
    LocalDate fechaEmision
) {}
