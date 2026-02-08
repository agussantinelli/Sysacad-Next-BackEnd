package com.sysacad.backend.dto.inscripcion_cursado;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComisionDisponibleDTO {
    private UUID idComision;
    private String nombreComision;
    private String turno;
    private String ubicacion; 
    private List<String> horarios; 
    private List<String> profesores; 
    
    
    private boolean habilitada; 
    private String mensaje;     
}
