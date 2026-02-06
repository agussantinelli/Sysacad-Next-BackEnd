package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.PlanMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanMateriaRepository extends JpaRepository<PlanMateria, PlanMateria.PlanMateriaId> {

    List<PlanMateria> findByIdNroPlan(Integer nroPlan);

    List<PlanMateria> findByNivel(Short nivel);

    List<PlanMateria> findByIdIdMateria(UUID idMateria);

    List<PlanMateria> findByIdIdCarreraAndIdNroPlan(UUID idCarrera, Integer nroPlan);
    
    List<PlanMateria> findByIdIdCarreraAndNivel(UUID idCarrera, Short nivel);
}