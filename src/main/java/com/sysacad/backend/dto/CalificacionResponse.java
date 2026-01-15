package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.Calificacion;
import com.sysacad.backend.modelo.enums.TipoInscripcion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CalificacionResponse {
    private UUID idUsuario;
    private String nombreUsuario;
    private String legajoUsuario;
    private UUID idComision;
    private String nombreComision;
    private String materia;

    private TipoInscripcion tipoInscripcion;
    private Integer vecesTipoInscripcion;
    private String concepto;
    private BigDecimal nota;
    private LocalDate fecha;

    public CalificacionResponse(Calificacion calificacion) {
        if (calificacion.getId() != null) {
            this.idUsuario = calificacion.getId().getIdUsuario();
            this.idComision = calificacion.getId().getIdComision();
            this.tipoInscripcion = calificacion.getId().getTipoInscripcion();
            this.vecesTipoInscripcion = calificacion.getId().getVecesTipoInscripcion();
            this.concepto = calificacion.getId().getConcepto();
        }
        this.nota = calificacion.getNota();
        // Calificacion no tiene fecha en el modelo actual, se elimina campo o se toma
        // de otra parte
        // this.fecha = null;

        if (calificacion.getInscripcion() != null && calificacion.getInscripcion().getUsuario() != null) {
            this.nombreUsuario = calificacion.getInscripcion().getUsuario().getNombre() + " "
                    + calificacion.getInscripcion().getUsuario().getApellido();
            this.legajoUsuario = String.valueOf(calificacion.getInscripcion().getUsuario().getLegajo());
        }
    }
}
