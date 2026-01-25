package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inscripciones_cursado")
public class InscripcionCursado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comision", nullable = false)
    private Comision comision;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private com.sysacad.backend.modelo.enums.EstadoCursada estado;

    @Column(name = "nota_final", precision = 4, scale = 2)
    private BigDecimal notaFinal;

    @Column(name = "fecha_promocion")
    private LocalDate fechaPromocion;

    @Column(name = "fecha_regularidad")
    private LocalDate fechaRegularidad;

    @Column(length = 10)
    private String tomo;

    @Column(length = 10)
    private String folio;

    @OneToMany(mappedBy = "inscripcionCursado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalificacionCursada> calificaciones = new ArrayList<>();
}
