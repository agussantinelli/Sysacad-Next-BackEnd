package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.enums.TipoMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, UUID> {

    List<Materia> findByNombreContainingIgnoreCase(String nombre);

    List<Materia> findByTipoMateria(TipoMateria tipoMateria);
}