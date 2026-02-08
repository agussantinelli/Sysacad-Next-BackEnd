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

    List<InscripcionExamen> findByUsuarioIdAndEstado(UUID usuarioId, com.sysacad.backend.modelo.enums.EstadoExamen estado);

    boolean existsByUsuarioIdAndDetalleMesaExamen_MateriaIdAndEstado(UUID usuarioId, UUID materiaId, com.sysacad.backend.modelo.enums.EstadoExamen estado);

    long countByUsuarioIdAndDetalleMesaExamen_MateriaIdAndEstadoIn(UUID usuarioId, UUID materiaId, List<com.sysacad.backend.modelo.enums.EstadoExamen> estados);

    boolean existsByUsuarioIdAndMateriaIdAndEstado(UUID usuarioId, UUID materiaId, com.sysacad.backend.modelo.enums.EstadoExamen estado);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionExamen i WHERE i.detalleMesaExamen.id = :detalleId")
    Long countByDetalleMesaExamenId(@org.springframework.data.repository.query.Param("detalleId") DetalleMesaExamen.DetalleId detalleId);

    List<InscripcionExamen> findByDetalleMesaExamenId(DetalleMesaExamen.DetalleId detalleId);

    long countByDetalleMesaExamenIdAndEstado(DetalleMesaExamen.DetalleId detalleId, com.sysacad.backend.modelo.enums.EstadoExamen estado);

    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionExamen i JOIN i.detalleMesaExamen d LEFT JOIN d.auxiliares a WHERE (d.presidente.id = :profesorId OR a.id = :profesorId) AND (:anio IS NULL OR YEAR(d.diaExamen) = :anio)")
    long countExamenesByProfesor(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("anio") Integer anio);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionExamen i JOIN i.detalleMesaExamen d LEFT JOIN d.auxiliares a WHERE (d.presidente.id = :profesorId OR a.id = :profesorId) AND i.estado = :estado AND (:anio IS NULL OR YEAR(d.diaExamen) = :anio)")
    long countExamenesByProfesorAndEstado(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("anio") Integer anio, @org.springframework.data.repository.query.Param("estado") com.sysacad.backend.modelo.enums.EstadoExamen estado);

    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionExamen i JOIN i.detalleMesaExamen d " +
            "WHERE (:anio IS NULL OR YEAR(d.diaExamen) = :anio) " +
            "AND (:facultadId IS NULL OR EXISTS (SELECT 1 FROM PlanMateria pm JOIN pm.plan p JOIN p.carrera car JOIN car.facultades f WHERE pm.materia.id = d.materia.id AND f.id = :facultadId)) " +
            "AND (:carreraId IS NULL OR EXISTS (SELECT 1 FROM PlanMateria pm JOIN pm.plan p JOIN p.carrera car WHERE pm.materia.id = d.materia.id AND car.id = :carreraId)) " +
            "AND (:estado IS NULL OR i.estado = :estado)")
    long countExamenesAdmin(@org.springframework.data.repository.query.Param("anio") Integer anio,
                            @org.springframework.data.repository.query.Param("facultadId") UUID facultadId,
                            @org.springframework.data.repository.query.Param("carreraId") UUID carreraId,
                            @org.springframework.data.repository.query.Param("estado") com.sysacad.backend.modelo.enums.EstadoExamen estado);

    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionExamen i JOIN i.detalleMesaExamen d LEFT JOIN d.auxiliares a WHERE (d.presidente.id = :profesorId OR a.id = :profesorId) AND d.materia.id = :materiaId AND (:anio IS NULL OR YEAR(d.diaExamen) = :anio)")
    long countExamenesByProfesorAndMateria(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("materiaId") UUID materiaId, @org.springframework.data.repository.query.Param("anio") Integer anio);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionExamen i JOIN i.detalleMesaExamen d LEFT JOIN d.auxiliares a WHERE (d.presidente.id = :profesorId OR a.id = :profesorId) AND d.materia.id = :materiaId AND i.estado = :estado AND (:anio IS NULL OR YEAR(d.diaExamen) = :anio)")
    long countExamenesByProfesorAndMateriaAndEstado(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("materiaId") UUID materiaId, @org.springframework.data.repository.query.Param("anio") Integer anio, @org.springframework.data.repository.query.Param("estado") com.sysacad.backend.modelo.enums.EstadoExamen estado);
}
