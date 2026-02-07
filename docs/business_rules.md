# Consideraciones del Negocio

Este documento detalla las reglas de negocio y consideraciones técnicas derivadas del análisis por ingeniería inversa del sistema original.

## Reglas de Negocio

1.  **Carreras y Facultades**:
    - Una Facultad puede dictar muchas Carreras.
    - Una Carrera puede dictarse en muchas Facultades (Relación Muchos a Muchos).
    - Las Carreras son entidades únicas (ej. "Ingeniería en Sistemas" es la misma entidad dictada en Rosario o Córdoba), identificadas por un ID único.

    - **Identificador de Plan (`anioPlan`)**: Cada carrera expone su plan vigente (nroPlan) en las respuestas de usuario para facilitar la navegación en el frontend.

2.  **Planes de Estudio**:
    - Los Planes de Estudio pertenecen a una Carrera específica.
    - Se identifican por un año (ej. 2008, 2023) dentro de una Carrera.

3.  **Matriculación**:
    - Un alumno se matricula en una Carrera en una **Facultad específica** (seleccionada de las disponibles para esa carrera), bajo un Plan de Estudio determinado. 
    - La **Facultad** forma parte de la clave primaria de la matriculación, permitiendo que un alumno curse la misma carrera en facultades distintas si fuera necesario (aunque no es común) o distinguiendo administrativamente dónde está inscripto.
    - **Restricción de Matriculación Única Activa**: Un alumno solo puede tener **una matriculación con estado ACTIVO** a la vez. Si intenta matricularse en otra carrera mientras tiene una matriculación activa, el sistema rechazará la operación indicando en qué carrera y plan está actualmente matriculado.


4.  **Cursado y Exámenes**:
    - Las inscripciones a cursado y exámenes están vinculadas a la matriculación del alumno.

5.  **Notificaciones**:
    - Los avisos pueden ser dirigidos a todos o a personas específicas, con seguimiento de estado (Leído, Enviado, etc.).
    - Se envían notificaciones por email automáticas ante: Carga de notas (Cursada/Examen), Bienvenida de nuevo usuario, y nuevos mensajes en grupos de chat.

6.  **Recuperación de Contraseña**:
    - El token de recuperación generado por el sistema tiene una validez de **24 horas**. Pasado ese tiempo, el usuario debe solicitar uno nuevo.

## Consideraciones de Diseño

- **Ingeniería Inversa**: El modelo de datos y la lógica han sido deducidos analizando el comportamiento del sistema original ("Sysacad") de la **FRRO (Facultad Regional Rosario) de la UTN**.
- **Identificadores**: Se migra hacia el uso de IDs únicos (UUID) para entidades principales como Carrera, desacoplándolas de claves compuestas rígidas dependientes de la Facultad.
- **Equivalencias Automáticas**: Se introduce la entidad `Equivalencia` para gestionar la homologación automática de materias entre planes. Al matricularse un alumno en un nuevo plan, el sistema detecta materias aprobadas en planes anteriores y genera créditos equivalentes sin nota numérica.
- **Creación de Comisiones**:
    - **Salón Opcional**: Al crear una comisión, la asignación de un salón es opcional.
    - **Formato de Nombre**: El sistema ajusta automáticamente el nombre de la comisión (ej. "1k1" -> "1K1") asegurando que la letra central sea mayúscula.
    - **Unicidad**: No se permite crear dos comisiones con el mismo nombre.


