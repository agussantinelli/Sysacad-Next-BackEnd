package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Aviso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvisoRepository extends JpaRepository<Aviso, UUID> {

    List<Aviso> findByFechaEmisionAfter(LocalDateTime fecha);

}