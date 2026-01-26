package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.MensajeGrupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MensajeGrupoRepository extends JpaRepository<MensajeGrupo, UUID> {
    Page<MensajeGrupo> findByGrupoIdOrderByFechaEnvioDesc(UUID idGrupo, Pageable pageable);
}
