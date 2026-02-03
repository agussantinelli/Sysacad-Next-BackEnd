DROP DATABASE IF EXISTS sysacad_db;
CREATE DATABASE sysacad_db WITH ENCODING 'UTF8';

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

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
    password VARCHAR(255) NOT NULL,
    password_change_required BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_nacimiento DATE NOT NULL,
    genero VARCHAR(1) NOT NULL CHECK (genero IN ('M', 'F')),
    telefono VARCHAR(50),
    direccion VARCHAR(255),
    ciudad VARCHAR(100),
    foto_perfil VARCHAR(255),
    fecha_ingreso DATE NOT NULL,
    titulo_academico VARCHAR(100), 
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'ESTUDIANTE', 'PROFESOR')),
    estado VARCHAR(20) NOT NULL,

    CONSTRAINT uq_usuario_legajo UNIQUE (legajo),
    CONSTRAINT uq_usuario_mail UNIQUE (mail),
    CONSTRAINT uq_usuario_dni UNIQUE (tipo_documento, dni)
);

CREATE TABLE materias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_materia VARCHAR(20) NOT NULL CHECK (tipo_materia IN ('BASICA', 'ESPECIFICA', 'COMPARTIDA')),
    duracion VARCHAR(20) NOT NULL CHECK (duracion IN ('ANUAL', 'CUATRIMESTRAL')),
    modalidad VARCHAR(20) NOT NULL DEFAULT 'PRESENCIAL' CHECK (modalidad IN ('PRESENCIAL', 'MIXTO', 'VIRTUAL')),
    cuatrimestre_dictado VARCHAR(20) CHECK (cuatrimestre_dictado IN ('PRIMERO', 'SEGUNDO', 'ANUAL', 'AMBOS')),
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
    id_carrera UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    alias VARCHAR(20) NOT NULL
);

CREATE TABLE facultades_carreras (
    id_facultad UUID NOT NULL,
    id_carrera UUID NOT NULL,
    PRIMARY KEY (id_facultad, id_carrera),
    CONSTRAINT fk_fc_facultad FOREIGN KEY (id_facultad) REFERENCES facultades_regionales(id),
    CONSTRAINT fk_fc_carrera FOREIGN KEY (id_carrera) REFERENCES carreras(id_carrera)
);

CREATE TABLE planes_de_estudios (
    id_carrera UUID NOT NULL,
    nro_plan INTEGER NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    es_vigente BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id_carrera, nro_plan),
    CONSTRAINT fk_plan_carrera FOREIGN KEY (id_carrera) REFERENCES carreras(id_carrera)
);

CREATE TABLE plan_materias (
    id_carrera UUID NOT NULL,
    nro_plan INTEGER NOT NULL,
    id_materia UUID NOT NULL,
    codigo_materia VARCHAR(20) NOT NULL,
    nivel SMALLINT NOT NULL,
    PRIMARY KEY (id_carrera, nro_plan, id_materia),
    CONSTRAINT fk_pm_plan FOREIGN KEY (id_carrera, nro_plan) REFERENCES planes_de_estudios(id_carrera, nro_plan),
    CONSTRAINT fk_pm_materia FOREIGN KEY (id_materia) REFERENCES materias(id)
);

CREATE TABLE correlatividades (
    id_correlatividad UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_materia UUID NOT NULL,
    id_correlativa UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    plan_id_carrera UUID,
    plan_nro_plan INTEGER,
    CONSTRAINT fk_corr_materia FOREIGN KEY (id_materia) REFERENCES materias(id),
    CONSTRAINT fk_corr_requerida FOREIGN KEY (id_correlativa) REFERENCES materias(id),
    CONSTRAINT fk_corr_plan FOREIGN KEY (plan_id_carrera, plan_nro_plan) REFERENCES planes_de_estudios(id_carrera, nro_plan)
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
    id_usuario UUID NOT NULL,
    id_comision UUID NOT NULL,
    PRIMARY KEY (id_usuario, id_comision),
    CONSTRAINT fk_pc_prof FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    CONSTRAINT fk_pc_com FOREIGN KEY (id_comision) REFERENCES comisiones(id)
);

CREATE TABLE asignaciones_materia (
    id_usuario UUID NOT NULL,
    id_materia UUID NOT NULL,
    cargo VARCHAR(20) NOT NULL CHECK (cargo IN ('JEFE_CATEDRA', 'DOCENTE')),
    PRIMARY KEY (id_usuario, id_materia),
    CONSTRAINT fk_am_prof FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    CONSTRAINT fk_am_mat FOREIGN KEY (id_materia) REFERENCES materias(id)
);

-- 8. Vida Académica del Alumno
CREATE TABLE matriculaciones (
    id_usuario UUID NOT NULL,
    id_facultad UUID NOT NULL,
    id_carrera UUID NOT NULL,
    nro_plan INTEGER NOT NULL,
    fecha_inscripcion DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_usuario, id_facultad, id_carrera, nro_plan),
    CONSTRAINT fk_matr_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    CONSTRAINT fk_matr_facultad FOREIGN KEY (id_facultad) REFERENCES facultades_regionales(id),
    CONSTRAINT fk_matr_plan FOREIGN KEY (id_carrera, nro_plan) REFERENCES planes_de_estudios(id_carrera, nro_plan)
);


