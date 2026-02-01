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
            // Verificar si ya hay matriculaciones cargadas
            if (matriculacionRepository.count() > 0) {
                System.out.println(">> MatriculacionSeeder: OMITIDO - Las matriculaciones ya están cargadas.");
                return;
            }

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
                matricularIfNotExists("55555", frro, carreraISI.getId(), 2023); // Agustin
                matricularIfNotExists("58888", frro, carreraISI.getId(), 2023); // Sofia
                matricularIfNotExists("60002", frro, carreraISI.getId(), 2023); // Carlos
                matricularIfNotExists("56666", frro, carreraISI.getId(), 2023); // Maria
                matricularIfNotExists("60003", frro, carreraISI.getId(), 2023); // Martin
                matricularIfNotExists("60004", frro, carreraISI.getId(), 2023); // Flavia
                matricularIfNotExists("60010", frro, carreraISI.getId(), 2023); // Pedro
                matricularIfNotExists("60011", frro, carreraISI.getId(), 2023); // Lionel
                matricularIfNotExists("60012", frro, carreraISI.getId(), 2023); // Alex
                matricularIfNotExists("60013", frro, carreraISI.getId(), 2023); // Diego
                matricularIfNotExists("60014", frro, carreraISI.getId(), 2023); // Enzo
            }

            if (carreraCivil != null) {
                matricularIfNotExists("57777", frro, carreraCivil.getId(), 2023); // Juan (Civil)
            }

            if (carreraElectrica != null) {
                matricularIfNotExists("59999", frro, carreraElectrica.getId(), 2023); // Miguel (Electrica)
            }

            System.out.println(">> Alumnos matriculados exitosamente.");
        // } (Block removed to allow individual checks)
    }

    private void matricularIfNotExists(String legajo, FacultadRegional facu, java.util.UUID idCarrera, Integer nroPlan) {
        Usuario alumno = usuarioRepository.findByLegajo(legajo).orElse(null);
        if (alumno == null)
            return;
        
        Matriculacion.MatriculacionId id = new Matriculacion.MatriculacionId(alumno.getId(), facu.getId(),
                idCarrera, nroPlan);

        if (matriculacionRepository.existsById(id)) {
            return;
        }

        Matriculacion eu = new Matriculacion();
        eu.setId(id);
        eu.setFechaInscripcion(LocalDate.now());
        eu.setEstado("ACTIVO");
        matriculacionRepository.save(eu);
    }
}
