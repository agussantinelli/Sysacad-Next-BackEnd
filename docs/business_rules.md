# Consideraciones del Negocio

Este documento detalla las reglas de negocio y consideraciones técnicas derivadas del análisis por ingeniería inversa del sistema original.

## Reglas de Negocio

1.  **Carreras y Facultades**:
    - Una Facultad puede dictar muchas Carreras.
    - Una Carrera puede dictarse en muchas Facultades (Relación Muchos a Muchos).
    - Las Carreras son entidades únicas (ej. "Ingeniería en Sistemas" es la misma entidad dictada en Rosario o Córdoba), identificadas por un ID único.

2.  **Planes de Estudio**:
    - Los Planes de Estudio pertenecen a una Carrera específica.
    - Se identifican por un año (ej. 2008, 2023) dentro de una Carrera.

3.  **Matriculación**:
    - Un alumno se matricula en una Carrera en una **Facultad específica** (seleccionada de las disponibles para esa carrera), bajo un Plan de Estudio determinado. 
    - La **Facultad** forma parte de la clave primaria de la matriculación, permitiendo que un alumno curse la misma carrera en facultades distintas si fuera necesario (aunque no es común) o distinguiendo administrativamente dónde está inscripto.

4.  **Cursado y Exámenes**:
    - Las inscripciones a cursado y exámenes están vinculadas a la matriculación del alumno.

5.  **Notificaciones**:
    - Los avisos pueden ser dirigidos a todos o a personas específicas, con seguimiento de estado (Leído, Enviado, etc.).

## Consideraciones de Diseño

- **Ingeniería Inversa**: El modelo de datos y la lógica han sido deducidos analizando el comportamiento del sistema original ("Sysacad") de la **FRRO (Facultad Regional Rosario) de la UTN**.
- **Identificadores**: Se migra hacia el uso de IDs únicos (UUID) para entidades principales como Carrera, desacoplándolas de claves compuestas rígidas dependientes de la Facultad.
- **Equivalencias Automáticas**: Se introduce la entidad `Equivalencia` para gestionar la homologación automática de materias entre planes. Al matricularse un alumno en un nuevo plan, el sistema detecta materias aprobadas en planes anteriores y genera créditos equivalentes sin nota numérica.

6.  **Reglas de Validación y Seguridad**:
    - **Inscripciones**:
        - **Validación de duplicados**: No se permite doble inscripción activa a la misma materia o examen.
        - **Validación de Dictado**: Para inscribirse a cursar, la comisión seleccionada debe dictar explícitamente la materia solicitada.
    - **Roles y Permisos**:
        - **Matriculación**: Solo se permite matricular usuarios que posean el rol `ESTUDIANTE`.
        - **Asignación Docente**: Solo un usuario con rol `ADMIN` o el propio `JEFE_CATEDRA` de la materia pueden asignar profesores a una comisión.
        - **Carga de Notas**: Funcionalidad restringida a usuarios con rol `ADMIN` o `PROFESOR`.
    - **Equivalencias Automáticas**:
        - Al realizar una nueva matriculación, el sistema busca coincidencias entre las "Materias Aprobadas" históricas del alumno y las "Materias Origen" definidas en las reglas de equivalencia del nuevo plan.
        - Si existe coincidencia y la materia destino aún no está aprobada, se genera automáticamente un registro de examen con estado `EQUIVALENCIA` (sin nota numérica).
    - **Validación de Correlativas (Visual)**:
        - Para habilitar la inscripción a cursado, se verifica que todas las materias correlativas requeridas estén en estado `REGULAR` o `APROBADA`.
        - Esta validación impide la selección de materias bloqueadas en la interfaz de usuario.
