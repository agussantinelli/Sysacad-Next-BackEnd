# Catálogo de Endpoints

Este documento lista todos los endpoints disponibles en la API del backend, agrupados por controlador.

## AuthController
Base URL: `/api/auth`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/login` | Public | Autenticación de usuarios (login). |

## AdminController
Base URL: `/api/admin`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/inscripciones` | ADMIN | Listar todas las inscripciones. Retorna `List<AdminInscripcionDTO>`. |
| `DELETE` | `/inscripciones/{id}` | ADMIN | Eliminar una inscripción. Requiere QueryParam `tipo`. Retorna `Void`. |
| `GET` | `/estadisticas` | ADMIN | Obtener estadísticas generales. Retorna `AdminEstadisticasDTO`. |
| `GET` | `/usuarios/{id}` | ADMIN | Obtener detalle de usuario por ID. Retorna `UsuarioResponse`. |


## AdminFacultadController
Base URL: `/api/admin/facultades`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | ADMIN | Listar todas las facultades. Retorna `List<FacultadResponse>`. |
| `POST` | `/` | ADMIN | Crear una nueva facultad. Body: `FacultadRequest`. Retorna `Void`. |
| `DELETE` | `/{id}` | ADMIN | Eliminar una facultad por ID. Retorna `Void`. |

## AdminCarreraController
Base URL: `/api/admin/carreras`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | ADMIN | Obtener carreras con estadísticas. Retorna `List<CarreraAdminDTO>`. |
| `POST` | `/` | ADMIN | Registrar una nueva carrera. Body: `CarreraRequest`. Retorna `CarreraResponse`. |
| `GET` | `/simples` | ADMIN | Obtener listado simple de carreras disponibles. Retorna `List<CarreraResponse>`. |
| `POST` | `/{carreraId}/facultades/{facultadId}` | ADMIN | Asociar una carrera a una facultad. Retorna `Void`. |
| `GET` | `/{carreraId}/plan/{anio}` | ADMIN | Obtener detalle de plan de estudio. Retorna `PlanDetalleDTO`. |
| `GET` | `/{carreraId}/planes/detallados` | ADMIN | Obtener todos los planes detallados de una carrera. Retorna `List<PlanDetalleDTO>`. |

## AdminMesaController
Base URL: `/api/admin/mesas`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | ADMIN | Obtener mesas con estadísticas. Retorna `List<MesaAdminDTO>`. |
| `GET` | `/turnos` | ADMIN | Obtener turnos de examen con estadísticas globales. Retorna `List<MesaExamenResponse>`. |
| `POST` | `/turnos` | ADMIN | Crear turno de examen. Body: `MesaExamenRequest`. |
| `DELETE` | `/turnos/{id}` | ADMIN | Eliminar turno de examen. Valida que no tenga alumnos inscriptos. |
| `PUT` | `/turnos/{id}` | ADMIN | Editar turno de examen. Valida superposición de fechas. Body: `MesaExamenRequest`. |
| `GET` | `/turnos/{id}` | ADMIN | Obtener detalles completos de un turno específico, incluyendo mesas e inscriptos. |
| `POST` | `/detalles` | ADMIN | Agregar detalle a una mesa. Body: `DetalleMesaRequest`. |
| `DELETE` | `/{idMesa}/detalle/{nroDetalle}` | ADMIN | Eliminar detalle de mesa. |
| `GET` | `/profesores-disponibles` | ADMIN | Buscar profesores para mesa. Params: `idMateria`, `fecha`, `hora`. Retorna `List<ProfesorDisponibleDTO>`. |

## AdminGeneralController
Base URL: `/api/admin/general`

| Method | Endpoint | Roles | Description | Return DTO |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/carreras` | ADMIN | Obtener todas las carreras (simple). | `List<CarreraResponse>` |
| `GET` | `/materias/buscar` | ADMIN | Buscar materias por nombre en una carrera. Params: `idCarrera`, `query`. | `List<SimpleMateriaDTO>` |

## AdminMatriculacionController
Base URL: `/api/admin/matriculacion`

| Method | Endpoint | Roles | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/usuarios/buscar` | ADMIN | Buscar usuarios por legajo parcial. Retorna `List<UsuarioResponse>`. |
| `GET` | `/facultades` | ADMIN | Obtener todas las facultades. Retorna `List<FacultadResponse>`. |
| `GET` | `/carreras` | ADMIN | Obtener carreras por facultad. Retorna `List<CarreraResponse>`. |
| `GET` | `/planes` | ADMIN | Obtener planes por carrera. Retorna `List<PlanDeEstudioResponse>`. |
| `POST` | `/` | ADMIN | Crear matrícula. Body: `MatriculacionRequest`. Retorna `Void`. |

