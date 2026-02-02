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
}