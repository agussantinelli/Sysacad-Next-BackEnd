package com.sysacad.backend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class InscripcionExamenRequest {
    private UUID idUsuario; // Optional if we get it from token, but good for admin
    private UUID idMesaExamen;
    private Integer nroDetalle;
}
