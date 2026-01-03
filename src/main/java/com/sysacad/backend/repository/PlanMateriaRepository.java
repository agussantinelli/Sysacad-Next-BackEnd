package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.PlanMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanMateriaRepository extends JpaRepository<PlanMateria, PlanMateria.PlanMateriaId> {

    List<PlanMateria> findByIdNombrePlan(String nombrePlan);

    List<PlanMateria> findByNivel(Short nivel);
}