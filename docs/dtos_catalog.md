# Backend DTO Catalog

This document enumerates all Data Transfer Objects (DTOs) used in the backend application (`src/main/java/com/sysacad/backend/dto`) to facilitate integration with the frontend.
DTOs are grouped by the Model Class they represent.

## Usuario

### `UsuarioRequest`
Used for creating or updating users.
- `legajo` (String)
- `password` (String): Only for creation/updates, never returned.
- `tipoDocumento` (TipoDocumento enum)
- `dni` (String)
- `nombre` (String)
- `apellido` (String)
- `mail` (String)
- `fechaNacimiento` (LocalDate)
- `genero` (Genero enum)
- `telefono` (String)
- `direccion` (String)
- `ciudad` (String)
- `fotoPerfil` (String): URL or base64.
- `fechaIngreso` (LocalDate)
- `tituloAcademico` (String)
- `rol` (RolUsuario enum)
- `estado` (String)

### `UsuarioResponse`
Standard user response.
- `id` (UUID)
- `legajo` (String)
- `tipoDocumento` (TipoDocumento enum)
- `dni` (String)
- `nombre` (String)
- `apellido` (String)
- `mail` (String)
- `fechaNacimiento` (LocalDate)
- `genero` (Genero enum)
- `telefono` (String)
- `direccion` (String)
- `ciudad` (String)
- `fotoPerfil` (String)
- `fechaIngreso` (LocalDate)
- `tituloAcademico` (String)
- `rol` (RolUsuario enum)
- `estado` (String)
- `tipoIdentificador` (String)
- `anioIngreso` (Integer)
- `passwordChangeRequired` (Boolean)
- `carreras` (List<InfoCarrera>): List of associated careers.

#### Inner Class: `InfoCarrera`
- `nombreCarrera` (String)
- `facultad` (String)

---

## FacultadRegional

### `FacultadRequest`
- `ciudad` (String)
- `provincia` (String)

### `FacultadResponse`
- `id` (UUID)
- `ciudad` (String)
- `provincia` (String)
- `nombreCompleto` (String): e.g., "UTN - Rosario"

---

## Carrera

### `CarreraRequest`
- `idFacultad` (UUID): Part of composite key.
- `idCarrera` (String): Code, e.g., "ISI". Part of composite key.
- `nombre` (String)

### `CarreraResponse`
- `idFacultad` (UUID)
- `idCarrera` (String)
- `nombre` (String)
- `nombreFacultad` (String)

---

## PlanDeEstudio

### `PlanDeEstudioRequest`
- `idFacultad` (UUID)
- `idCarrera` (String)
- `nombrePlan` (String): Identifier, e.g., "Plan 2008".
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)
- `esVigente` (Boolean)

### `PlanDeEstudioResponse`
- `nombrePlan` (String)
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)
- `esVigente` (Boolean)
- `nombreCarrera` (String)

---

## Materia

### `MateriaRequest`
- `nombre` (String)
- `descripcion` (String)
- `tipoMateria` (TipoMateria enum)
- `duracion` (DuracionMateria enum)
- `cuatrimestreDictado` (CuatrimestreDictado enum)
- `horasCursado` (Short)
- `rendirLibre` (Boolean)
- `optativa` (Boolean)
- `idsCorrelativas` (List<UUID>): List of IDs for prerequisite subjects.

### `MateriaResponse`
- `id` (UUID)
- `nombre` (String)
- `descripcion` (String)
- `tipoMateria` (TipoMateria enum)
- `duracion` (DuracionMateria enum)
- `cuatrimestreDictado` (CuatrimestreDictado enum)
- `horasCursado` (Short)
- `rendirLibre` (Boolean)
- `optativa` (Boolean)
- `correlativas` (List<SimpleMateriaDTO>)

#### Inner Class: `SimpleMateriaDTO`
- `id` (UUID)
- `nombre` (String)

---

## Comision

### `ComisionRequest`
- `nombre` (String)
- `turno` (String)
- `anio` (Integer)
- `idSalon` (UUID)
- `idsMaterias` (List<UUID>)
- `idsProfesores` (List<UUID>)

