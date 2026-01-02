package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
@Table(name = "salones")
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relación ManyToOne: Muchos salones pertenecen a una Facultad
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facultad", nullable = false)
    private FacultadRegional facultad;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 10)
    private String piso;
}