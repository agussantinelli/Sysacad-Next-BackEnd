package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.comision.HorarioResponse;
import com.sysacad.backend.modelo.Horario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class HorarioMapperTest {

    private final HorarioMapper mapper = Mappers.getMapper(HorarioMapper.class);

    @Test
    @DisplayName("Debe mapear Horario a DTO")
    void toDTO_Success() {
        Horario horario = new Horario();
        horario.setDia("LUNES");
        horario.setHoraInicio(LocalTime.of(8, 0));
        horario.setHoraFin(LocalTime.of(10, 0));

        HorarioResponse dto = mapper.toDTO(horario);

        assertNotNull(dto);
        assertEquals("LUNES", dto.getDia());
        assertEquals(LocalTime.of(8, 0), dto.getHoraInicio());
    }
}
