package com.sysacad.backend.dto.comision;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotaCursadaItemDTO {
    private UUID idInscripcion;
    private BigDecimal nota;
    private String estado;
}
