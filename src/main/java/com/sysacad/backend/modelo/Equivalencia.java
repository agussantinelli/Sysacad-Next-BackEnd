package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "equivalencias")
public class Equivalencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "id_carrera_origen", nullable = false)
    private UUID idCarreraOrigen;

    @Column(name = "nro_plan_origen", nullable = false)
    private Integer nroPlanOrigen;

    @Column(name = "id_materia_origen", nullable = false)
    private UUID idMateriaOrigen;

    @Column(name = "id_carrera_destino", nullable = false)
    private UUID idCarreraDestino;

    @Column(name = "nro_plan_destino", nullable = false)
    private Integer nroPlanDestino;

    @Column(name = "id_materia_destino", nullable = false)
    private UUID idMateriaDestino;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "motivo", length = 255)
    private String motivo;
}