## AdminInscripcionController
Base URL: `/api/admin/inscripcion`

| Method | Endpoint | Roles | Description | Return DTO |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/cursado/materias` | ADMIN | Materias disponibles para cursada. Param: `idAlumno`. | `List<MateriaResponse>` |
| `GET` | `/cursado/comisiones` | ADMIN | Comisiones disponibles. Params: `idAlumno`, `idMateria`. | `List<ComisionDisponibleDTO>` |
| `GET` | `/examen/materias` | ADMIN | Materias disponibles para examen. Param: `idAlumno`. | `List<MateriaResponse>` |
| `GET` | `/examen/mesas` | ADMIN | Mesas de examen disponibles. Params: `idAlumno`, `idMateria`. | `List<MesaExamenDisponibleDTO>` |
| `POST` | `/` | ADMIN | Inscripción (Cursada/Examen). Body: `AdminInscripcionRequest`. | `Void` |

## AdminComisionController
Base URL: `/api/admin/comisiones`

| Method | Endpoint | Roles | Description | Return DTO |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/` | ADMIN | Obtener todas las comisiones detalladas. | `List<AdminComisionDTO>` |
| `POST` | `/` | ADMIN | Crear comisión. Body: `ComisionRequest`. | `Void` |
| `POST` | `/{id}/materias` | ADMIN | Asignar materia y profesores a comisión. Body: `AsignarMateriaComisionRequest`. | `Void` |
| `POST` | `/profesores-disponibles` | ADMIN | Buscar profesores habilitados y sin superposición horaria. Body: `AsignarMateriaComisionRequest` (usa `idMateria`, `horarios`). | `List<ProfesorDisponibleDTO>` |
| `GET` | `/salones-disponibles` | ADMIN | Listar salones no ocupados por comisiones en un turno y año específico. Params: `turno`, `anio`. | `List<SalonResponse>` |

## CarreraController
Base URL: `/api/carreras`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/facultad/{idFacultad}` | Authenticated | Listar carreras de una facultad específica. |
| `POST` | `/planes` | ADMIN | Crear un nuevo plan de estudio. |
| `POST` | `/planes/materias` | ADMIN | Agregar una materia a un plan de estudio. |
| `GET` | `/{idCarrera}/planes/vigentes` | Authenticated | Listar planes de estudio vigentes de una carrera. |

## HealthController
Base URL: `/api/health`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Public | Verificar estado del servidor (Heartbeat). |

## ComisionController
Base URL: `/api/comisiones`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Crear una nueva comisión. |
| `GET` | `/` | ADMIN, PROFESOR | Listar comisiones por año (requiere param `anio`). |
| `GET` | `/{id}` | ADMIN | Buscar una comisión por ID. |
| `POST` | `/{id}/profesores` | ADMIN, PROFESOR | Asignar un profesor a una comisión. |

## FacultadController
Base URL: `/api/facultades`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Crear una nueva facultad regional. |
| `GET` | `/` | Authenticated | Listar todas las facultades. |
| `GET` | `/{id}` | Authenticated | Buscar una facultad por ID. |


## MateriaController
Base URL: `/api/materias`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Crear una nueva materia. |
| `PUT` | `/{id}` | ADMIN, PROFESOR | Actualizar datos de una materia. |
| `GET` | `/` | Authenticated | Listar todas las materias (opcional filtrar por tipo). |
| `GET` | `/{id}` | Authenticated | Buscar una materia por ID. |
| `DELETE` | `/{id}` | ADMIN | Eliminar una materia (baja lógica o física según impl). |
| `GET` | `/{idMateria}/mesas` | ESTUDIANTE | Listar mesas disponibles para una materia específica. Retorna lista con `habilitada` (boolean) y `mensaje`, validando inscripción previa y estado académico. |
| `GET` | `/{id}/correlativas` | Authenticated | Obtener árbol recursivo de correlativas de una materia filtro por `carreraId` y `nroPlan`. |

## PlanDeEstudioController
Base URL: `/api/planes`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Crear un nuevo plan de estudio (Alternativa a CarreraController). |
| `POST` | `/materias` | ADMIN | Agregar materia a plan (Alternativa). |
| `GET` | `/vigentes/{idCarrera}` | Authenticated | Listar planes vigentes (Alternativa). |
| `GET` | `/carrera/{idCarrera}` | Authenticated | Listar todos los planes de una carrera (vigentes o no). |

## ProfesorController
Base URL: `/api/profesores`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/mis-materias` | PROFESOR | Obtiene las materias asignadas al profesor. Retorna `List<MateriaProfesorDTO>`. |
| `GET` | `/mis-comisiones` | PROFESOR | Obtiene comisiones donde dicta clase. Retorna `List<ComisionDetalladaDTO>`. |
| `GET` | `/materias/{idMateria}/comisiones` | PROFESOR | Obtiene comisiones de una materia. Retorna `List<ComisionHorarioDTO>`. |
| `GET` | `/mesas-examen` | PROFESOR | Listado de mesas de examen donde participa. Retorna `List<ProfesorMesaExamenDTO>`. |
| `GET` | `/mesas-examen/{idMesa}/materias` | PROFESOR | Detalles de materias en una mesa específica. Retorna `List<ProfesorDetalleExamenDTO>`. |
| `GET` | `/mesas-examen/{idMesa}/materias/{nroDetalle}/inscriptos` | PROFESOR | Lista de alumnos inscriptos para corregir. Retorna `List<AlumnoExamenDTO>`. |
| `POST` | `/mesas-examen/calificar-lote` | PROFESOR | Carga masiva de notas. Body: `List<CargaNotaItemDTO>`. |
| `GET` | `/certificado-regular` | PROFESOR, ADMIN | Descargar "Certificado de Servicios" en PDF. |
| `GET` | `/comisiones/{idComision}/materias/{idMateria}/inscriptos` | PROFESOR | Obtener lista de alumnos cursando una materia en una comisión. |
| `POST` | `/comisiones/{idComision}/materias/{idMateria}/calificar` | PROFESOR | Cargar lote de notas parciales (concepto) para una cursada. |
| `GET` | `/estadisticas/general` | PROFESOR | Obtener estadísticas globales. QueryParam opcional: `anio`. |
| `GET` | `/estadisticas/materias/{idMateria}` | PROFESOR | Obtener estadísticas de una materia. QueryParam opcional: `anio`. |
| `GET` | `/estadisticas/anios` | PROFESOR | Obtener lista de años con actividad (distinct). |

