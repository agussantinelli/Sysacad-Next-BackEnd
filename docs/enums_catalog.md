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
    
### `TipoCorrelatividad`
Defines the requirement type for a prerequisite subject.
- `REGULAR`: Requires the subject to be Regularized or Approved.
- `PROMOCIONADA`: Requires the subject to be Promoted or Approved (Final).

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
- `ELIMINADO`
- `EN_BORRADOR`

### `EstadoAviso`
Status of a general notice.
- `ACTIVO`: Visible to all target users.
- `OCULTO`: Hidden from users (e.g. draft or expired).

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
