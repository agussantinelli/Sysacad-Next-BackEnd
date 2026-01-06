package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.RolCargo;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolCargo cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    private Usuario profesor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", insertable = false, updatable = false)
    private Materia materia;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AsignacionMateriaId implements Serializable {
        @Column(name = "id_usuario")
        private UUID idUsuario;

        @Column(name = "id_materia")
        private UUID idMateria;
    }
}