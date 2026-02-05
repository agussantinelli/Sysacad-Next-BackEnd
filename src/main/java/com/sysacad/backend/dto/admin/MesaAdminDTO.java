package com.sysacad.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesaAdminDTO {
    private UUID idMesaExamen;
    private Integer nroDetalle;
    private String materia;
    private String turno;
    private LocalDateTime fecha;
    private long cantidadInscriptos;
    private boolean abierta;
}
