package com.sysacad.backend.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class DetalleMesaRequest {
    private UUID idMesaExamen; // The Turno
    private UUID idMateria;
    private UUID idPresidente;
    private LocalDate diaExamen;
    private LocalTime horaExamen;
}
