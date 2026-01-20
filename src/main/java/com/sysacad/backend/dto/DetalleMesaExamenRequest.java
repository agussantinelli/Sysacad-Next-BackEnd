package com.sysacad.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class DetalleMesaExamenRequest {
    private UUID idMesaExamen;
    private UUID idMateria;
    private LocalDate diaExamen;
    private LocalTime horaExamen;
}
