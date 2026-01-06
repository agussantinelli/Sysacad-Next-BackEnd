package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.Inscripcion.InscripcionId;
import com.sysacad.backend.modelo.enums.TipoInscripcion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Table(name = "calificaciones")
public class Calificacion {

    @EmbeddedId
    private CalificacionId id;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal nota;

    // Relación JPA para integridad referencial (mapea las columnas del ID a la entidad padre)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", insertable = false, updatable = false),
            @JoinColumn(name = "id_comision", referencedColumnName = "id_comision", insertable = false, updatable = false),
            @JoinColumn(name = "tipo_inscripcion", referencedColumnName = "tipo", insertable = false, updatable = false),
            @JoinColumn(name = "veces_tipo_inscripcion", referencedColumnName = "veces_tipo", insertable = false, updatable = false)
    })
    private Inscripcion inscripcion;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalificacionId implements Serializable {

        @Column(name = "id_usuario")
        private UUID idUsuario;

        @Column(name = "id_comision")
        private UUID idComision;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_inscripcion", length = 20)
        private TipoInscripcion tipoInscripcion;

        @Column(name = "veces_tipo_inscripcion")
        private Integer vecesTipoInscripcion;

        @Column(name = "concepto", length = 100)
        private String concepto;

        public InscripcionId toInscripcionId() {
            // Nota: El orden de argumentos debe coincidir con el constructor de InscripcionId
            return new InscripcionId(idUsuario, idComision, tipoInscripcion, vecesTipoInscripcion);
        }
    }
}