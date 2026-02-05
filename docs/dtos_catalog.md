# Backend DTO Catalog

This document enumerates all Data Transfer Objects (DTOs) used in the backend application, grouped by their package structure in `src/main/java/com/sysacad/backend/dto`.

## Admin

### `AdminInscripcionDTO`
- `id` (UUID)
- `tipo` (String): "CURSADA" o "EXAMEN".
- `idAlumno` (UUID)
- `nombreAlumno` (String)
- `legajoAlumno` (String)
- `nombreMateria` (String)
- `comision` (String): Nombre de comisión (solo para Cursada, "N/A" para examen).
- `fechaInscripcion` (LocalDateTime)
- `estado` (String)

### `AdminEstadisticasDTO`
- Inherits from `ProfesorEstadisticasDTO` (see below).
- Provides aggregated statistics filtered by Year, Faculty, and Career.

### `MatriculacionRequest`
- `idUsuario` (UUID)
- `idFacultad` (UUID)
- `idCarrera` (UUID)
- `nroPlan` (Integer)

---

## Auth

### `LoginRequest`
- `identificador` (String)
- `password` (String)
- `tipoIdentificador` (String)


### `AuthResponse`
- `token` (String)
- `usuario` (UsuarioResponse)
- `bootId` (String): UUID of the current server instance.

---

## Aviso

### `AvisoRequest`
- `titulo` (String)
- `descripcion` (String)
- `estado` (EstadoAviso enum)

### `AvisoResponse`
- `id` (UUID)
- `titulo` (String)
- `descripcion` (String)
- `fechaEmision` (LocalDateTime)
- `estado` (EstadoAviso enum)
- `visto` (Boolean): Indica si el usuario actual ya leyó este aviso.

---

## CalificacionCursada

### `CalificacionCursadaRequest`
- `descripcion` (String)
- `nota` (BigDecimal)

### `CalificacionCursadaResponse`
- `id` (UUID)
- `descripcion` (String)
- `nota` (BigDecimal)
- `fecha` (LocalDate)

---

## Carrera

### `CarreraRequest`
- `idFacultad` (UUID)
- `alias` (String)
- `nombre` (String)

### `CarreraResponse`
- `id` (UUID)
- `alias` (String)
- `nombre` (String)
- `facultades` (List<String>)

---

## CarreraMaterias

### `CarreraMateriasDTO`
Response for `/api/alumnos/mis-carreras-materias`.
- `idCarrera` (UUID)
- `nombreCarrera` (String)
- `idFacultad` (UUID)
- `nombreFacultad` (String)
- `nombrePlan` (String)
- `materias` (List<EstudianteMateriaDTO>): List of subjects (excludes "PENDIENTE").

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

### `ComisionHorarioDTO`
- `idComision` (UUID)
- `nombre` (String)
- `anio` (Integer)
- `turno` (String)
- `salon` (String)
- `horarios` (List<String>): Formatted schedules (e.g., "LUNES 08:00 - 12:00")
- `profesores` (List<String>): List of all professors teaching in this commission for this subject.
- `cantidadAlumnos` (Long): Number of students enrolled in this commission for this subject

### `ComisionDetalladaDTO`
- `idComision` (UUID)
- `nombre` (String)
- `anio` (Integer)
- `turno` (String)
- `salon` (String)
- `horarios` (List<String>)
- `profesores` (List<String>): List of all professors teaching in this commission for this subject.
- `cantidadAlumnos` (Long)
- `nombreMateria` (String)
- `idMateria` (UUID)



### `AlumnoCursadaDTO`
- `idInscripcion` (UUID)
- `nombre` (String)
- `apellido` (String)
- `legajo` (Long)
- `estado` (EstadoCursada enum)
- `notaFinal` (BigDecimal): Grade if promoted or regularized.
- `calificaciones` (List<CalificacionDTO>)

#### Inner Class: `CalificacionDTO`
- `concepto` (String)
- `nota` (BigDecimal)
- `fecha` (LocalDate)

### `CargaNotasCursadaDTO`
- `concepto` (String)
- `esNotaFinal` (Boolean)
- `notas` (List<NotaCursadaItemDTO>)

#### Inner Class: `NotaCursadaItemDTO`
- `idInscripcion` (UUID)
- `nota` (BigDecimal)
- `estado` (String): Optional. Value from `EstadoCursada` (e.g., "REGULAR", "PROMOCIONADO"). Required if `esNotaFinal` is true.

---

## DetalleMesaExamen

### `DetalleMesaExamenRequest`
- `idMesaExamen` (UUID)
- `nroDetalle` (Integer)
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

## EstudianteMateria

