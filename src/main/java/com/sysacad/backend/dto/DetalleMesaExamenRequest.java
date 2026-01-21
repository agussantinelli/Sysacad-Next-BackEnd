package com.sysacad.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class DetalleMesaExamenRequest {
    private UUID idMesaExamen;
    private Integer nroDetalle;
    private UUID idMateria;
    private UUID idPresidente;
    private LocalDate diaExamen;
    private LocalTime horaExamen;
}
