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
- `nroCarrera` (Integer): Sequential number, e.g., 1. Part of composite key.
- `alias` (String): E.g., "ISI".
- `nombre` (String)

### `CarreraResponse`
- `idFacultad` (UUID)
- `nroCarrera` (Integer)
- `alias` (String)
- `nombre` (String)
- `nombreFacultad` (String)

---

## PlanDeEstudio

### `PlanDeEstudioRequest`
- `idFacultad` (UUID)
- `nroCarrera` (Integer)
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
- `nroCarrera` (Integer)
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

---

## MesaExamen

### `MesaExamenRequest`
- `nombre` (String): e.g., "Turno Febrero 2026".
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)

### `MesaExamenResponse`
- `id` (UUID)
- `nombre` (String)
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)
- `detalles` (List<DetalleMesaExamenResponse>)

### `DetalleMesaExamenRequest`
- `idMesaExamen` (UUID)
- `nroDetalle` (Integer): Sequential number for the detail within the exam table.
- `idMateria` (UUID)
- `idPresidente` (UUID)
- `diaExamen` (LocalDate)
- `horaExamen` (LocalTime)

### `DetalleMesaExamenResponse`
- `idMesaExamen` (UUID)
- `nroDetalle` (Integer)
- `nombreMateria` (String)
- `idMateria` (UUID)
- `nombrePresidente` (String)
- `idPresidente` (UUID)
- `diaExamen` (LocalDate)
- `horaExamen` (LocalTime)

---

## InscripcionExamen

### `InscripcionExamenRequest`
- `idUsuario` (UUID): Optional (inferred from token if missing).
- `idMesaExamen` (UUID)
- `nroDetalle` (Integer)

### `InscripcionExamenResponse`
- `id` (UUID)
- `nombreAlumno` (String)
- `legajoAlumno` (String)
- `nombreMateria` (String)
- `fechaExamen` (LocalDate)
- `horaExamen` (LocalTime)
- `fechaInscripcion` (LocalDateTime)
- `fechaInscripcion` (LocalDateTime)
- `estado` (String): e.g., "PENDIENTE" (mapped from EstadoExamen).
- `nota` (BigDecimal)

### `CargaNotaExamenRequest`
- `nota` (BigDecimal)
- `estado` (EstadoExamen enum)

---

## InscripcionCursado

### `InscripcionCursadoRequest`
- `idUsuario` (UUID): Optional if token used.
- `idMateria` (UUID)
- `idComision` (UUID)

### `InscripcionCursadoResponse`
- `id` (UUID)
- `nombreMateria` (String)
- `nombreComision` (String)
- `anioCursado` (Integer)
- `estado` (String): e.g., "CURSANDO" (mapped from EstadoCursada).
- `notaFinal` (BigDecimal)
- `fechaPromocion` (LocalDate)
- `fechaInscripcion` (LocalDateTime)
- `calificaciones` (List<CalificacionCursadaResponse>)

### `CalificacionCursadaRequest`
- `descripcion` (String)
- `nota` (BigDecimal)

### `CalificacionCursadaResponse`
- `id` (UUID)
- `descripcion` (String)
- `nota` (BigDecimal)
- `fecha` (LocalDate)

