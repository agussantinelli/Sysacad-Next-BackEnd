package com.sysacad.backend.dto.examen;

import java.time.LocalDate;

public record ProfesorCertificadoDTO(
    String nombreCompleto,
    String dni,
    String legajo,
    String rol,
    LocalDate fechaEmision
) {}
