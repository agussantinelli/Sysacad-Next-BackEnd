package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.enums.CuatrimestreDictado;
import com.sysacad.backend.modelo.enums.DuracionMateria;
import com.sysacad.backend.modelo.enums.TipoMateria;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MateriaRequest {
    // No necesitamos ID en el request de creación
    private String nombre;
    private String descripcion;
    private TipoMateria tipoMateria;
    private DuracionMateria duracion;
    private CuatrimestreDictado cuatrimestreDictado;
    private Short horasCursado;
    private Boolean rendirLibre;
    private Boolean optativa;

    // Recibimos solo los IDs de las correlativas para buscarlas en BD
    private List<UUID> idsCorrelativas;
}