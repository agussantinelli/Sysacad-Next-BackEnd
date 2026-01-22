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
3.  **Matriculación**:
    - Un alumno se matricula en una Carrera en una **Facultad específica** (seleccionada de las disponibles para esa carrera), bajo un Plan de Estudio determinado. 
    - La **Facultad** forma parte de la clave primaria de la matriculación, permitiendo que un alumno curse la misma carrera en facultades distintas si fuera necesario (aunque no es común) o distinguiendo administrativamente dónde está inscripto.

4.  **Cursado y Exámenes**:
    - Las inscripciones a cursado y exámenes están vinculadas a la matriculación del alumno.

## Consideraciones de Diseño

- **Ingeniería Inversa**: El modelo de datos y la lógica han sido deducidos analizando el comportamiento del sistema original ("Sysacad").
- **Identificadores**: Se migra hacia el uso de IDs únicos (UUID) para entidades principales como Carrera, desacoplándolas de claves compuestas rígidas dependientes de la Facultad.
