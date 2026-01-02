package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.TipoInscripcion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "inscripciones")
public class Inscripcion {

    @EmbeddedId
    private InscripcionId id;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(nullable = false, length = 50)
    private String condicion;

    @Column(name = "descripcion_condicion", columnDefinition = "TEXT")
    private String descripcionCondicion;

    @Column(name = "fecha_promocion")
    private LocalDate fechaPromocion;

    @Column(name = "nota_final", precision = 4, scale = 2)
    private BigDecimal notaFinal;

    @Column(length = 20)
    private String tomo;

    @Column(length = 20)
    private String folio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comision", insertable = false, updatable = false)
    private Comision comision;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InscripcionId implements Serializable {
        @Column(name = "id_usuario")
        private UUID idUsuario;

        @Column(name = "id_comision")
        private UUID idComision;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo", length = 20)
        private TipoInscripcion tipo;

        @Column(name = "veces_tipo")
        private Integer vecesTipo;
    }
}