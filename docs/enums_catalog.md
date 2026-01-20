# Backend Enum Catalog

This document enumerates all Enum types used in the backend application (`src/main/java/com/sysacad/backend/modelo/enums`) to facilitate integration with the frontend. Enums are grouped by the Entity/Model they are primarily associated with.

## Materia

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

---

## Inscripcion

### `TipoInscripcion` (Deprecated)
Distinguishes between enrolling for a course vs an exam. (Legacy entity `Inscripcion`).
- `CURSADO`
- `EXAMEN`

### `EstadoCursada`
Used in `InscripcionCursado`.
- `CURSANDO`
- `REGULAR`
- `PROMOCIONADO`
- `LIBRE`
- `APROBADO`

### `EstadoExamen`
Used in `InscripcionExamen`.
- `PENDIENTE`
- `APROBADO`
- `DESAPROBADO`
- `AUSENTE`

---

## Usuario

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

---

## Comision

### `RolCargo`
Specific roles within a subject's teaching staff.
- `JEFE_CATEDRA`: Administrative permissions over the subject.
- `DOCENTE`: General teaching staff.

---

## Horario

### `DiaSemana`
Days of the week.
- `LUNES`
- `MARTES`
- `MIERCOLES`
- `JUEVES`
- `VIERNES`
- `SABADO`
