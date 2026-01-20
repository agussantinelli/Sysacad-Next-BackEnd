package com.sysacad.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class DetalleMesaExamenResponse {
    private UUID id;
    private String nombreMateria;
    private UUID idMateria; // added for convenience
    private String nombrePresidente;
    private UUID idPresidente;
    private LocalDate diaExamen;
    private LocalTime horaExamen;
}
