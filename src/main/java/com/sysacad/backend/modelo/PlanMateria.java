package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Table(name = "plan_materias")
public class PlanMateria {

    @EmbeddedId
    private PlanMateriaId id;

    @Column(name = "codigo_materia", nullable = false, length = 20)
    private String codigoMateria;

    @Column(name = "nivel", nullable = false)
    private Short nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "id_facultad", referencedColumnName = "id_facultad", insertable = false, updatable = false),
            @JoinColumn(name = "nro_carrera", referencedColumnName = "nro_carrera", insertable = false, updatable = false),
            @JoinColumn(name = "nro_plan", referencedColumnName = "nro_plan", insertable = false, updatable = false)
    })
    private PlanDeEstudio plan;

    // Relación con Materia (FK Simple)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", insertable = false, updatable = false)
    private Materia materia;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanMateriaId implements Serializable {
        @Column(name = "id_facultad")
        private UUID idFacultad;

        @Column(name = "nro_carrera")
        private Integer nroCarrera;

        @Column(name = "nro_plan")
        private Integer nroPlan;

        @Column(name = "id_materia")
        private UUID idMateria;
    }
}