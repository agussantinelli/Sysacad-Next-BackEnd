package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlanDeEstudioRepository extends JpaRepository<PlanDeEstudio, PlanDeEstudio.PlanId> {

    List<PlanDeEstudio> findByIdIdCarreraAndEsVigenteTrue(String idCarrera);

}