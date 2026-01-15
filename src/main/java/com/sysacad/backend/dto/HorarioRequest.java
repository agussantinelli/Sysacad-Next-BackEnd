package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.enums.DiaSemana;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class HorarioRequest {
    private UUID idComision;
    private UUID idMateria;
    private DiaSemana dia;
    private LocalTime horaDesde;
    private LocalTime horaHasta;
}
