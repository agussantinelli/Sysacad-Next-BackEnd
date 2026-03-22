---
name: test-enforcement
description: Mandatory rule requiring a corresponding test file for every business logic file.
---

# 🛡️ Sysacad Next - Test Enforcement

Para mantener la confiabilidad total y prevenir regresiones en el sistema académico, es OBLIGATORIO que cada archivo que contenga lógica de negocio (Services) o endpoints (Controllers) tenga su correspondiente archivo de prueba.

## ⚖️ Directrices

1.  **Regla del Uno**: Cada archivo `.java` en `service/` o `controller/` DEBE tener un archivo coincidente `Test.java` en `src/test/`.
2.  **Ubicación**: Los archivos de prueba deben estar en `src/test/java/com/sysacad/backend/` replicando exactamente la estructura de paquetes del código fuente.
3.  **Nombramiento**: Si la clase es `MateriaService.java`, el archivo de prueba DEBE ser `MateriaServiceTest.java`.
4.  **Contenido**: El test debe cubrir al menos el camino feliz (success path) y los casos de borde conocidos (ej. validación de cupos, errores de correlatividad).
5.  **Creación/Modificación Simultánea**: 
    - Si se crea un Service o Controller, su Test DEBE crearse en el mismo paso.
    - Si se modifica la lógica, el Test DEBE actualizarse simultáneamente para reflejar los cambios. Jamás dejes tests desactualizados.

## 📋 Ejemplos

### Estructura Correcta
```
src/main/java/com/sysacad/backend/service/
└── MateriaService.java

src/test/java/com/sysacad/backend/service/
└── MateriaServiceTest.java
```

### Estructura Incorrecta (Falta Test)
```
src/main/java/com/sysacad/backend/service/
└── MateriaService.java
(Error: Falta MateriaServiceTest.java en la carpeta de tests correspondiente)
```
