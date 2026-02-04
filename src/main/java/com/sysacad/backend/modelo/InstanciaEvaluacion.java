package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "instancias_evaluacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanciaEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre; // Ej: "1er Parcial", "TP Integrador"
}
