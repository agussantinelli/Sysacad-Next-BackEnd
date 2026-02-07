package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.RolGrupo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "miembros_grupo")
public class MiembroGrupo {

    @EmbeddedId
    private MiembroGrupoId id;

    @ManyToOne
    @MapsId("idGrupo")
    @JoinColumn(name = "id_grupo")
    private Grupo grupo;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_interno", nullable = false, length = 20)
    private RolGrupo rol = RolGrupo.MIEMBRO;

    @Column(name = "fecha_union")
    private LocalDateTime fechaUnion = LocalDateTime.now();

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    private void validarRol() {
        if (this.usuario != null && 
            this.usuario.getRol() == com.sysacad.backend.modelo.enums.RolUsuario.ESTUDIANTE) {
            this.rol = RolGrupo.MIEMBRO;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class MiembroGrupoId implements Serializable {
        private UUID idGrupo;
        private UUID idUsuario;
    }
}
