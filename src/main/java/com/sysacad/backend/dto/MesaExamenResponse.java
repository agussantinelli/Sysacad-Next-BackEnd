package com.sysacad.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Data
public class MesaExamenResponse {
    private UUID id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<DetalleMesaExamenResponse> detalles;
}
