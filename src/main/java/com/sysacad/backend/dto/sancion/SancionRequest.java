package com.sysacad.backend.dto.sancion;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SancionRequest {
    private UUID idUsuario;
    private String motivo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