### `ComisionResponse`
- `id` (UUID)
- `nombre` (String)
- `turno` (String)
- `anio` (Integer)
- `nombreSalon` (String)
- `ubicacionSalon` (String)
- `materiasNombres` (List<String>)
- `profesores` (List<ProfesorResumenDTO>)

#### Inner Class: `ProfesorResumenDTO`
- `legajo` (String)
- `nombreCompleto` (String)

---

## Inscripcion

### `InscripcionRequest`
- `idUsuario` (UUID)
- `idComision` (UUID)
- `tipo` (TipoInscripcion enum): "CURSADO" or "EXAMEN".
- `vecesTipo` (Integer): Default 1.
- `condicion` (String): e.g., "REGULAR", "LIBRE".

### `InscripcionResponse`
- `nombreMateria` (String)
- `comision` (String)
- `anioCursado` (Integer)
- `tipo` (TipoInscripcion enum)
- `fechaInscripcion` (String): Formatted "dd/MM/yyyy HH:mm".
- `condicion` (String)
- `notaFinal` (BigDecimal)
- `idMateria` (UUID): Reference ID.
- `idComision` (UUID): Reference ID.

---

## Calificacion

### `CalificacionRequest`
- `idUsuario` (UUID)
- `idComision` (UUID)
- `tipoInscripcion` (TipoInscripcion enum)
- `vecesTipoInscripcion` (Integer)
- `concepto` (String): e.g., "Parcial 1".
- `nota` (BigDecimal)

### `CalificacionResponse`
- `idUsuario` (UUID)
- `nombreUsuario` (String)
- `legajoUsuario` (String)
- `idComision` (UUID)
- `nombreComision` (String)
- `materia` (String)
- `tipoInscripcion` (TipoInscripcion enum)
- `vecesTipoInscripcion` (Integer)
- `concepto` (String)
- `nota` (BigDecimal)
- `fecha` (LocalDate)

---

## Aviso

### `AvisoRequest`
- `titulo` (String)
- `descripcion` (String)
- `estado` (String)

### `AvisoResponse`
- `id` (UUID)
- `titulo` (String)
- `descripcion` (String)
- `fechaEmision` (LocalDateTime)
- `estado` (String)

---

## Salon

### `SalonRequest`
- `idFacultad` (UUID)
- `nombre` (String)
- `piso` (String)

### `SalonResponse`
- `id` (UUID)
- `idFacultad` (UUID)
- `nombreFacultad` (String)
- `nombre` (String)
- `piso` (String)

---

## Sancion

### `SancionRequest`
- `idUsuario` (UUID)
- `motivo` (String)
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)

### `SancionResponse`
- `id` (UUID)
- `idUsuario` (UUID)
- `nombreUsuario` (String)
- `legajoUsuario` (String)
- `motivo` (String)
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)

---

## Horario

### `HorarioRequest`
- `idComision` (UUID)
- `idMateria` (UUID)
- `dia` (DiaSemana enum)
- `horaDesde` (LocalTime)
- `horaHasta` (LocalTime)

### `HorarioResponse`
- `idComision` (UUID)
- `nombreComision` (String)
- `idMateria` (UUID)
- `nombreMateria` (String)
- `dia` (DiaSemana enum)
- `horaDesde` (LocalTime)
- `horaHasta` (LocalTime)

---

## Matriculacion / Academico

### `CarreraMateriasDTO`
Response structure for `/api/alumnos/mis-carreras-materias`.
- `idCarrera` (String)
- `nombreCarrera` (String)
- `nombrePlan` (String)
- `materias` (List<EstudianteMateriaDTO>)

### `EstudianteMateriaDTO`
Enriched subject details for a specific student.
- `idMateria` (UUID)
- `nombre` (String)
- `nivel` (Short): Year/Level of the subject in the plan.
- `estado` (String): "PENDIENTE", "CURSANDO", "REGULAR", "APROBADA", "LIBRE".
- `nota` (String): Final grade or "-" if not approved.
- `sePuedeInscribir` (Boolean): Calculated based on correlatives and current status.
- `esElectiva` (Boolean)
