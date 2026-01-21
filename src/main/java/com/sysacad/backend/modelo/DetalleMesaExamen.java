package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "detalle_mesa_examen")
public class DetalleMesaExamen {

    @EmbeddedId
    private DetalleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idMesaExamen")
    @JoinColumn(name = "id_mesa_examen")
    private MesaExamen mesaExamen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presidente", nullable = false)
    private Usuario presidente;

    @Column(name = "dia_examen", nullable = false)
    private LocalDate diaExamen;

    @Column(name = "hora_examen", nullable = false)
    private LocalTime horaExamen;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleId implements Serializable {
        @Column(name = "id_mesa_examen")
        private UUID idMesaExamen;

        @Column(name = "nro_detalle")
        private Integer nroDetalle;
    }
}
