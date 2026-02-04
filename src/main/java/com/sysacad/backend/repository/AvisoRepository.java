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
    
    List<Aviso> findAllByOrderByFechaEmisionDesc();

    List<Aviso> findByEstadoOrderByFechaEmisionDesc(com.sysacad.backend.modelo.enums.EstadoAviso estado);

    List<Aviso> findByEstado(com.sysacad.backend.modelo.enums.EstadoAviso estado);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Aviso a WHERE a.estado = com.sysacad.backend.modelo.enums.EstadoAviso.ACTIVO AND NOT EXISTS (SELECT ap FROM AvisoPersona ap WHERE ap.aviso = a AND ap.persona.id = :idUsuario AND ap.estado = com.sysacad.backend.modelo.enums.EstadoAvisoPersona.LEIDO)")
    long countAvisosNoLeidos(@org.springframework.data.repository.query.Param("idUsuario") java.util.UUID idUsuario);
}