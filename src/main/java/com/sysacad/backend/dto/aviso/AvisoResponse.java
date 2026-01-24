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
    private String estado;

    public AvisoResponse(Aviso aviso) {
        this.id = aviso.getId();
        this.titulo = aviso.getTitulo();
        this.descripcion = aviso.getDescripcion();
        this.fechaEmision = aviso.getFechaEmision();
        this.estado = aviso.getEstado();
    }
}
