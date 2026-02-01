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
    private String ubicacion; // "Aula 305 - Piso 3"
    private List<String> horarios; // "Lunes 18:00 - 22:00", "Jueves 18:00 - 22:00"
    private List<String> profesores; // "Nicolas Cabello", "Sandra Civiero"
    
    // Estado de elegibilidad para el alumno solicitante
    private boolean habilitada; // true si se puede inscribir
    private String mensaje;     // "Disponible", "Superposición con Análisis II", "Cupo lleno"
}
