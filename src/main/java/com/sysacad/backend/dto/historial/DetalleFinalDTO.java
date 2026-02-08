package com.sysacad.backend.dto.historial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleFinalDTO {
    private LocalDate fechaExamen;
    private String turno; 
    private String estado;
    private String nota;
    private String tomo;
    private String folio;
}
