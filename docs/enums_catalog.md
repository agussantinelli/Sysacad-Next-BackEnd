# Backend Enum Catalog

This document enumerates all Enum types used in the backend application (`src/main/java/com/sysacad/backend/modelo/enums`) to facilitate integration with the frontend.

## Academic

### `CuatrimestreDictado`
Defines when a subject is taught.
- `PRIMERO`
- `SEGUNDO`
- `ANUAL`
- `AMBOS`

### `DuracionMateria`
Defines the duration of a subject.
- `ANUAL`
- `CUATRIMESTRAL`

### `ModalidadMateria`
Defines the teaching mode.
- `PRESENCIAL`
- `MIXTO`
- `VIRTUAL`

### `TipoMateria`
Classifies the type of subject.
- `BASICA`
- `ESPECIFICA`
- `COMPARTIDA`

### `TipoInscripcion`
Distinguishes between enrolling for a course vs an exam.
- `CURSADO`
- `EXAMEN`

---

## People & Users

### `Genero`
- `M`
- `F`

### `TipoDocumento`
Identity document types.
- `DNI`
- `PASAPORTE`
- `LC`
- `LE`
- `CI`

### `RolUsuario`
Main user roles in the system.
- `ADMIN`
- `ESTUDIANTE`
- `PROFESOR`

### `RolCargo`
Specific roles within a subject's teaching staff.
- `JEFE_CATEDRA`: Administrative permissions over the subject.
- `DOCENTE`: General teaching staff.

---

## General

### `DiaSemana`
Days of the week.
- `LUNES`
- `MARTES`
- `MIERCOLES`
- `JUEVES`
- `VIERNES`
- `SABADO`
