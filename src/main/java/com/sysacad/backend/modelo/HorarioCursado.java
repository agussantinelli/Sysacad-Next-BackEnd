package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.DiaSemana;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "horarios_cursado")
public class HorarioCursado {

    @EmbeddedId
    private HorarioCursadoId id;

    @Column(name = "hora_hasta", nullable = false)
    private LocalTime horaHasta;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idComision")
    @JoinColumn(name = "id_comision")
    private Comision comision;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idMateria")
    @JoinColumn(name = "id_materia")
    private Materia materia;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HorarioCursadoId implements Serializable {

        @Column(name = "id_comision")
        private UUID idComision;

        @Column(name = "id_materia")
        private UUID idMateria;

        @Enumerated(EnumType.STRING)
        @Column(name = "dia", length = 20)
        private DiaSemana dia;

        @Column(name = "hora_desde")
        private LocalTime horaDesde;
    }
}