6.  **Reglas de Validación y Seguridad**:
    - **Inscripciones**:
        - **Validación de duplicados**: No se permite doble inscripción activa a la misma materia o examen.
        - **Validación de Dictado**: Para inscribirse a cursar, la comisión seleccionada debe dictar explícitamente la materia solicitada.
        - **Superposición Horaria**: No se permite la inscripción si el horario de cursado de la nueva materia se solapa (aunque sea parcialmente) con el de cualquier otra materia que el alumno esté cursando actualmente (`Estado: CURSANDO`).
    - **Roles y Permisos**:
        - **Matriculación**: Solo se permite matricular usuarios que posean el rol `ESTUDIANTE`.
        - **Asignación Docente**: Solo un usuario con rol `ADMIN` o el propio `JEFE_CATEDRA` de la materia pueden asignar profesores a una comisión.
        - **Carga de Notas**: Funcionalidad restringida a usuarios con rol `ADMIN` o `PROFESOR`.
    - **Equivalencias Automáticas**:
        - Al realizar una nueva matriculación, el sistema busca coincidencias entre las "Materias Aprobadas" históricas del alumno y las "Materias Origen" definidas en las reglas de equivalencia del nuevo plan.
        - Si existe coincidencia y la materia destino aún no está aprobada, se genera automáticamente un registro de examen con estado `EQUIVALENCIA` (sin nota numérica).
    - **Validación de Correlativas**:
        - **REGULAR**: Para cursar, las correlativas de este tipo deben estar en estado `REGULAR` (Cursada Aprobada) o `APROBADO` (Final/Promoción).
        - **PROMOCIONADA**: Para cursar, las correlativas de este tipo deben estar en estado `PROMOCIONADO` o `APROBADO` (Final).
        - **RENDIR FINAL**: Para rendir el examen final de una materia, el alumno debe tener la materia misma **Regularizada** (Cursada Aprobada) y **TODAS** sus correlativas (tanto Regulares como Promocionadas) **APROBADAS** (Final o Promoción).
        - Esta validación impide la inscripción si no se cumplen los requisitos específicos definidos en el Plan de Estudio.
    - **Reglas de Calificación y Regularidad**:
        - **Regularización**:
            - La nota de fin de cursada debe estar entre **4.00 y 6.00** (sin incluir el 6.00) para obtener el estado `REGULAR`.
        - **Promoción**:
            - La nota de fin de cursada debe ser **6.00 o superior** para obtener el estado `PROMOCIONADO`.
        - **Exámenes Finales**:
            - **Aprobación**: Se requiere una nota mínima de **6.00** para aprobar un examen (`APROBADO`).
            - **Aplazos/Fallos**: Una nota inferior a 6.00 se considera fallo (`DESAPROBADO`).
        - **Pérdida de Regularidad por Aplazos**:
            - Si un alumno con estado `REGULAR` en una materia acumula **4 examenes desaprobados consecutivos** (nota menor a 6 en exámenes finales), **pierde automáticamente su regularidad** y su cursada pasa a estado `LIBRE`.


### Poderes del Jefe de Cátedra
El **Jefe de Cátedra** tiene permisos extendidos sobre las materias que lidera:
- Puede ver **todas las comisiones** de la materia, no solo aquellas donde dicta clases.
- Puede cargar notas y gestionar exámenes para **cualquier comisión** de dicha materia.
- En los listados, se le muestra la información completa de todos los profesores y alumnos de la cátedra.
- **Mensajería**: Tiene permiso para enviar mensajes a los grupos de **cualquier comisión** de su materia, sin necesidad de estar asignado como docente frente a curso.

### Mensajería y Grupos
El sistema de comunicación se rige por las siguientes reglas:
- **Vinculación Estricta**: Un grupo de chat coincide con una dupla única de **Comisión + Materia**.
- **Auto-creación**: Si se intenta enviar un mensaje y el grupo aún no existe, el sistema lo crea automáticamente bajo el formato `{Nombre Comisión} - {Nombre Materia}`.
- **Restricción de Emisión**: Solo los usuarios con rol `PROFESOR` que estén asignados a la comisión específica (o el `JEFE_CATEDRA` de la materia) pueden enviar mensajes.
- **Rol de Estudiante**: Los alumnos inscriptos en la comisión para esa materia actúan como receptores; pueden leer el historial pero no tienen permisos de escritura.

    - **Gestión de Mesas de Examen**:
        - **Nombres de Turno**: Por convención, todos los turnos deben comenzar con la palabra "Turno". Si el usuario ingresa un nombre sin este prefijo, el sistema lo antepone automáticamente (ej. "Febrero 2026" -> "Turno Febrero 2026").
        - **Superposición de Turnos**: No está permitido crear o modificar un Turno de Examen de manera que su rango de fechas (Inicio-Fin) se superponga con el rango de otro turno existente.
        - **Coherencia de Fechas**: La fecha específica de un examen (Mesa de una Materia) debe estar estrictamente comprendida dentro del rango de fechas definido para el Turno de Examen al que pertenece.
        - **Disponibilidad Docente**: El sistema valida que el profesor asignado como Presidente de Mesa no tenga asignada otra mesa de examen en la misma fecha y hora.
        - **Eliminación Segura**: Un Turno de Examen solo puede ser eliminado si **no existen alumnos inscriptos** en ninguna de las materias que lo componen. Si hay inscriptos, la operación se bloquea para preservar la integridad de los datos académicos.