CREATE TABLE horarios_cursado (
    id_comision UUID NOT NULL,
    id_materia UUID NOT NULL,
    dia VARCHAR(20) NOT NULL CHECK (dia IN ('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO')),
    hora_desde TIME NOT NULL,
    hora_hasta TIME NOT NULL,
    PRIMARY KEY (id_comision, id_materia, dia, hora_desde),
    CONSTRAINT fk_hc_comision FOREIGN KEY (id_comision) REFERENCES comisiones(id),
    CONSTRAINT fk_hc_materia FOREIGN KEY (id_materia) REFERENCES materias(id),
    CONSTRAINT chk_horario_valido CHECK (hora_hasta > hora_desde)
);

CREATE TABLE mesas_examen (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL
);

CREATE TABLE detalle_mesa_examen (
    id_mesa_examen UUID NOT NULL,
    nro_detalle INTEGER NOT NULL,
    id_materia UUID NOT NULL,
    dia_examen DATE NOT NULL,
    hora_examen TIME NOT NULL,
    id_presidente UUID NOT NULL,
    PRIMARY KEY (id_mesa_examen, nro_detalle),
    CONSTRAINT fk_dme_mesa FOREIGN KEY (id_mesa_examen) REFERENCES mesas_examen(id),
    CONSTRAINT fk_dme_materia FOREIGN KEY (id_materia) REFERENCES materias(id),
    CONSTRAINT fk_dme_presidente FOREIGN KEY (id_presidente) REFERENCES usuarios(id)
);

CREATE TABLE detalle_mesa_examen_auxiliares (
    id_mesa_examen UUID NOT NULL,
    nro_detalle INTEGER NOT NULL,
    id_usuario UUID NOT NULL,
    PRIMARY KEY (id_mesa_examen, nro_detalle, id_usuario),
    CONSTRAINT fk_dmea_detalle FOREIGN KEY (id_mesa_examen, nro_detalle) REFERENCES detalle_mesa_examen(id_mesa_examen, nro_detalle),
    CONSTRAINT fk_dmea_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE inscripciones_examen (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario UUID NOT NULL,
    id_mesa_examen UUID,
    nro_detalle INTEGER,
    id_materia UUID,
    fecha_inscripcion TIMESTAMP NOT NULL,
    estado VARCHAR(50) NOT NULL,
    nota DECIMAL(4, 2),
    tomo VARCHAR(20),
    folio VARCHAR(20),
    CONSTRAINT fk_ie_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    CONSTRAINT fk_ie_detalle FOREIGN KEY (id_mesa_examen, nro_detalle) REFERENCES detalle_mesa_examen(id_mesa_examen, nro_detalle),
    CONSTRAINT fk_ie_materia FOREIGN KEY (id_materia) REFERENCES materias(id)
);

CREATE TABLE equivalencias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_carrera_origen UUID NOT NULL,
    nro_plan_origen INTEGER NOT NULL,
    id_materia_origen UUID NOT NULL,
    id_carrera_destino UUID NOT NULL,
    nro_plan_destino INTEGER NOT NULL,
    id_materia_destino UUID NOT NULL,
    fecha_creacion DATE,
    motivo VARCHAR(255)
);

CREATE TABLE inscripciones_cursado (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario UUID NOT NULL,
    id_materia UUID NOT NULL,
    id_comision UUID NOT NULL,
    fecha_inscripcion TIMESTAMP NOT NULL,
    estado VARCHAR(50) NOT NULL,
    nota_final DECIMAL(4, 2),
    fecha_promocion DATE,
    tomo VARCHAR(20),
    folio VARCHAR(20),
    CONSTRAINT fk_ic_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id),
    CONSTRAINT fk_ic_materia FOREIGN KEY (id_materia) REFERENCES materias(id),
    CONSTRAINT fk_ic_comision FOREIGN KEY (id_comision) REFERENCES comisiones(id)
);

CREATE TABLE calificaciones_cursada (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_inscripcion_cursado UUID NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    nota DECIMAL(4, 2) NOT NULL,
    fecha DATE NOT NULL,
    CONSTRAINT fk_cc_inscripcion FOREIGN KEY (id_inscripcion_cursado) REFERENCES inscripciones_cursado(id) ON DELETE CASCADE
);

CREATE TABLE avisos_personas (
    id_aviso UUID NOT NULL,
    id_persona UUID NOT NULL,
    estado VARCHAR(20) NOT NULL,
    PRIMARY KEY (id_aviso, id_persona),
    CONSTRAINT fk_ap_aviso FOREIGN KEY (id_aviso) REFERENCES avisos(id),
    CONSTRAINT fk_ap_persona FOREIGN KEY (id_persona) REFERENCES usuarios(id)
);

CREATE TABLE grupos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50), -- Opcional: 'CATEDRA', 'ESTUDIO', etc.
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE miembros_grupo (
    id_grupo UUID NOT NULL,
    id_usuario UUID NOT NULL,
    rol_interno VARCHAR(20) NOT NULL DEFAULT 'MIEMBRO', -- 'ADMIN', 'MIEMBRO'
    fecha_union TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_grupo, id_usuario),
    CONSTRAINT fk_mg_grupo FOREIGN KEY (id_grupo) REFERENCES grupos(id),
    CONSTRAINT fk_mg_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE mensajes_grupo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_grupo UUID NOT NULL,
    id_usuario UUID NOT NULL, -- El remitente
    contenido TEXT NOT NULL,
    editado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_msg_grupo FOREIGN KEY (id_grupo) REFERENCES grupos(id),
    CONSTRAINT fk_msg_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

