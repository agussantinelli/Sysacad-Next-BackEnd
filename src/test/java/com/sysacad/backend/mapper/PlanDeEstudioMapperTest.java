package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.PlanDeEstudio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlanDeEstudioMapperTest {

    private final PlanDeEstudioMapper mapper = Mappers.getMapper(PlanDeEstudioMapper.class);

    @Test
    @DisplayName("Debe mapear PlanDeEstudio a DTO vinculando el nombre de la carrera")
    void toDTO_Success() {
        PlanDeEstudio plan = new PlanDeEstudio();
        PlanDeEstudio.PlanId id = new PlanDeEstudio.PlanId();
        id.setNroPlan(2023);
        id.setIdCarrera(UUID.randomUUID());
        plan.setId(id);
        plan.setNombre("Plan 2023");
        
        Carrera carrera = new Carrera();
        carrera.setId(id.getIdCarrera());
        carrera.setNombre("Sistemas");
        plan.setCarrera(carrera);

        PlanDeEstudioResponse dto = mapper.toDTO(plan);

        assertNotNull(dto);
        assertEquals("Plan 2023", dto.getNombrePlan());
        assertEquals("Sistemas", dto.getNombreCarrera());
    }
}