### `EstudianteMateriaDTO`
- `idMateria` (UUID)
- `nombre` (String)
- `nivel` (Short)
- `estado` (String)
- `nota` (String)
- `sePuedeInscribir` (Boolean)
- `esElectiva` (Boolean)
- `horasCursado` (Short)
- `cuatrimestre` (String)
- `correlativas` (List<CorrelativaDTO>)
- `tieneInscripcionExamenPendiente` (Boolean)

#### Inner Class: `CorrelativaDTO`
- `nombre` (String)
- `condicion` (String): "REGULAR" or "PROMOCIONADA".

---

## Facultad

### `FacultadRequest`
- `ciudad` (String)
- `provincia` (String)

### `FacultadResponse`
- `id` (UUID)
- `ciudad` (String)
- `provincia` (String)
- `nombreCompleto` (String)

---

---

## Grupo

### `GrupoRequest`
- `nombre` (String)
- `descripcion` (String)
- `tipo` (String)
- `idsIntegrantes` (List<UUID>): Initial list of members to add.

### `GrupoResponse`
- `id` (UUID)
- `nombre` (String)
- `descripcion` (String)
- `tipo` (String)
- `estado` (EstadoGrupo enum)
- `fechaCreacion` (LocalDateTime)

### `MiembroGrupoRequest`
- `idUsuario` (UUID)
- `rol` (RolGrupo enum)

### `MiembroGrupoResponse`
- `idUsuario` (UUID)
- `nombre` (String)
- `apellido` (String)
- `rol` (RolGrupo enum)
- `fechaUnion` (LocalDateTime)
- `ultimoAcceso` (LocalDateTime)

### `MensajeGrupoRequest`
- `contenido` (String)
- `idUsuarioRemitente` (UUID): Optional/Ignored (inferred from token).

### `MensajeGrupoResponse`
- `id` (UUID)
- `idGrupo` (UUID)
- `idUsuarioRemitente` (UUID)
- `nombreRemitente` (String)
- `contenido` (String)
- `editado` (Boolean)
- `fechaEnvio` (LocalDateTime)

---

## Historial

### `HistorialMateriaDTO`
Response for `/api/alumnos/mis-carreras-materias/historial/{idMateria}`.
- `nombreMateria` (String)
- `cursadas` (List<DetalleCursadaDTO>)
- `finales` (List<DetalleFinalDTO>)

### `DetalleCursadaDTO`
- `fechaInscripcion` (LocalDate)
- `comision` (String)
- `estado` (String)
- `nota` (String)
- `tomo` (String)
- `folio` (String)

### `DetalleFinalDTO`
- `fechaExamen` (LocalDate)
- `turno` (String)
- `estado` (String)
- `nota` (String)
- `tomo` (String)
- `folio` (String)

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

## InscripcionCursado

### `InscripcionCursadoRequest`
- `idUsuario` (UUID)
- `idMateria` (UUID)
- `idComision` (UUID)

### `InscripcionCursadoResponse`
- `id` (UUID)
- `nombreMateria` (String)
- `nombreComision` (String)
- `anioCursado` (Integer)
- `estado` (String)
- `notaFinal` (BigDecimal)
- `fechaPromocion` (LocalDate)
- `fechaInscripcion` (LocalDateTime)
- `calificaciones` (List<CalificacionCursadaResponse>)

### `ComisionDisponibleDTO`
Response for `/api/inscripciones-cursado/materias/{idMateria}/disponibles`.
- `idComision` (UUID)
- `nombreComision` (String)
- `turno` (String)
- `ubicacion` (String): e.g., "Aula 305 (Rosario)".
- `horarios` (List<String>): List of formatted schedules (e.g. "LUNES 18:00 - 22:00").
- `profesores` (List<String>): List of professors teaching *this* subject in *this* commission.
- `habilitada` (Boolean): Indicates if the user can enroll.
- `mensaje` (String): Reason for eligibility/ineligibility (e.g. "Disponible", "Superposición con...").

---

## InscripcionExamen

### `InscripcionExamenRequest`
- `idUsuario` (UUID)
- `idDetalleMesa` (UUID)
- `nroDetalle` (Integer)

### `InscripcionExamenResponse`
- `id` (UUID)
- `nombreAlumno` (String)
- `legajoAlumno` (String)
- `nombreMateria` (String)
- `fechaExamen` (LocalDate)
- `horaExamen` (LocalTime)
- `fechaInscripcion` (LocalDateTime)
- `estado` (String)
- `nota` (BigDecimal)

### `CargaNotaExamenRequest`
- `nota` (BigDecimal)
- `estado` (EstadoExamen enum)

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
- `idsCorrelativas` (List<UUID>)

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

