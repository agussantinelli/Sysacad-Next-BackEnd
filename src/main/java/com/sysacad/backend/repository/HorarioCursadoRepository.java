package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.HorarioCursado;
import com.sysacad.backend.modelo.HorarioCursado.HorarioCursadoId;
import com.sysacad.backend.modelo.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HorarioCursadoRepository extends JpaRepository<HorarioCursado, HorarioCursadoId> {


    // Obtener todos los horarios de una comisión específica
    List<HorarioCursado> findByIdIdComision(UUID idComision);

    // Obtener todos los horarios de una materia (independiente de la comisión)
    List<HorarioCursado> findByIdIdMateria(UUID idMateria);

    // Obtener horarios por día (útil para reportes de ocupación diaria)
    List<HorarioCursado> findByIdDia(DiaSemana dia);

    // Buscar horario específico de una materia en una comisión
    List<HorarioCursado> findByIdIdComisionAndIdIdMateria(UUID idComision, UUID idMateria);


    @Query("SELECT h FROM HorarioCursado h WHERE h.id.idComision = :idComision " +
            "AND h.id.dia = :dia " +
            "AND (h.id.horaDesde < :horaHasta AND h.horaHasta > :horaDesde)")
    List<HorarioCursado> encontrarSolapamientos(
            @Param("idComision") UUID idComision,
            @Param("dia") DiaSemana dia,
            @Param("horaDesde") LocalTime horaDesde,
            @Param("horaHasta") LocalTime horaHasta
    );

    @Query("SELECT h FROM HorarioCursado h JOIN FETCH h.comision c JOIN FETCH c.profesores p JOIN FETCH h.materia m " +
            "WHERE h.id.dia = :dia " +
            "AND (h.id.horaDesde < :horaHasta AND h.horaHasta > :horaDesde)")
    List<HorarioCursado> encontrarSolapamientosGlobal(
            @Param("dia") DiaSemana dia,
            @Param("horaDesde") LocalTime horaDesde,
            @Param("horaHasta") LocalTime horaHasta
    );
}