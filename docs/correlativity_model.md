# Modelo de Correlatividades

El sistema implementa un modelo de correlatividades refinado que permite definir requisitos específicos para cursar materias, basado en el estado académico del alumno en las materias correlativas.

## Entidades

### Correlatividad
Entidad asociativa que vincula una **Materia** con su **Correlativa**, especificando el tipo de requisito.

Campos:
- `id`: Identificador único.
- `materia`: La materia que tiene el requisito.
- `correlativa`: La materia que es requisito.
- `tipo`: `TipoCorrelatividad` (REGULAR, PROMOCIONADA).

### TipoCorrelatividad (Enum)
Define el nivel de aprobación requerido de la materia correlativa.

- **REGULAR**: El alumno debe tener la materia correlativa **Regularizada** (cursada aprobada) o **Aprobada** (final aprobado o promocionada).
- **PROMOCIONADA**: El alumno debe tener la materia correlativa **Promocionada** o con **Final Aprobado**.

## Lógica de Validación

La validación se realiza en `CorrelatividadService.puedeCursar(...)`.

1.  **Requisito REGULAR**:
    - Se verifica si el alumno tiene la inscripción a cursada en estado `REGULAR` o `PROMOCIONADO`.
    - O si tiene un examen final en estado `APROBADO`.

2.  **Requisito PROMOCIONADA**:
    - Se verifica si el alumno tiene la inscripción a cursada en estado `PROMOCIONADO`.
    - O si tiene un examen final en estado `APROBADO`.

## Base de Datos (Seeder)

El `UTNSeeder` ha sido actualizado para reflejar los planes de estudio reales de:
- Ingeniería en Sistemas de Información (ISI)
- Ingeniería Civil (IC)
- Ingeniería Química (IQ)
- Ingeniería Mecánica (IM)
- Ingeniería Eléctrica (IEE)

Cada materia define sus correlativas separadas por tipo (Regulares y Promocionadas) según el plan de estudios.
