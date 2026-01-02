package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Table(name = "asignaciones_materia")
public class AsignacionMateria {

    @EmbeddedId
    private AsignacionMateriaId id;

    @Column(nullable = false, length = 50)
    private String rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profesor", insertable = false, updatable = false)
    private Profesor profesor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", insertable = false, updatable = false)
    private Materia materia;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AsignacionMateriaId implements Serializable {
        @Column(name = "id_profesor")
        private UUID idProfesor;

        @Column(name = "id_materia")
        private UUID idMateria;
    }
}