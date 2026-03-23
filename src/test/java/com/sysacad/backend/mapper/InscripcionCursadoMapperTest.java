package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoResponse;
import com.sysacad.backend.modelo.InscripcionCursado;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.Comision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class InscripcionCursadoMapperTest {

    private final InscripcionCursadoMapper mapper = Mappers.getMapper(InscripcionCursadoMapper.class);

    @Test
    @DisplayName("Debe mapear InscripcionCursado a DTO")
    void toDTO_Success() {
        InscripcionCursado inscripcion = new InscripcionCursado();
        
        Usuario alumno = new Usuario();
        alumno.setNombre("Alex");
        alumno.setLegajo("LEG789");
        inscripcion.setUsuario(alumno);
        
        Comision comision = new Comision();
        comision.setNombre("Comision X");
        inscripcion.setComision(comision);

        InscripcionCursadoResponse dto = mapper.toDTO(inscripcion);

        assertNotNull(dto);
        assertEquals("Alex", dto.getNombreAlumno());
        assertEquals("Comision X", dto.getNombreComision());
    }
}
