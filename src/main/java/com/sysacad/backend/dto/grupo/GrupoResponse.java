package com.sysacad.backend.dto.grupo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrupoResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private String estado;
    private LocalDateTime fechaCreacion;
}
