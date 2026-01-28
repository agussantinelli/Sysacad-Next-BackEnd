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


### `EstadoCursada`
Used in `InscripcionCursado`.
- `CURSANDO`
- `REGULAR`
- `PROMOCIONADO`
- `LIBRE`

### `EstadoExamen`
Used in `InscripcionExamen`.
- `PENDIENTE`
- `APROBADO`
- `DESAPROBADO`
- `AUSENTE`
- `EQUIVALENCIA`

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

---

## Aviso

### `EstadoAvisoPersona`
Status of a notification for a specific user.
- `LEIDO`
- `ENVIADO`
- `BORRADO`
- `ELIMINADO`
- `EN_BORRADOR`

---

## Grupo

### `RolGrupo`
Role of a user within a communication group.
- `ADMIN`
- `MIEMBRO`

### `EstadoGrupo`
Status of a group.
- `ACTIVO`
- `ARCHIVADO`
- `ELIMINADO`