---

## UsuarioController
Base URL: `/api/usuarios`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Registrar un nuevo usuario (Full data + Estado Default ACTIVO). |
| `PUT` | `/{id}` | Authenticated | Actualizar datos completos de un usuario. |
| `PUT` | `/{id}/estado` | ADMIN | Cambiar estado de un usuario (QueryParam: `nuevoEstado`). |
| `POST` | `/{id}/foto` | Authenticated | Subir foto de perfil. |
| `POST` | `/{id}/cambiar-password` | Authenticated | Cambiar contraseña (requiere password actual). |
| `GET` | `/` | ADMIN | Obtener todos los usuarios (opcional filtrar por rol). |
| `GET` | `/{id}` | Authenticated | Obtener usuario por ID. |
| `GET` | `/buscar/legajo/{legajo}` | ADMIN, PROFESOR | Buscar usuario por legajo. |
| `GET` | `/materia/{idMateria}` | ADMIN, PROFESOR | Obtener docentes de una materia. |
| `GET` | `/alumnos/materia/{idMateria}` | ADMIN, PROFESOR | Obtener alumnos inscriptos en una materia. |
| `DELETE` | `/{id}` | ADMIN | Eliminar un usuario. |

## AvisoController
Base URL: `/api/avisos`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Publicar un nuevo aviso (Estado opcional en body: ACTIVO/OCULTO). |
| `PUT` | `/{id}/estado` | ADMIN | Cambiar estado de un aviso (QueryParam: `nuevoEstado`). |
| `GET` | `/` | Authenticated | Obtener listado de últimos avisos (Solo estado ACTIVO). |
| `POST` | `/{id}/leido` | Authenticated | Marcar un aviso como leído por el usuario actual. |
| `GET` | `/sin-leer/cantidad` | Authenticated | Obtener cantidad de avisos activos no leídos por el usuario. |

## GrupoController
Base URL: `/api/grupos`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | Authenticated | Crear un nuevo grupo de chat. |
| `GET` | `/mis-grupos` | Authenticated | Listar grupos a los que pertenece el usuario. |
| `GET` | `/{id}` | Authenticated | Ver detalle de un grupo. |
| `POST` | `/{id}/miembros` | Authenticated | Agregar miembro a un grupo. |
| `DELETE` | `/{id}/miembros/{idUsuario}` | Authenticated | Eliminar miembro de un grupo. |
| `POST` | `/{id}/mensajes` | Authenticated | Enviar un mensaje al grupo. |
| `GET` | `/{id}/mensajes` | Authenticated | Leer historial de mensajes (paginado). |
| `POST` | `/{id}/marcar-leido` | Authenticated | Marcar el grupo como leído por el usuario. |
| `GET` | `/{id}/miembros` | Authenticated | Obtener miembros con fecha de último acceso. |

