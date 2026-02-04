package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.InstanciaEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstanciaEvaluacionRepository extends JpaRepository<InstanciaEvaluacion, UUID> {
    Optional<InstanciaEvaluacion> findByNombre(String nombre);
}
