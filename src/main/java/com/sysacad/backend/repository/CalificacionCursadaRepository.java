package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.CalificacionCursada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CalificacionCursadaRepository extends JpaRepository<CalificacionCursada, UUID> {

    java.util.Optional<CalificacionCursada> findByInscripcionCursadoIdAndInstanciaEvaluacionId(UUID idInscripcion, UUID idInstancia);
}
