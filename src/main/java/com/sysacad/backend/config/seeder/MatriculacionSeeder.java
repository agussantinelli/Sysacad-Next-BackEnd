package com.sysacad.backend.config.seeder;

import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.modelo.Matriculacion;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import com.sysacad.backend.repository.MatriculacionRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class MatriculacionSeeder {

    private final MatriculacionRepository matriculacionRepository;
    private final CarreraRepository carreraRepository;
    private final FacultadRegionalRepository facultadRepository;
    private final UsuarioRepository usuarioRepository;

    public MatriculacionSeeder(MatriculacionRepository matriculacionRepository, CarreraRepository carreraRepository,
                               FacultadRegionalRepository facultadRepository, UsuarioRepository usuarioRepository) {
        this.matriculacionRepository = matriculacionRepository;
        this.carreraRepository = carreraRepository;
        this.facultadRepository = facultadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void seed() {
        if (matriculacionRepository.count() == 0) {
            System.out.println(">> MatriculacionSeeder: Matriculando alumnos de prueba en carreras...");

            FacultadRegional frro = facultadRepository.findAll().stream()
                    .filter(f -> f.getCiudad().equalsIgnoreCase("Rosario"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Error: Facultad Rosario no encontrada (UTNSeeder falló?)."));

            // Recuperar Carreras por Alias
            List<Carrera> todasCarreras = carreraRepository.findAll();
            Carrera carreraISI = todasCarreras.stream().filter(c -> "ISI".equals(c.getAlias())).findFirst().orElse(null);
            Carrera carreraCivil = todasCarreras.stream().filter(c -> "IC".equals(c.getAlias())).findFirst().orElse(null);
            Carrera carreraElectrica = todasCarreras.stream().filter(c -> "IEE".equals(c.getAlias())).findFirst().orElse(null);

            if (carreraISI != null) {
                // Matriculamos a la mayoría en Sistemas (ISI) - Plan 2023
                matricular("55555", frro, carreraISI.getId(), 2023); // Agustin
                matricular("58888", frro, carreraISI.getId(), 2023); // Sofia
                matricular("60002", frro, carreraISI.getId(), 2023); // Carlos
                matricular("56666", frro, carreraISI.getId(), 2023); // Maria
                matricular("60003", frro, carreraISI.getId(), 2023); // Martin
                matricular("60004", frro, carreraISI.getId(), 2023); // Flavia
                matricular("60010", frro, carreraISI.getId(), 2023); // Pedro
                matricular("60011", frro, carreraISI.getId(), 2023); // Lionel
                matricular("60012", frro, carreraISI.getId(), 2023); // Alex
                matricular("60013", frro, carreraISI.getId(), 2023); // Diego
                matricular("60014", frro, carreraISI.getId(), 2023); // Enzo
            }

            if (carreraCivil != null) {
                matricular("57777", frro, carreraCivil.getId(), 2023); // Juan (Civil)
            }

            if (carreraElectrica != null) {
                matricular("59999", frro, carreraElectrica.getId(), 2023); // Miguel (Electrica)
            }

            System.out.println(">> Alumnos matriculados exitosamente.");
        }
    }

    private void matricular(String legajo, FacultadRegional facu, java.util.UUID idCarrera, Integer nroPlan) {
        Usuario alumno = usuarioRepository.findByLegajo(legajo).orElse(null);
        if (alumno == null)
            return;
        
        Matriculacion eu = new Matriculacion();
        Matriculacion.MatriculacionId id = new Matriculacion.MatriculacionId(alumno.getId(), facu.getId(),
                idCarrera, nroPlan);
        eu.setId(id);
        eu.setFechaInscripcion(LocalDate.now());
        eu.setEstado("ACTIVO");
        matriculacionRepository.save(eu);
    }
}
