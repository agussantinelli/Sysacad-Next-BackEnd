CREATE DATABASE IF NOT EXISTS gestion_academica
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE gestion_academica;

CREATE TABLE carreras (
                          id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          CONSTRAINT uq_carrera_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE materias (
                          id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          descripcion TEXT,
                          tipo_materia VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE usuarios (
                          id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                          legajo VARCHAR(20) NOT NULL,
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          mail VARCHAR(150) NOT NULL,
                          fecha_ingreso DATE NOT NULL DEFAULT (CURRENT_DATE),
                          rol ENUM('admin', 'estudiante') NOT NULL,
                          CONSTRAINT uq_usuario_legajo UNIQUE (legajo),
                          CONSTRAINT uq_usuario_mail UNIQUE (mail)
) ENGINE=InnoDB;

CREATE TABLE profesores (
                            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL,
                            apellido VARCHAR(100) NOT NULL,
                            mail VARCHAR(150) NOT NULL,
                            fecha_nacimiento DATE NOT NULL,
                            fecha_ingreso DATE NOT NULL,
                            CONSTRAINT uq_profesor_mail UNIQUE (mail)
) ENGINE=InnoDB;

CREATE TABLE planes_estudio (
                                id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                id_carrera BIGINT UNSIGNED NOT NULL,
                                nombre VARCHAR(100) NOT NULL,
                                fecha_inicio DATE NOT NULL,
                                fecha_fin DATE,
                                es_vigente BOOLEAN NOT NULL DEFAULT TRUE,
                                CONSTRAINT fk_plan_carrera FOREIGN KEY (id_carrera) REFERENCES carreras(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE plan_materias (
                               id_plan BIGINT UNSIGNED NOT NULL,
                               id_materia BIGINT UNSIGNED NOT NULL,
                               anio_cursada TINYINT UNSIGNED NOT NULL,
                               PRIMARY KEY (id_plan, id_materia),
                               CONSTRAINT fk_pm_plan FOREIGN KEY (id_plan) REFERENCES planes_estudio(id),
                               CONSTRAINT fk_pm_materia FOREIGN KEY (id_materia) REFERENCES materias(id)
) ENGINE=InnoDB;

CREATE TABLE correlativas (
                              id_materia BIGINT UNSIGNED NOT NULL,
                              id_correlativa BIGINT UNSIGNED NOT NULL,
                              PRIMARY KEY (id_materia, id_correlativa),
                              CONSTRAINT fk_corr_materia FOREIGN KEY (id_materia) REFERENCES materias(id),
                              CONSTRAINT fk_corr_requerida FOREIGN KEY (id_correlativa) REFERENCES materias(id)
) ENGINE=InnoDB;

CREATE TABLE comisiones (
                            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                            id_materia BIGINT UNSIGNED NOT NULL,
                            nombre VARCHAR(50) NOT NULL,
                            turno VARCHAR(20) NOT NULL,
                            anio YEAR NOT NULL,
                            CONSTRAINT fk_comision_materia FOREIGN KEY (id_materia) REFERENCES materias(id)
) ENGINE=InnoDB;

CREATE TABLE inscripciones_carrera (
                                       id_usuario BIGINT UNSIGNED NOT NULL,
                                       id_carrera BIGINT UNSIGNED NOT NULL,
                                       fecha_inscripcion DATE NOT NULL DEFAULT (CURRENT_DATE),
                                       estado VARCHAR(20) NOT NULL DEFAULT 'activo',
                                       PRIMARY KEY (id_usuario, id_carrera),
                                       CONSTRAINT fk_ic_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
                                       CONSTRAINT fk_ic_carrera FOREIGN KEY (id_carrera) REFERENCES carreras(id)
) ENGINE=InnoDB;

CREATE TABLE asignaciones_profesores (
                                         id_profesor BIGINT UNSIGNED NOT NULL,
                                         id_materia BIGINT UNSIGNED NOT NULL,
                                         rol ENUM('profesor', 'jefe_catedra') NOT NULL,
                                         PRIMARY KEY (id_profesor, id_materia),
                                         CONSTRAINT fk_ap_profesor FOREIGN KEY (id_profesor) REFERENCES profesores(id),
                                         CONSTRAINT fk_ap_materia FOREIGN KEY (id_materia) REFERENCES materias(id)
) ENGINE=InnoDB;

CREATE TABLE profesores_comisiones (
                                       id_comision BIGINT UNSIGNED NOT NULL,
                                       id_profesor BIGINT UNSIGNED NOT NULL,
                                       PRIMARY KEY (id_comision, id_profesor),
                                       CONSTRAINT fk_pc_comision FOREIGN KEY (id_comision) REFERENCES comisiones(id),
                                       CONSTRAINT fk_pc_profesor FOREIGN KEY (id_profesor) REFERENCES profesores(id)
) ENGINE=InnoDB;

CREATE TABLE inscripciones_cursada (
                                       id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                       id_usuario BIGINT UNSIGNED NOT NULL,
                                       id_comision BIGINT UNSIGNED NOT NULL,
                                       fecha_inscripcion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       condicion VARCHAR(20) NOT NULL DEFAULT 'regular',
                                       nota_final DECIMAL(4, 2) NULL,
                                       CONSTRAINT uq_cursada_usuario_comision UNIQUE (id_usuario, id_comision),
                                       CONSTRAINT fk_cursada_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
                                       CONSTRAINT fk_cursada_comision FOREIGN KEY (id_comision) REFERENCES comisiones(id)
) ENGINE=InnoDB;

CREATE TABLE calificaciones (
                                id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                id_inscripcion_cursada BIGINT UNSIGNED NOT NULL,
                                concepto VARCHAR(100) NOT NULL,
                                nota DECIMAL(4, 2) NOT NULL,
                                CONSTRAINT fk_calif_inscripcion FOREIGN KEY (id_inscripcion_cursada) REFERENCES inscripciones_cursada(id) ON DELETE CASCADE
) ENGINE=InnoDB;