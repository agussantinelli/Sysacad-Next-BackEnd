# Backend DTO Catalog

This document enumerates all Data Transfer Objects (DTOs) used in the backend application (`src/main/java/com/sysacad/backend/dto`) to facilitate integration with the frontend.

## Authentication

### `LoginRequest`
Used for user authentication.
- `identificador` (String): Legajo or Email.
- `password` (String): User password.
- `tipoIdentificador` (String): Optional, type of identifier used.

### `AuthResponse`
Returned upon successful authentication.
- `token` (String): JWT token.
- `usuario` (UsuarioResponse): Detailed user information.

---

## Usuario (Users)

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

## Facultad & Carrera

### `FacultadRequest`
- `ciudad` (String)
- `provincia` (String)

### `FacultadResponse`
- `id` (UUID)
- `ciudad` (String)
- `provincia` (String)
- `nombreCompleto` (String): e.g., "UTN - Rosario"

### `CarreraRequest`
- `idFacultad` (UUID): Part of composite key.
- `idCarrera` (String): Code, e.g., "ISI". Part of composite key.
- `nombre` (String)

### `CarreraResponse`
- `idFacultad` (UUID)
- `idCarrera` (String)
- `nombre` (String)
- `nombreFacultad` (String)

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

## Materia & Comision

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

## Inscripcion & Calificacion

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

### `CalificacionRequest`
- `idUsuario` (UUID)
- `idComision` (UUID)
- `tipoInscripcion` (TipoInscripcion enum)
- `vecesTipoInscripcion` (Integer)
- `concepto` (String): e.g., "Parcial 1".
- `nota` (BigDecimal)
