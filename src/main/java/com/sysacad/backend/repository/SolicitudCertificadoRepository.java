package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.SolicitudCertificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SolicitudCertificadoRepository extends JpaRepository<SolicitudCertificado, UUID> {
}
