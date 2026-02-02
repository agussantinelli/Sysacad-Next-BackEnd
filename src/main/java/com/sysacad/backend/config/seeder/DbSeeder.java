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
            InscripcionSeeder inscripcionSeeder,
            AvisoSeeder avisoSeeder,
            com.sysacad.backend.repository.MateriaRepository materiaRepository
    ) {
        return args -> {
            // Verificar si ya se ejecutó el seeder
            if (materiaRepository.count() > 0) {
                System.out.println(">> DbSeeder: OMITIDO - La base de datos ya contiene datos.");
                System.out.println(">> Para volver a seedear, eliminar todas las tablas primero.");
                return;
            }

            System.out.println(">> DbSeeder: Iniciando proceso de seeding...");
            
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

            // Avisos
            avisoSeeder.seed();

            System.out.println(">> DbSeeder: Proceso de seeding completo.");
        };
    }
}