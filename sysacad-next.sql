DROP DATABASE IF EXISTS sysacad_db;
CREATE DATABASE sysacad_db WITH ENCODING 'UTF8';

CREATE TABLE facultades_regionales (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ciudad VARCHAR(100) NOT NULL,
    provincia VARCHAR(100) NOT NULL
);

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    legajo VARCHAR(20) NOT NULL,
    tipo_documento VARCHAR(20) NOT NULL DEFAULT 'DNI' CHECK (tipo_documento IN ('DNI', 'PASAPORTE', 'LC', 'LE', 'CI')),
    dni VARCHAR(15) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    mail VARCHAR(150) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    genero VARCHAR(1) NOT NULL CHECK (genero IN ('M', 'F')),
    telefono VARCHAR(50),
    direccion VARCHAR(255),
    ciudad VARCHAR(100),
    foto_perfil VARCHAR(255),
    fecha_ingreso DATE NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('admin', 'estudiante')),
    estado VARCHAR(20) NOT NULL,

    CONSTRAINT uq_usuario_legajo UNIQUE (legajo),
    CONSTRAINT uq_usuario_mail UNIQUE (mail),
    CONSTRAINT uq_usuario_dni UNIQUE (tipo_documento, dni)
);

CREATE TABLE profesores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tipo_documento VARCHAR(20) NOT NULL DEFAULT 'DNI' CHECK (tipo_documento IN ('DNI', 'PASAPORTE', 'LC', 'LE', 'CI')),
    dni VARCHAR(15) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    mail VARCHAR(150) NOT NULL,
    titulo_academico VARCHAR(100) NULL,
    fecha_nacimiento DATE NOT NULL,
    genero VARCHAR(1) NOT NULL CHECK (genero IN ('M', 'F')),
    fecha_ingreso DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,

    CONSTRAINT uq_profesor_mail UNIQUE (mail),
    CONSTRAINT uq_profesor_dni UNIQUE (tipo_documento, dni)
);

CREATE TABLE materias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_materia VARCHAR(20) NOT NULL CHECK (tipo_materia IN ('basica', 'especifica', 'compartida')),
    duracion VARCHAR(20) NOT NULL CHECK (duracion IN ('anual', 'cuatrimestral')),
    horas_cursado SMALLINT NOT NULL,
    rendir_libre BOOLEAN NOT NULL DEFAULT FALSE,
    optativa BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE salones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_facultad UUID NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    piso VARCHAR(10) NOT NULL,
    CONSTRAINT fk_salon_facultad FOREIGN KEY (id_facultad) REFERENCES facultades_regionales(id)
);

CREATE TABLE sanciones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario UUID NOT NULL,
    motivo TEXT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,
    CONSTRAINT fk_sancion_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE avisos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_emision TIMESTAMP NOT NULL,
    estado VARCHAR(20) NOT NULL
);

CREATE TABLE carreras (
    id_facultad UUID NOT NULL,
    id_carrera VARCHAR(20) NOT NULL, 
    nombre VARCHAR(100) NOT NULL,

    PRIMARY KEY (id_facultad, id_carrera),
    CONSTRAINT fk_carrera_facultad FOREIGN KEY (id_facultad) REFERENCES facultades_regionales(id)
);

CREATE TABLE planes_de_estudios (
    id_facultad UUID NOT NULL,
    id_carrera VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL, 
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    es_vigente BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id_facultad, id_carrera, nombre),

    CONSTRAINT fk_plan_carrera FOREIGN KEY (id_facultad, id_carrera)
        REFERENCES carreras(id_facultad, id_carrera) ON DELETE CASCADE
);

CREATE TABLE plan_materias (
    id_facultad UUID NOT NULL,
    id_carrera VARCHAR(20) NOT NULL,
    nombre_plan VARCHAR(100) NOT NULL,
    id_materia UUID NOT NULL,
    codigo_materia VARCHAR(20) NOT NULL,
    nivel SMALLINT NOT NULL,

    PRIMARY KEY (id_facultad, id_carrera, nombre_plan, id_materia),

    CONSTRAINT fk_pm_plan FOREIGN KEY (id_facultad, id_carrera, nombre_plan)
        REFERENCES planes_de_estudios(id_facultad, id_carrera, nombre),
    CONSTRAINT fk_pm_materia FOREIGN KEY (id_materia) REFERENCES materias(id)
);

