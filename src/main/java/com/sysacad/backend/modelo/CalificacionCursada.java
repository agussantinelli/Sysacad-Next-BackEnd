package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "calificaciones_cursada")
public class CalificacionCursada {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inscripcion_cursado", nullable = false)
    private InscripcionCursado inscripcionCursado;

    @Column(nullable = false, length = 100)
    private String descripcion; // e.g., "1er Parcial"

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal nota;

    @Column(nullable = false)
    private LocalDate fecha;
}
