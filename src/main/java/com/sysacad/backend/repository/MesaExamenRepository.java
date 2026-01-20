package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.MesaExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MesaExamenRepository extends JpaRepository<MesaExamen, UUID> {
}
