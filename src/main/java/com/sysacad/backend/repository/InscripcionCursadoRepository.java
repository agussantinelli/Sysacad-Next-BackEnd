package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.InscripcionCursado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InscripcionCursadoRepository extends JpaRepository<InscripcionCursado, UUID> {
    List<InscripcionCursado> findByUsuarioId(UUID idUsuario);

    Optional<InscripcionCursado> findByUsuarioIdAndMateriaId(UUID idUsuario, UUID idMateria);

    List<InscripcionCursado> findByComisionId(UUID idComision);

    List<InscripcionCursado> findByMateriaId(UUID idMateria);

    List<InscripcionCursado> findByUsuarioIdAndEstado(UUID idUsuario, com.sysacad.backend.modelo.enums.EstadoCursada estado);
}
