# Catálogo de Endpoints

Este documento lista todos los endpoints disponibles en la API del backend, agrupados por controlador.

## AuthController
Base URL: `/api/auth`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/login` | Public | Autenticación de usuarios (login). |

## CarreraController
Base URL: `/api/carreras`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Registrar una nueva carrera. |
| `GET` | `/facultad/{idFacultad}` | Authenticated | Listar carreras de una facultad específica. |
| `POST` | `/planes` | ADMIN | Crear un nuevo plan de estudio. |
| `POST` | `/planes/materias` | ADMIN | Agregar una materia a un plan de estudio. |
| `GET` | `/{idCarrera}/planes/vigentes` | Authenticated | Listar planes de estudio vigentes de una carrera. |

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

## InscripcionController
Base URL: `/api/inscripciones`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN, ESTUDIANTE | Inscribir un alumno a una comisión. |
| `GET` | `/alumno/{idAlumno}` | ADMIN, ESTUDIANTE | Obtener historial de inscripciones de un alumno. |
| `POST` | `/notas` | ADMIN, PROFESOR | Cargar nota a una inscripción. |
| `GET` | `/validar-correlatividad` | ADMIN, ESTUDIANTE | Validar si un alumno puede cursar una materia. |

## MateriaController
Base URL: `/api/materias`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Crear una nueva materia. |
| `PUT` | `/{id}` | ADMIN, PROFESOR | Actualizar datos de una materia. |
| `GET` | `/` | Authenticated | Listar todas las materias (opcional filtrar por tipo). |
| `GET` | `/{id}` | Authenticated | Buscar una materia por ID. |
| `DELETE` | `/{id}` | ADMIN | Eliminar una materia (baja lógica o física según impl). |

## PlanDeEstudioController
Base URL: `/api/planes`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Crear un nuevo plan de estudio (Alternativa a CarreraController). |
| `POST` | `/materias` | ADMIN | Agregar materia a plan (Alternativa). |
| `GET` | `/vigentes/{idCarrera}` | Authenticated | Listar planes vigentes (Alternativa). |
| `GET` | `/carrera/{idCarrera}` | Authenticated | Listar todos los planes de una carrera (vigentes o no). |

## UsuarioController
Base URL: `/api/usuarios`

| Método | Endpoint | Roles / Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | ADMIN | Registrar un nuevo usuario. |
| `PUT` | `/{id}` | Authenticated | Actualizar datos de un usuario. |
| `POST` | `/{id}/foto` | Authenticated | Subir foto de perfil. |
| `GET` | `/` | ADMIN | Obtener todos los usuarios (opcional filtrar por rol). |
| `GET` | `/{id}` | Authenticated | Obtener usuario por ID. |
| `GET` | `/buscar/legajo/{legajo}` | ADMIN, PROFESOR | Buscar usuario por legajo. |
| `GET` | `/materia/{idMateria}` | ADMIN, PROFESOR | Obtener docentes de una materia. |
| `GET` | `/alumnos/materia/{idMateria}` | ADMIN, PROFESOR | Obtener alumnos inscriptos en una materia. |
| `DELETE` | `/{id}` | ADMIN | Eliminar un usuario. |
