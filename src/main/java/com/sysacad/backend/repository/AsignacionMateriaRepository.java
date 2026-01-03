package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.AsignacionMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AsignacionMateriaRepository extends JpaRepository<AsignacionMateria, AsignacionMateria.AsignacionMateriaId> {

    List<AsignacionMateria> findByIdIdUsuario(UUID idUsuario);

    List<AsignacionMateria> findByIdIdMateria(UUID idMateria);
}