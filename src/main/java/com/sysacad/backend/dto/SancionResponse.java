package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.Sancion;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SancionResponse {
    private UUID id;
    private UUID idUsuario;
    private String nombreUsuario;
    private String legajoUsuario;
    private String motivo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public SancionResponse(Sancion sancion) {
        this.id = sancion.getId();
        this.motivo = sancion.getMotivo();
        this.fechaInicio = sancion.getFechaInicio();
        this.fechaFin = sancion.getFechaFin();
        if (sancion.getUsuario() != null) {
            this.idUsuario = sancion.getUsuario().getId();
            this.nombreUsuario = sancion.getUsuario().getNombre() + " " + sancion.getUsuario().getApellido();
            this.legajoUsuario = String.valueOf(sancion.getUsuario().getLegajo());
        }
    }
}
