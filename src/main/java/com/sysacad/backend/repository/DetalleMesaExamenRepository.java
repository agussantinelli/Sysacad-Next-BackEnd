package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.DetalleMesaExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface DetalleMesaExamenRepository extends JpaRepository<DetalleMesaExamen, UUID> {
    List<DetalleMesaExamen> findByMesaExamenId(UUID mesaExamenId);

    List<DetalleMesaExamen> findByMateriaId(UUID materiaId);
}
