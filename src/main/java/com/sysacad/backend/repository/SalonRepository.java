package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalonRepository extends JpaRepository<Salon, UUID> {
    // Buscar salones de una facultad específica
    List<Salon> findByFacultadId(UUID idFacultad);
}