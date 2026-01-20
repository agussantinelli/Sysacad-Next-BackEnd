package com.sysacad.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class InscripcionCursadoResponse {
    private UUID id;
    private String nombreMateria;
    private String nombreComision;
    private Integer anioCursado;
    private String estado;
    private BigDecimal notaFinal;
    private LocalDate fechaPromocion;
    private LocalDateTime fechaInscripcion;
    private List<CalificacionCursadaResponse> calificaciones;
}
