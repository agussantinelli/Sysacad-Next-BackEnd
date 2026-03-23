package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenResponse;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DetalleMesaExamenMapperTest {

    private final DetalleMesaExamenMapper mapper = Mappers.getMapper(DetalleMesaExamenMapper.class);

    @Test
    @DisplayName("Debe mapear DetalleMesaExamen a DTO incluyendo expresión Java para nombre de presidente")
    void toDTO_WithJavaExpression() {
        // Arrange
        DetalleMesaExamen detalle = new DetalleMesaExamen();
        DetalleMesaExamen.DetalleId id = new DetalleMesaExamen.DetalleId();
        id.setIdMesaExamen(UUID.randomUUID());
        id.setNroDetalle(1);
        detalle.setId(id);
        
        Materia materia = new Materia();
        materia.setId(UUID.randomUUID());
        materia.setNombre("Algoritmos");
        detalle.setMateria(materia);
        
        Usuario presidente = new Usuario();
        presidente.setId(UUID.randomUUID());
        presidente.setNombre("Juan");
        presidente.setApellido("Perez");
        detalle.setPresidente(presidente);

        // Act
        DetalleMesaExamenResponse dto = mapper.toDTO(detalle);

        // Assert
        assertNotNull(dto);
        assertEquals("Algoritmos", dto.getNombreMateria());
        assertEquals("Juan Perez", dto.getNombrePresidente());
    }
}
