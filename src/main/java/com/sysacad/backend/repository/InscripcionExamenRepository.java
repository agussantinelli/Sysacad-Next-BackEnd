package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.InscripcionExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import com.sysacad.backend.modelo.DetalleMesaExamen;

@Repository
public interface InscripcionExamenRepository extends JpaRepository<InscripcionExamen, UUID> {
    List<InscripcionExamen> findByUsuarioId(UUID usuarioId);

    Optional<InscripcionExamen> findByUsuarioIdAndDetalleMesaExamenId(UUID usuarioId,
            DetalleMesaExamen.DetalleId detalleMesaExamenId);
}
