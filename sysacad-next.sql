DROP DATABASE IF EXISTS gestion_academica;
CREATE DATABASE gestion_academica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_academica;

CREATE TABLE usuarios (
                          id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, -- Ovalo ID
                          legajo VARCHAR(20) NOT NULL,
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          mail VARCHAR(150) NOT NULL,
                          fecha_ingreso DATE NOT NULL, -- Asumo default current en insert
                          rol ENUM('admin', 'estudiante') NOT NULL, -- Nota: "El rol puede ser..."
                          CONSTRAINT uq_usuario_legajo UNIQUE (legajo),
                          CONSTRAINT uq_usuario_mail UNIQUE (mail)
) ENGINE=InnoDB;

CREATE TABLE carreras (
                          id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, -- Ovalo ID
                          nombre VARCHAR(100) NOT NULL,
                          CONSTRAINT uq_carrera_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE materias (
                          id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, -- Ovalo ID
                          nombre VARCHAR(100) NOT NULL,
                          descripcion TEXT,
                          tipo_materia VARCHAR(50) NOT NULL,
                          rendir_libre BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;

CREATE TABLE salones (
                         id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, -- Ovalo ID
                         nombre VARCHAR(50) NOT NULL,
                         piso VARCHAR(10) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE profesores (
                            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, -- Ovalo ID
                            nombre VARCHAR(100) NOT NULL,
                            apellido VARCHAR(100) NOT NULL,
                            mail VARCHAR(150) NOT NULL,
                            fecha_nacimiento DATE NOT NULL,
                            fecha_ingreso DATE NOT NULL,
                            CONSTRAINT uq_profesor_mail UNIQUE (mail)
) ENGINE=InnoDB;


CREATE TABLE planes_de_estudios (
                                    id_carrera BIGINT UNSIGNED NOT NULL,
                                    nombre VARCHAR(100) NOT NULL, -- Parte de la PK en entidad débil
                                    fecha_inicio DATE NOT NULL,
                                    fecha_fin DATE,
                                    es_vigente BOOLEAN NOT NULL DEFAULT TRUE,
                                    PRIMARY KEY (id_carrera, nombre),
                                    CONSTRAINT fk_plan_carrera FOREIGN KEY (id_carrera) REFERENCES carreras(id)
) ENGINE=InnoDB;

CREATE TABLE comisiones (
                            id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY, -- Necesario para no propagar una PK gigante
                            id_materia BIGINT UNSIGNED NOT NULL,
                            id_salon BIGINT UNSIGNED NOT NULL, -- Relación 1 a N con SALONES
                            nombre VARCHAR(50) NOT NULL,
                            turno VARCHAR(20) NOT NULL,
                            anio YEAR NOT NULL,
                            CONSTRAINT fk_comision_materia FOREIGN KEY (id_materia) REFERENCES materias(id),
                            CONSTRAINT fk_comision_salon FOREIGN KEY (id_salon) REFERENCES salones(id)
) ENGINE=InnoDB;

CREATE TABLE plan_materias (
                               id_carrera BIGINT UNSIGNED NOT NULL,
                               nombre_plan VARCHAR(100) NOT NULL,
                               id_materia BIGINT UNSIGNED NOT NULL,
                               anio TINYINT UNSIGNED NOT NULL, -- Atributo del rombo MP
                               PRIMARY KEY (id_carrera, nombre_plan, id_materia),
                               CONSTRAINT fk_mp_plan FOREIGN KEY (id_carrera, nombre_plan) REFERENCES planes_de_estudios(id_carrera, nombre),
                               CONSTRAINT fk_mp_materia FOREIGN KEY (id_materia) REFERENCES materias(id)
) ENGINE=InnoDB;

CREATE TABLE correlativas (
                              id_materia BIGINT UNSIGNED NOT NULL,
                              id_correlativa BIGINT UNSIGNED NOT NULL,
                              PRIMARY KEY (id_materia, id_correlativa),
                              CONSTRAINT fk_corr_materia FOREIGN KEY (id_materia) REFERENCES materias(id),
                              CONSTRAINT fk_corr_requerida FOREIGN KEY (id_correlativa) REFERENCES materias(id)
) ENGINE=InnoDB;

-- Relación "Estudia" (Usuario - Carrera) con atributos
CREATE TABLE estudios_usuario (
                                  id_usuario BIGINT UNSIGNED NOT NULL,
                                  id_carrera BIGINT UNSIGNED NOT NULL,
                                  fecha_inscripcion DATE NOT NULL,
                                  estado VARCHAR(20) NOT NULL,
                                  PRIMARY KEY (id_usuario, id_carrera),
                                  CONSTRAINT fk_estudia_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
                                  CONSTRAINT fk_estudia_carrera FOREIGN KEY (id_carrera) REFERENCES carreras(id)
) ENGINE=InnoDB;

CREATE TABLE asignaciones_materia (
                                      id_profesor BIGINT UNSIGNED NOT NULL,
                                      id_materia BIGINT UNSIGNED NOT NULL,
                                      rol VARCHAR(50) NOT NULL,
                                      PRIMARY KEY (id_profesor, id_materia),
                                      CONSTRAINT fk_asig_profesor FOREIGN KEY (id_profesor) REFERENCES profesores(id),
                                      CONSTRAINT fk_asig_materia FOREIGN KEY (id_materia) REFERENCES materias(id)
) ENGINE=InnoDB;

CREATE TABLE profesores_comisiones (
                                       id_profesor BIGINT UNSIGNED NOT NULL,
                                       id_comision BIGINT UNSIGNED NOT NULL,
                                       PRIMARY KEY (id_profesor, id_comision),
                                       CONSTRAINT fk_dan_profesor FOREIGN KEY (id_profesor) REFERENCES profesores(id),
                                       CONSTRAINT fk_dan_comision FOREIGN KEY (id_comision) REFERENCES comisiones(id)
) ENGINE=InnoDB;



CREATE TABLE inscripciones (
                               id_usuario BIGINT UNSIGNED NOT NULL,
                               id_comision BIGINT UNSIGNED NOT NULL,
                               tipo ENUM('cursado', 'examen') NOT NULL, -- Parte de la PK para diferenciar inscripciones
                               veces_tipo INT UNSIGNED DEFAULT 1,
                               fecha_inscripcion DATETIME NOT NULL,
                               condicion VARCHAR(50) NOT NULL,
                               descripcion_condicion TEXT,
                               fecha_promocion DATE,
                               nota_final DECIMAL(4, 2),
                               tomo VARCHAR(20),
                               folio VARCHAR(20),

                               PRIMARY KEY (id_usuario, id_comision, tipo,veces_tipo),

                               CONSTRAINT fk_inscrip_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
                               CONSTRAINT fk_inscrip_comision FOREIGN KEY (id_comision) REFERENCES comisiones(id)
) ENGINE=InnoDB;

CREATE TABLE calificaciones (
                                id_usuario BIGINT UNSIGNED NOT NULL,
                                id_comision BIGINT UNSIGNED NOT NULL,
                                tipo_inscripcion ENUM('cursado', 'examen') NOT NULL,

                                concepto VARCHAR(100) NOT NULL, -- Ej: "Parcial 1", "TP Final"
                                nota DECIMAL(4, 2) NOT NULL,

                                PRIMARY KEY (id_usuario, id_comision, tipo_inscripcion, concepto),

                                CONSTRAINT fk_calif_inscripcion
                                    FOREIGN KEY (id_usuario, id_comision, tipo_inscripcion)
                                        REFERENCES inscripciones (id_usuario, id_comision, tipo)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE
) ENGINE=InnoDB;