package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenResponse;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.InscripcionExamen;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InscripcionExamenMapperTest {

    private final InscripcionExamenMapper mapper = Mappers.getMapper(InscripcionExamenMapper.class);

    @Test
    @DisplayName("Debe mapear InscripcionExamen a DTO")
    void toDTO_Success() {
        // Arrange
        InscripcionExamen inscripcion = new InscripcionExamen();
        inscripcion.setId(UUID.randomUUID());
        
        Usuario alumno = new Usuario();
        alumno.setId(UUID.randomUUID());
        alumno.setNombre("Maria");
        alumno.setLegajo("LEG001");
        inscripcion.setUsuario(alumno);
        
        DetalleMesaExamen detalle = new DetalleMesaExamen();
        DetalleMesaExamen.DetalleId detId = new DetalleMesaExamen.DetalleId();
        detId.setIdMesaExamen(UUID.randomUUID());
        detId.setNroDetalle(1);
        detalle.setId(detId);
        Materia materia = new Materia();
        materia.setId(UUID.randomUUID());
        materia.setNombre("Fisica");
        detalle.setMateria(materia);
        detalle.setDiaExamen(LocalDate.of(2024, 12, 1));
        inscripcion.setDetalleMesaExamen(detalle);

        // Act
        InscripcionExamenResponse dto = mapper.toDTO(inscripcion);

        // Assert
        assertNotNull(dto);
        assertEquals("Maria", dto.getNombreAlumno());
        assertEquals("Fisica", dto.getNombreMateria());
        assertEquals(LocalDate.of(2024, 12, 1), dto.getFechaExamen());
    }
}
