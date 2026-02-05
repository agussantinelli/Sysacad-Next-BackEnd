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

    boolean existsByUsuarioIdAndMateriaIdAndEstado(UUID idUsuario, UUID idMateria, com.sysacad.backend.modelo.enums.EstadoCursada estado);

    long countByComisionIdAndMateriaIdAndEstado(UUID idComision, UUID idMateria, com.sysacad.backend.modelo.enums.EstadoCursada estado);

    List<InscripcionCursado> findByComisionIdAndMateriaIdAndEstado(UUID idComision, UUID idMateria, com.sysacad.backend.modelo.enums.EstadoCursada estado);
    
    List<InscripcionCursado> findByComisionIdAndMateriaId(UUID idComision, UUID idMateria);

    @org.springframework.data.jpa.repository.Query("SELECT i FROM InscripcionCursado i WHERE i.comision.id = :idComision AND i.materia.id = :idMateria AND (i.estado = com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO OR ((i.estado = com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR OR i.estado = com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO) AND (i.fechaRegularidad >= :fechaLimite OR i.fechaPromocion >= :fechaLimite)))")
    List<InscripcionCursado> findInscriptosActivosOSemiesActivos(@org.springframework.data.repository.query.Param("idComision") UUID idComision, @org.springframework.data.repository.query.Param("idMateria") UUID idMateria, @org.springframework.data.repository.query.Param("fechaLimite") java.time.LocalDate fechaLimite);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionCursado i WHERE i.comision.id = :idComision AND i.materia.id = :idMateria AND (i.estado = com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO OR ((i.estado = com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR OR i.estado = com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO) AND (i.fechaRegularidad >= :fechaLimite OR i.fechaPromocion >= :fechaLimite)))")
    long countInscriptosActivosOSemiesActivos(@org.springframework.data.repository.query.Param("idComision") UUID idComision, @org.springframework.data.repository.query.Param("idMateria") UUID idMateria, @org.springframework.data.repository.query.Param("fechaLimite") java.time.LocalDate fechaLimite);

    // Estadísticas
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionCursado i JOIN i.comision c JOIN c.profesores p WHERE p.id = :profesorId AND (:anio IS NULL OR c.anio = :anio)")
    long countAlumnosByProfesor(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("anio") Integer anio);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionCursado i JOIN i.comision c JOIN c.profesores p WHERE p.id = :profesorId AND i.estado = :estado AND (:anio IS NULL OR c.anio = :anio)")
    long countAlumnosByProfesorAndEstado(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("anio") Integer anio, @org.springframework.data.repository.query.Param("estado") com.sysacad.backend.modelo.enums.EstadoCursada estado);

    @org.springframework.data.jpa.repository.Query("SELECT AVG(i.notaFinal) FROM InscripcionCursado i JOIN i.comision c JOIN c.profesores p WHERE p.id = :profesorId AND i.notaFinal IS NOT NULL AND (:anio IS NULL OR c.anio = :anio)")
    Double calculateAverageGradeByProfesor(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("anio") Integer anio);

    // Estadísticas Admin
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionCursado i JOIN i.comision c " +
            "WHERE (:anio IS NULL OR c.anio = :anio) " +
            "AND (:facultadId IS NULL OR EXISTS (SELECT 1 FROM PlanMateria pm JOIN pm.plan p JOIN p.carrera car JOIN car.facultades f WHERE pm.materia.id = i.materia.id AND f.id = :facultadId)) " +
            "AND (:carreraId IS NULL OR EXISTS (SELECT 1 FROM PlanMateria pm JOIN pm.plan p JOIN p.carrera car WHERE pm.materia.id = i.materia.id AND car.id = :carreraId)) " +
            "AND (:estado IS NULL OR i.estado = :estado)")
    long countAlumnosAdmin(@org.springframework.data.repository.query.Param("anio") Integer anio,
                           @org.springframework.data.repository.query.Param("facultadId") UUID facultadId,
                           @org.springframework.data.repository.query.Param("carreraId") UUID carreraId,
                           @org.springframework.data.repository.query.Param("estado") com.sysacad.backend.modelo.enums.EstadoCursada estado);

    @org.springframework.data.jpa.repository.Query("SELECT AVG(i.notaFinal) FROM InscripcionCursado i JOIN i.comision c " +
            "WHERE i.notaFinal IS NOT NULL " +
            "AND (:anio IS NULL OR c.anio = :anio) " +
            "AND (:facultadId IS NULL OR EXISTS (SELECT 1 FROM PlanMateria pm JOIN pm.plan p JOIN p.carrera car JOIN car.facultades f WHERE pm.materia.id = i.materia.id AND f.id = :facultadId)) " +
            "AND (:carreraId IS NULL OR EXISTS (SELECT 1 FROM PlanMateria pm JOIN pm.plan p JOIN p.carrera car WHERE pm.materia.id = i.materia.id AND car.id = :carreraId))")
    Double calculateAverageGradeAdmin(@org.springframework.data.repository.query.Param("anio") Integer anio,
                                      @org.springframework.data.repository.query.Param("facultadId") UUID facultadId,
                                      @org.springframework.data.repository.query.Param("carreraId") UUID carreraId);

    // Estadísticas por Materia
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionCursado i JOIN i.comision c JOIN c.profesores p WHERE p.id = :profesorId AND i.materia.id = :materiaId AND (:anio IS NULL OR c.anio = :anio)")
    long countAlumnosByProfesorAndMateria(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("materiaId") UUID materiaId, @org.springframework.data.repository.query.Param("anio") Integer anio);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM InscripcionCursado i JOIN i.comision c JOIN c.profesores p WHERE p.id = :profesorId AND i.materia.id = :materiaId AND i.estado = :estado AND (:anio IS NULL OR c.anio = :anio)")
    long countAlumnosByProfesorAndMateriaAndEstado(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("materiaId") UUID materiaId, @org.springframework.data.repository.query.Param("anio") Integer anio, @org.springframework.data.repository.query.Param("estado") com.sysacad.backend.modelo.enums.EstadoCursada estado);

    @org.springframework.data.jpa.repository.Query("SELECT AVG(i.notaFinal) FROM InscripcionCursado i JOIN i.comision c JOIN c.profesores p WHERE p.id = :profesorId AND i.materia.id = :materiaId AND i.notaFinal IS NOT NULL AND (:anio IS NULL OR c.anio = :anio)")
    Double calculateAverageGradeByProfesorAndMateria(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId, @org.springframework.data.repository.query.Param("materiaId") UUID materiaId, @org.springframework.data.repository.query.Param("anio") Integer anio);
}
