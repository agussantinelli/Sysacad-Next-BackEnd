package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.calificacion_cursada.CalificacionCursadaResponse;
import com.sysacad.backend.modelo.CalificacionCursada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CalificacionCursadaMapperTest {

    private final CalificacionCursadaMapper mapper = Mappers.getMapper(CalificacionCursadaMapper.class);

    @Test
    @DisplayName("Debe mapear CalificacionCursada a DTO")
    void toDTO_Success() {
        UUID id = UUID.randomUUID();
        CalificacionCursada calificacion = new CalificacionCursada();
        calificacion.setId(id);
        calificacion.setNota(new BigDecimal("8.00"));

        CalificacionCursadaResponse dto = mapper.toDTO(calificacion);

        assertNotNull(dto);
        assertEquals(calificacion.getId(), dto.getId());
        assertEquals(calificacion.getNota(), dto.getNota());
    }
}
