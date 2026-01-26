package com.sysacad.backend.dto.grupo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GrupoRequest {
    private String nombre;
    private String descripcion;
    private String tipo;
    private List<UUID> idsIntegrantes;
}
