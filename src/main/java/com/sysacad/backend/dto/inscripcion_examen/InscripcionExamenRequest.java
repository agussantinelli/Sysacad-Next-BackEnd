package com.sysacad.backend.dto.inscripcion_examen;

import lombok.Data;
import java.util.UUID;

@Data
public class InscripcionExamenRequest {
    private UUID idUsuario; 
    private UUID idMesaExamen;
    private Integer nroDetalle;
}
