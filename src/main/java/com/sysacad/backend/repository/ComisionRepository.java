package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Comision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComisionRepository extends JpaRepository<Comision, UUID> {

    List<Comision> findByAnio(Integer anio);

    List<Comision> findByTurno(String turno);

    List<Comision> findByMateriasId(UUID materiaId);

    List<Comision> findByNombreAndAnio(String nombre, Integer anio);

    List<Comision> findByMateriasIdAndProfesoresId(UUID materiaId, UUID profesorId);

    List<Comision> findByProfesoresId(UUID profesorId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c.anio FROM Comision c JOIN c.profesores p WHERE p.id = :profesorId ORDER BY c.anio DESC")
    List<Integer> findDistinctAniosByProfesorId(@org.springframework.data.repository.query.Param("profesorId") UUID profesorId);
    
    List<Comision> findByTurnoAndAnio(String turno, Integer anio);

    List<Comision> findByTurnoIgnoreCaseAndAnio(String turno, Integer anio);

    boolean existsByNombre(String nombre);
}