### `CorrelativaArbolDTO`
Response for `/api/materias/{id}/correlativas`.
- `idMateria` (UUID)
- `nombre` (String)
- `tipoCorrelatividad` (String): "REGULAR", "PROMOCIONADA"
- `correlativas` (List<CorrelativaArbolDTO>): Recursive list of prerequisites.

---

## MesaExamen

### `MesaExamenRequest`
- `nombre` (String)
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)

### `MesaExamenResponse`
- `id` (UUID)
- `nombre` (String)
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)
    - `detalles` (List<DetalleMesaExamenResponse>)

### `MesaExamenDisponibleDTO`
Response for `/api/materias/{idMateria}/mesas`.
- `idDetalleMesa` (UUID): ID of the exam table (MesaExamen ID).
- `nroDetalle` (Integer): Detail number (required for inscription).
- `nombreMesa` (String): e.g., "Turno Febrero 2026".
- `fecha` (LocalDate)
- `hora` (LocalTime)
- `presidente` (String)
- `habilitada` (Boolean): If the student can enroll.
- `mensaje` (String): Reason for eligibility/ineligibility.

---

## PlanDeEstudio

### `PlanDeEstudioRequest`
- `idCarrera` (UUID)
- `nroPlan` (Integer)
- `nombrePlan` (String)
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)
- `esVigente` (Boolean)

### `PlanDeEstudioResponse`
- `nroPlan` (Integer)
- `nombrePlan` (String)
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)
- `esVigente` (Boolean)
- `nombreCarrera` (String)
- `idCarrera` (UUID)

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

## Usuario

### `UsuarioRequest`
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

### `CambioPasswordRequest`
- `passwordActual` (String)
- `passwordNueva` (String)

### `UsuarioResponse`
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

## MateriaProfesor

### `MateriaProfesorDTO`
- `id` (UUID)
- `nombre` (String)
- `nivel` (Integer)
- `plan` (String)
- `cargo` (RolCargo enum)
- `jefeCatedra` (String, nullable): Name of the head professor (null if the professor IS the head)

---

## Examen (Profesor)

### `ProfesorMesaExamenDTO`
- `id` (UUID)
- `nombre` (String)
- `fechaInicio` (LocalDate)
- `fechaFin` (LocalDate)
- `cantidadMateriasInvolucradas` (Integer)

### `ProfesorDetalleExamenDTO`
- `idMesaExamen` (UUID)
- `nroDetalle` (Integer)
- `idMateria` (UUID)
- `nombreMateria` (String)
- `anioMateria` (String)
- `fecha` (LocalDate)
- `hora` (LocalTime)
- `cantidadInscriptos` (Long)
- `todosCorregidos` (Boolean): Indica si todos los inscriptos tienen nota/estado final.
- `tribunal` (List<MiembroTribunalDTO>)

#### MiembroTribunalDTO
- `nombre`: String (Nombre y Apellido)
- `rol`: String (PRESIDENTE, AUXILIAR)

#### AlumnoExamenDTO
- `idInscripcion`: UUID (ID de InscripcionExamen)
- `nombre`: String
- `apellido`: String
- `legajo`: Long
- `estado`: String (PENDIENTE, APROBADO, DESAPROBADO, AUSENTE)
- `nota`: BigDecimal
- `tomo`: String
- `folio`: String

#### CargaNotaItemDTO
- `idInscripcion`: UUID
- `nota`: BigDecimal
- `estado`: String (APROBADO, DESAPROBADO, AUSENTE)
- `tomo`: String (Opcional, si aprueba)
- `folio`: String (Opcional, si aprueba)

### `ProfesorCertificadoDTO`
- `nombreCompleto`: String
- `dni`: String
- `legajo`: String
- `rol`: String (e.g. "PROFESOR", "ADMINISTRADOR")
- `fechaEmision`: LocalDate

### `ProfesorEstadisticasDTO`
- `cantidadTotalAlumnos` (long): Total enrolled (sum of all statuses).
- `cantidadPromocionados` (long)
- `cantidadRegulares` (long)
- `cantidadLibres` (long)
- `notaPromedio` (BigDecimal): Average numeric grade.
- `cantidadTotalInscriptosExamen` (long)
- `cantidadAprobadosExamen` (long)
- `cantidadDesaprobadosExamen` (long)
- `cantidadAusentesExamen` (long)


---

## Reporte

### `ReporteCertificadoDTO`
- `legajo` (String)
- `mail` (String)
- `nombre` (String)
- `apellido` (String)
- `tipoCertificado` (String)
- `fecha` (LocalDateTime)
