package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.comision.ComisionResponse;
import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComisionMapperTest {

    @InjectMocks
    private ComisionMapperImpl mapper; // MapStruct genera la implementación

    @Mock
    private AsignacionMateriaRepository asignacionMateriaRepository;

    @Test
    @DisplayName("Debe mapear Comision a DTO con lógica compleja de materias y profesores")
    void toDTO_ComplexLogic() {
        // Arrange
        Comision comision = new Comision();
        comision.setNombre("Comision A");
        
        Materia materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Materia 1");
        
        Usuario profesor = new Usuario();
        profesor.setId(2L);
        profesor.setNombre("Prof");
        profesor.setApellido("Esor");
        profesor.setLegajo("LEG123");
        
        comision.setMaterias(Set.of(materia));
        comision.setProfesores(Set.of(profesor));

        // Mock repository existsById
        when(asignacionMateriaRepository.existsById(any(AsignacionMateria.AsignacionMateriaId.class))).thenReturn(true);

        // Act
        ComisionResponse dto = mapper.toDTO(comision);

        // Assert
        assertNotNull(dto);
        assertEquals("Comision A", dto.getNombre());
        assertNotNull(dto.getMateriasDetalle());
        assertEquals(1, dto.getMateriasDetalle().size());
        assertEquals("Materia 1", dto.getMateriasDetalle().get(0).getNombre());
        assertEquals(1, dto.getMateriasDetalle().get(0).getProfesoresDocentes().size());
        assertEquals("LEG123", dto.getMateriasDetalle().get(0).getProfesoresDocentes().get(0).getLegajo());
    }
}
