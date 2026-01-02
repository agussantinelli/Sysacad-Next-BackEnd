package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "planes_de_estudios")
public class PlanDeEstudio {

    @EmbeddedId
    private PlanId id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "es_vigente", nullable = false)
    private Boolean esVigente = true;

    // Relación con Carrera (Solo lectura)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "id_facultad", referencedColumnName = "id_facultad", insertable = false, updatable = false),
            @JoinColumn(name = "id_carrera", referencedColumnName = "id_carrera", insertable = false, updatable = false)
    })
    private Carrera carrera;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanId implements Serializable {
        @Column(name = "id_facultad")
        private UUID idFacultad;

        @Column(name = "id_carrera", length = 20)
        private String idCarrera;

        @Column(name = "nombre", length = 100)
        private String nombre;
    }
}