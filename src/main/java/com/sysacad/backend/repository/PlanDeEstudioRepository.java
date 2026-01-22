package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.PlanDeEstudio.PlanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanDeEstudioRepository extends JpaRepository<PlanDeEstudio, PlanId> {

    // Buscar planes vigentes por carrera
    List<PlanDeEstudio> findByIdIdCarreraAndEsVigenteTrue(java.util.UUID idCarrera);

    // Buscar todos los planes de una carrera específica
    List<PlanDeEstudio> findByIdIdCarrera(java.util.UUID idCarrera);
}