## HorarioCursadoController
Base URL: `/api/horarios`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Registrar nuevo horario de cursado. |
| `GET` | `/comision/{idComision}` | Authenticated | Obtener horarios de una comisión. |
| `DELETE` | `/` | ADMIN | Eliminar horario (params: `idComision`, `idMateria`, `dia`, `horaDesde`). |

## SalonController
Base URL: `/api/salones`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Crear un nuevo salón/aula. |
| `GET` | `/facultad/{idFacultad}` | Authenticated | Listar salones detallados de una facultad. |

## SancionController
Base URL: `/api/sanciones`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Aplicar una sanción a un estudiante. |
| `GET` | `/usuario/{idUsuario}` | ADMIN, ESTUDIANTE | Ver sanciones vigentes e históricas de un usuario. |

## MatriculacionController
Base URL: `/api/alumnos`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/mis-carreras-materias/historial/{idMateria}` | ESTUDIANTE | Obtener historial detallado (cursadas y finales) de una materia. |

## MesaExamenController
Base URL: `/api/mesas`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Crear una nueva mesa de examen (Turno). |
| `GET` | `/` | Authenticated | Listar todos los turnos/mesas de examen disponibles. |
| `POST` | `/detalles` | ADMIN | Agregar fecha y hora de examen para una materia en un turno. |
| `GET` | `/disponibles` | ESTUDIANTE | Listar exámenes disponibles para inscribirse (Filtrados por `puedeRendir`). |
| `GET` | `/detalles/{id}/{nroDetalle}` | ESTUDIANTE | Ver detalle de una mesa de examen específica. |

## InscripcionExamenController
Base URL: `/api/inscripciones-examen`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN, ESTUDIANTE | Inscribir a un alumno en el examen final de una materia. |
| `GET` | `/mis-inscripciones` | ESTUDIANTE | Listar inscripciones a exámenes del usuario actual. |
| `DELETE` | `/{id}` | ADMIN, ESTUDIANTE | Dar de baja una inscripción a examen. |
| `POST` | `/{id}/calificar` | ADMIN, PROFESOR | Cargar nota y estado a un examen. |

## InscripcionCursadoController
Base URL: `/api/inscripciones-cursado`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN, ESTUDIANTE | Inscribirse a cursar una materia en una comisión. |
| `GET` | `/mis-cursadas` | ESTUDIANTE | Obtener historial de materias cursadas y notas parciales. |
| `POST` | `/{id}/notas` | ADMIN, PROFESOR | Cargar una nota parcial a una cursada. |
| `GET` | `/actuales` | ESTUDIANTE, ADMIN | Obtener materias que cursa actualmente (Admin puede usar `?idUsuario=UUID`). |
| `GET` | `/actuales` | ESTUDIANTE, ADMIN | Obtener materias que cursa actualmente (Admin puede usar `?idUsuario=UUID`). |
| `GET` | `/materias/{idMateria}/disponibles` | ESTUDIANTE, ADMIN | Obtener comisiones disponibles. Retorna lista con `habilitada` (boolean) y `mensaje` (motivo de rechazo/éxito), validando horarios y correlativas. |
| `PUT` | `/{id}/finalizar` | ADMIN, PROFESOR | Finalizar cursada (Cargar nota final y estado: REGULAR/PROMOCIONADO). Valida reglas de negocio (notas 4-5.5 / >=6). |
| `GET` | `/alumno/{idAlumno}/materia/{idMateria}/notas` | ADMIN, PROFESOR, ESTUDIANTE | Obtener notas parciales de un alumno en una materia. |

## CalendarioPdfController
Base URL: `/api/calendario`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Authenticated | Descargar el calendario académico en formato PDF. |

## CertificadoController
Base URL: `/api/alumnos`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/certificado-regular` | ESTUDIANTE | Descargar "Certificado de Alumno Regular" en PDF. |

## ReporteController
Base URL: `/api/reportes`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/certificados` | ADMIN (Recomendado) | Obtener historial de descargas de certificados (Legajo, Mail, Nombre, Tipo, Fecha). |
