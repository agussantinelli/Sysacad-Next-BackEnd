package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "detalle_mesa_examen")
public class DetalleMesaExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa_examen", nullable = false)
    private MesaExamen mesaExamen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @Column(name = "dia_examen", nullable = false)
    private LocalDate diaExamen;

    @Column(name = "hora_examen", nullable = false)
    private LocalTime horaExamen;
}
