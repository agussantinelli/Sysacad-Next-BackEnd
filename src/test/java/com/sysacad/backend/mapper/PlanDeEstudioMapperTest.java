package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.PlanDeEstudio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class PlanDeEstudioMapperTest {

    private final PlanDeEstudioMapper mapper = Mappers.getMapper(PlanDeEstudioMapper.class);

    @Test
    @DisplayName("Debe mapear PlanDeEstudio a DTO vinculando el nombre de la carrera")
    void toDTO_Success() {
        PlanDeEstudio plan = new PlanDeEstudio();
        plan.setNombre("Plan 2023");
        
        Carrera carrera = new Carrera();
        carrera.setNombre("Sistemas");
        plan.setCarrera(carrera);

        PlanDeEstudioResponse dto = mapper.toDTO(plan);

        assertNotNull(dto);
        assertEquals("Plan 2023", dto.getNombre());
        assertEquals("Sistemas", dto.getNombreCarrera());
    }
}
