package com.sysacad.backend.dto.mesa_examen;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MesaExamenRequest {
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
