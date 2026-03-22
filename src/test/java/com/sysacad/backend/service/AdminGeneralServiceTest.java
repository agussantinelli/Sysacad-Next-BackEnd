package com.sysacad.backend.service;

import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.dto.materia.SimpleMateriaDTO;
import com.sysacad.backend.mapper.CarreraMapper;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.MateriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminGeneralServiceTest {

    @Mock
    private CarreraRepository carreraRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private CarreraMapper carreraMapper;

    @InjectMocks
    private AdminGeneralService adminGeneralService;

    @Test
    void obtenerTodasLasCarreras_DeberiaRetornarListaDeDTOs() {
        Carrera carrera = new Carrera();
        when(carreraRepository.findAll()).thenReturn(List.of(carrera));
        when(carreraMapper.toDTO(any())).thenReturn(new CarreraResponse());

        List<CarreraResponse> resultado = adminGeneralService.obtenerTodasLasCarreras();

        assertFalse(resultado.isEmpty());
        verify(carreraRepository).findAll();
    }

    @Test
    void buscarMateriasPorCarreraYNombre_DeberiaRetornarListaDeSimpleMateriaDTO() {
        UUID carreraId = UUID.randomUUID();
        Materia materia = new Materia();
        materia.setId(UUID.randomUUID());
        materia.setNombre("Programación");
        
        when(materiaRepository.findByCarreraIdAndNombreExcluding(carreraId, "Prog")).thenReturn(List.of(materia));

        List<SimpleMateriaDTO> resultado = adminGeneralService.buscarMateriasPorCarreraYNombre(carreraId, "Prog");

        assertFalse(resultado.isEmpty());
        assertEquals("Programación", resultado.get(0).getNombre());
    }
}
