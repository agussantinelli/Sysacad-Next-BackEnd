package com.sysacad.backend.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MesaRequest {
    private UUID idMateria;
    private String turno; // Ej: "Febrero", "Julio"
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin; // Para inscripciones
    private LocalDateTime fechaExamen;
}
