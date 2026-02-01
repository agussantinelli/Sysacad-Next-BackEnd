package com.sysacad.backend.dto.mesa_examen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesaExamenDisponibleDTO {
    private UUID idDetalleMesa;
    private String nombreMesa;
    private LocalDate fecha;
    private LocalTime hora;
    private String presidente;
    private boolean habilitada;
    private String mensaje;
}
