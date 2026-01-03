package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, UUID> {

    List<Sancion> findByUsuarioId(UUID idUsuario);

}