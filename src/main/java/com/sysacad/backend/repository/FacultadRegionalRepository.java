package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.FacultadRegional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FacultadRegionalRepository extends JpaRepository<FacultadRegional, UUID> {
}