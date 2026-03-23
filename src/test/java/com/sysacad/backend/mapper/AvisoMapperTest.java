package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.aviso.AvisoRequest;
import com.sysacad.backend.dto.aviso.AvisoResponse;
import com.sysacad.backend.modelo.Aviso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class AvisoMapperTest {

    private final AvisoMapper mapper = Mappers.getMapper(AvisoMapper.class);

    @Test
    @DisplayName("Debe mapear Aviso a AvisoResponse correctamente")
    void toDTO_Success() {
        Aviso aviso = new Aviso();
        aviso.setId(1L);
        aviso.setTitulo("Test");
        aviso.setMensaje("Mensaje");

        AvisoResponse dto = mapper.toDTO(aviso);

        assertNotNull(dto);
        assertEquals(aviso.getId(), dto.getId());
        assertEquals(aviso.getTitulo(), dto.getTitulo());
    }

    @Test
    @DisplayName("Debe mapear AvisoRequest a Aviso correctamente")
    void toEntity_Success() {
        AvisoRequest request = new AvisoRequest();
        request.setTitulo("New Title");
        request.setMensaje("New Message");

        Aviso aviso = mapper.toEntity(request);

        assertNotNull(aviso);
        assertEquals(request.getTitulo(), aviso.getTitulo());
        assertEquals(request.getMensaje(), aviso.getMensaje());
    }
}
