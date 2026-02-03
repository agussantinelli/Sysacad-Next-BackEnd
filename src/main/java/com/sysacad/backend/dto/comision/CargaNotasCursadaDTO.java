package com.sysacad.backend.dto.comision;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargaNotasCursadaDTO {
    private String concepto;
    private List<NotaCursadaItemDTO> notas;
}
