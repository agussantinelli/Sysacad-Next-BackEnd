package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Calificacion;
import com.sysacad.backend.modelo.Calificacion.CalificacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, CalificacionId> {

    List<Calificacion> findByIdIdUsuario(UUID idUsuario);

    List<Calificacion> findByIdIdComision(UUID idComision);
}