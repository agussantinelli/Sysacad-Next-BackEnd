package com.sysacad.backend.config.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DbSeeder {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            UTNSeeder utnSeeder,
            UsuarioSeeder usuarioSeeder,
            MatriculacionSeeder matriculacionSeeder,
            ComisionSeeder comisionSeeder,
            InscripcionSeeder inscripcionSeeder
    ) {
        return args -> {
            // Carga Estructural (Materias, Planes, Carreras)
            utnSeeder.seed();

            // Cargar Usuarios
            usuarioSeeder.seed();

            // Matriculaciones
            matriculacionSeeder.seed();

            // Comisiones y Asignaciones
            comisionSeeder.seed();

            // Inscripciones y Examenes
            inscripcionSeeder.seed();

            System.out.println(">> DbSeeder: Proceso de seeding completo.");
        };
    }
}