CREATE TABLE correlativas (
    id_materia UUID NOT NULL,
    id_correlativa UUID NOT NULL,
    PRIMARY KEY (id_materia, id_correlativa),
    CONSTRAINT fk_corr_materia FOREIGN KEY (id_materia) REFERENCES materias(id),
    CONSTRAINT fk_corr_requerida FOREIGN KEY (id_correlativa) REFERENCES materias(id)
);

CREATE TABLE comisiones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_salon UUID NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    turno VARCHAR(20) NOT NULL,
    anio INTEGER NOT NULL,

    CONSTRAINT fk_comision_salon FOREIGN KEY (id_salon) REFERENCES salones(id)
);

CREATE TABLE materias_comisiones (
    id_materia UUID NOT NULL,
    id_comision UUID NOT NULL,

    PRIMARY KEY (id_materia, id_comision),

    CONSTRAINT fk_mc_materia FOREIGN KEY (id_materia) REFERENCES materias(id),
    CONSTRAINT fk_mc_comision FOREIGN KEY (id_comision) REFERENCES comisiones(id)
);

CREATE TABLE profesores_comisiones (
    id_profesor UUID NOT NULL,
    id_comision UUID NOT NULL,
    PRIMARY KEY (id_profesor, id_comision),
    CONSTRAINT fk_pc_prof FOREIGN KEY (id_profesor) REFERENCES profesores(id),
    CONSTRAINT fk_pc_com FOREIGN KEY (id_comision) REFERENCES comisiones(id)
);

CREATE TABLE asignaciones_materia (
    id_profesor UUID NOT NULL,
    id_materia UUID NOT NULL,
    rol VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_profesor, id_materia),
    CONSTRAINT fk_am_prof FOREIGN KEY (id_profesor) REFERENCES profesores(id),
    CONSTRAINT fk_am_mat FOREIGN KEY (id_materia) REFERENCES materias(id)
);

CREATE TABLE estudios_usuario (
    id_usuario UUID NOT NULL,
    id_facultad UUID NOT NULL,
    id_carrera VARCHAR(20) NOT NULL,
    nombre_plan VARCHAR(100) NOT NULL,

    fecha_inscripcion DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,

    PRIMARY KEY (id_usuario, id_facultad, id_carrera, nombre_plan),

    CONSTRAINT fk_estudia_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    CONSTRAINT fk_estudia_plan FOREIGN KEY (id_facultad, id_carrera, nombre_plan)
        REFERENCES planes_de_estudios(id_facultad, id_carrera, nombre)
);

CREATE TABLE inscripciones (
    id_usuario UUID NOT NULL,
    id_comision UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('cursado', 'examen')),
    veces_tipo INTEGER NOT NULL DEFAULT 1,

    fecha_inscripcion TIMESTAMP NOT NULL,
    condicion VARCHAR(50) NOT NULL,
    descripcion_condicion TEXT,
    fecha_promocion DATE,
    nota_final DECIMAL(4, 2),
    tomo VARCHAR(20),
    folio VARCHAR(20),

    PRIMARY KEY (id_usuario, id_comision, tipo, veces_tipo),

    CONSTRAINT fk_inscrip_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    CONSTRAINT fk_inscrip_comision FOREIGN KEY (id_comision) REFERENCES comisiones(id)
);

CREATE TABLE calificaciones (
    id_usuario UUID NOT NULL,
    id_comision UUID NOT NULL,
    tipo_inscripcion VARCHAR(20) NOT NULL CHECK (tipo_inscripcion IN ('cursado', 'examen')),
    veces_tipo_inscripcion INTEGER NOT NULL,

    concepto VARCHAR(100) NOT NULL,
    nota DECIMAL(4, 2) NOT NULL,

    PRIMARY KEY (id_usuario, id_comision, tipo_inscripcion, veces_tipo_inscripcion, concepto),

    CONSTRAINT fk_calif_inscripcion
        FOREIGN KEY (id_usuario, id_comision, tipo_inscripcion, veces_tipo_inscripcion)
            REFERENCES inscripciones (id_usuario, id_comision, tipo, veces_tipo)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);
