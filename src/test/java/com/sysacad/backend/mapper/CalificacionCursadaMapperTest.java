package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.calificacion.CalificacionCursadaResponse;
import com.sysacad.backend.modelo.CalificacionCursada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class CalificacionCursadaMapperTest {

    private final CalificacionCursadaMapper mapper = Mappers.getMapper(CalificacionCursadaMapper.class);

    @Test
    @DisplayName("Debe mapear CalificacionCursada a DTO")
    void toDTO_Success() {
        CalificacionCursada calificacion = new CalificacionCursada();
        calificacion.setId(10L);
        calificacion.setNota(8);

        CalificacionCursadaResponse dto = mapper.toDTO(calificacion);

        assertNotNull(dto);
        assertEquals(calificacion.getId(), dto.getId());
        assertEquals(calificacion.getNota(), dto.getNota());
    }
}
