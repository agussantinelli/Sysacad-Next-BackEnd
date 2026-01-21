package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "inscripciones_examen")
public class InscripcionExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "id_mesa_examen", referencedColumnName = "id_mesa_examen", nullable = false),
            @JoinColumn(name = "nro_detalle", referencedColumnName = "nro_detalle", nullable = false)
    })
    private DetalleMesaExamen detalleMesaExamen;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private com.sysacad.backend.modelo.enums.EstadoExamen estado;

    @Column(precision = 4, scale = 2)
    private java.math.BigDecimal nota;
}
