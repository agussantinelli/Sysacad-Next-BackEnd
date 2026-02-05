package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.enums.TipoMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, UUID> {

    List<Materia> findByNombreContainingIgnoreCase(String nombre);

    Optional<Materia> findByNombre(String nombre);

    List<Materia> findByTipoMateria(TipoMateria tipoMateria);
    
    @org.springframework.data.jpa.repository.Query("SELECT m FROM PlanMateria pm JOIN pm.materia m JOIN pm.plan p JOIN p.carrera c WHERE c.id = :carreraId AND LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Materia> findByCarreraIdAndNombreExcluding(@org.springframework.data.repository.query.Param("carreraId") UUID carreraId, @org.springframework.data.repository.query.Param("nombre") String nombre);
}