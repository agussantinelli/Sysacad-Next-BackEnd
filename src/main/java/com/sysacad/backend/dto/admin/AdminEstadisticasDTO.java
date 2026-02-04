package com.sysacad.backend.dto.admin;

import com.sysacad.backend.dto.profesor.ProfesorEstadisticasDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdminEstadisticasDTO extends ProfesorEstadisticasDTO {
    // Hereda todos los campos de estadisticas de profesor
    // Se utilizará para devolver las estadísticas generales filtradas
}
