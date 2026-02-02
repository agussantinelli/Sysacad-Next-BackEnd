package com.sysacad.backend.dto.materia;

import com.sysacad.backend.modelo.enums.RolCargo;
import java.util.UUID;

public record MateriaProfesorDTO(
        UUID id,
        String nombre,
        Integer nivel,
        String plan, 
        RolCargo cargo
) {}
