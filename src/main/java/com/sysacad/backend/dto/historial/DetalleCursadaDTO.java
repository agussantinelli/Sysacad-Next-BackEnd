package com.sysacad.backend.dto.historial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCursadaDTO {
    private LocalDate fechaInscripcion;
    private String comision;
    private String estado;
    private String nota;
}
