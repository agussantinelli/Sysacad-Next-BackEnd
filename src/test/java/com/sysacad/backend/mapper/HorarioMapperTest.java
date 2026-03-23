package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.horario.HorarioResponse;
import com.sysacad.backend.modelo.HorarioCursado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.sysacad.backend.modelo.enums.DiaSemana;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HorarioMapperTest {

    private final HorarioMapper mapper = Mappers.getMapper(HorarioMapper.class);

    @Test
    @DisplayName("Debe mapear HorarioCursado a DTO")
    void toDTO_Success() {
        HorarioCursado horario = new HorarioCursado();
        HorarioCursado.HorarioCursadoId id = new HorarioCursado.HorarioCursadoId();
        id.setDia(DiaSemana.LUNES);
        id.setHoraDesde(LocalTime.of(8, 0));
        id.setIdComision(UUID.randomUUID());
        id.setIdMateria(UUID.randomUUID());
        
        horario.setId(id);
        horario.setHoraHasta(LocalTime.of(10, 0));

        HorarioResponse dto = mapper.toDTO(horario);

        assertNotNull(dto);
        assertEquals(DiaSemana.LUNES, dto.getDia());
        assertEquals(LocalTime.of(8, 0), dto.getHoraDesde());
        assertEquals(LocalTime.of(10, 0), dto.getHoraHasta());
    }
}
