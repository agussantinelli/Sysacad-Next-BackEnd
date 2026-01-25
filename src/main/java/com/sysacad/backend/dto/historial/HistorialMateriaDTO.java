package com.sysacad.backend.dto.historial;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialMateriaDTO {
    private String nombreMateria;
    private List<DetalleCursadaDTO> cursadas;
    private List<DetalleFinalDTO> finales;
}
