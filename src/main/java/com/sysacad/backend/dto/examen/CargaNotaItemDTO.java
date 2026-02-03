package com.sysacad.backend.dto.examen;

import com.sysacad.backend.modelo.enums.EstadoExamen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargaNotaItemDTO {
    private UUID idInscripcion;
    private BigDecimal nota;
    private EstadoExamen estado;
    private String tomo;
    private String folio;
}
