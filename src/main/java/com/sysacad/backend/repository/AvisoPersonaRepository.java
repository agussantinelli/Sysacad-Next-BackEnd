package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.AvisoPersona;
import com.sysacad.backend.modelo.AvisoPersona.AvisoPersonaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisoPersonaRepository extends JpaRepository<AvisoPersona, AvisoPersonaId> {
}
