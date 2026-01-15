package com.sysacad.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class AvisoRequest {
    private String titulo;
    private String descripcion;
    private String estado;
}
