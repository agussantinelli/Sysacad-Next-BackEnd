package com.sysacad.backend.dto.aviso;

import com.sysacad.backend.modelo.Aviso;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class AvisoResponse {
    private UUID id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaEmision;
    private com.sysacad.backend.modelo.enums.EstadoAviso estado;
}
