package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoResponse;
import com.sysacad.backend.modelo.InscripcionCursado;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Materia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InscripcionCursadoMapperTest {

    private InscripcionCursadoMapper mapper;

    @BeforeEach
    void setUp() throws Exception {
        mapper = Mappers.getMapper(InscripcionCursadoMapper.class);
        Field field = mapper.getClass().getDeclaredField("calificacionCursadaMapper");
        field.setAccessible(true);
        field.set(mapper, CalificacionCursadaMapper.INSTANCE);
    }

    @Test
    @DisplayName("Debe mapear InscripcionCursado a DTO")
    void toDTO_Success() {
        InscripcionCursado inscripcion = new InscripcionCursado();
        inscripcion.setId(UUID.randomUUID());
        
        Materia materia = new Materia();
        materia.setNombre("Materia X");
        inscripcion.setMateria(materia);
        
        Comision comision = new Comision();
        comision.setNombre("Comision X");
        inscripcion.setComision(comision);

        InscripcionCursadoResponse dto = mapper.toDTO(inscripcion);

        assertNotNull(dto);
        assertEquals("Materia X", dto.getNombreMateria());
        assertEquals("Comision X", dto.getNombreComision());
    }
}
