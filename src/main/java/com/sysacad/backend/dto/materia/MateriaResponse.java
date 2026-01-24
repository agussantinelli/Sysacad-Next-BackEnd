package com.sysacad.backend.dto.materia;

import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.enums.CuatrimestreDictado;
import com.sysacad.backend.modelo.enums.DuracionMateria;
import com.sysacad.backend.modelo.enums.ModalidadMateria;
import com.sysacad.backend.modelo.enums.TipoMateria;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MateriaResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private TipoMateria tipoMateria;
    private DuracionMateria duracion;
    private ModalidadMateria modalidad;
    private CuatrimestreDictado cuatrimestreDictado;
    private Short horasCursado;
    private Boolean rendirLibre;
    private Boolean optativa;

    private List<SimpleMateriaDTO> correlativas;

    public MateriaResponse(Materia materia) {
        this.id = materia.getId();
        this.nombre = materia.getNombre();
        this.descripcion = materia.getDescripcion();
        this.tipoMateria = materia.getTipoMateria();
        this.duracion = materia.getDuracion();
        this.modalidad = materia.getModalidad();
        this.cuatrimestreDictado = materia.getCuatrimestreDictado();
        this.horasCursado = materia.getHorasCursado();
        this.rendirLibre = materia.getRendirLibre();
        this.optativa = materia.getOptativa();

        if (materia.getCorrelativas() != null) {
            this.correlativas = materia.getCorrelativas().stream()
                    .map(m -> new SimpleMateriaDTO(m.getId(), m.getNombre()))
                    .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    public static class SimpleMateriaDTO {
        private UUID id;
        private String nombre;

        public SimpleMateriaDTO(UUID id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
    }
}