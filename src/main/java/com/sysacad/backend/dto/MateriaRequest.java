package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.enums.CuatrimestreDictado;
import com.sysacad.backend.modelo.enums.DuracionMateria;
import com.sysacad.backend.modelo.enums.ModalidadMateria;
import com.sysacad.backend.modelo.enums.TipoMateria;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MateriaRequest {
    private String nombre;
    private String descripcion;
    private TipoMateria tipoMateria;
    private DuracionMateria duracion;
    private ModalidadMateria modalidad; // Opcional en el JSON (si es null, el controller o la entidad usan default)
    private CuatrimestreDictado cuatrimestreDictado;
    private Short horasCursado;
    private Boolean rendirLibre;
    private Boolean optativa;

    private List<UUID> idsCorrelativas;
}