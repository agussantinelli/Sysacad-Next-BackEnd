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
@Table(name = "matriculaciones")
public class Matriculacion {

    @EmbeddedId
    private MatriculacionId id;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @Column(nullable = false, length = 20)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "id_facultad", referencedColumnName = "id_facultad", insertable = false, updatable = false),
            @JoinColumn(name = "nro_carrera", referencedColumnName = "nro_carrera", insertable = false, updatable = false),
            @JoinColumn(name = "nombre_plan", referencedColumnName = "nombre", insertable = false, updatable = false)
    })
    private PlanDeEstudio plan;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatriculacionId implements Serializable {
        @Column(name = "id_usuario")
        private UUID idUsuario;

        @Column(name = "id_facultad")
        private UUID idFacultad;

        @Column(name = "nro_carrera")
        private Integer nroCarrera;

        @Column(name = "nombre_plan", length = 100)
        private String nombrePlan;
    }
}
