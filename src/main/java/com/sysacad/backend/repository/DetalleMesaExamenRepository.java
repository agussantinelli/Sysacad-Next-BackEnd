package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.DetalleMesaExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface DetalleMesaExamenRepository extends JpaRepository<DetalleMesaExamen, DetalleMesaExamen.DetalleId> {
    
    List<DetalleMesaExamen> findByMesaExamenId(UUID mesaExamenId);

    List<DetalleMesaExamen> findByMateriaId(UUID materiaId);

    @org.springframework.data.jpa.repository.Query("SELECT d FROM DetalleMesaExamen d JOIN FETCH d.mesaExamen LEFT JOIN FETCH d.presidente WHERE d.materia.id = :materiaId")
    List<DetalleMesaExamen> findByMateriaIdWithDetails(@org.springframework.data.repository.query.Param("materiaId") UUID materiaId);

    List<DetalleMesaExamen> findByPresidenteId(UUID presidenteId);

    List<DetalleMesaExamen> findByAuxiliaresId(UUID auxiliarId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT YEAR(d.diaExamen) FROM DetalleMesaExamen d LEFT JOIN d.auxiliares a WHERE d.presidente.id = :profesorId OR a.id = :profesorId ORDER BY YEAR(d.diaExamen) DESC")
    List<Integer> findDistinctYearsByProfesorId(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId);
}
