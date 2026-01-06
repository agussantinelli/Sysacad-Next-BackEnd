<h1 align="center">⚙️ Sysacad Next - Backend</h1>

<div align="center">
    <a href="https://github.com/agussantinelli/Sysacad-Next-FrontEnd" target="_blank">
        <img src="https://img.shields.io/badge/🚀%20Repo%20Frontend-Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Frontend Repo Badge"/>
    </a>
    <a href="https://github.com/agussantinelli/Sysacad-Next-Backend" target="_blank">
        <img src="https://img.shields.io/badge/⚙️%20Repo%20Backend%20(Estás%20Aquí)-Java%20Spring-F80000?style=for-the-badge&logo=spring&logoColor=white" alt="Backend Repo Badge"/>
    </a>
    <a href="https://github.com/agussantinelli" target="_blank">
        <img src="https://img.shields.io/badge/👤%20Contacto-agussantinelli-000000?style=for-the-badge&logo=github&logoColor=white" alt="Contact Badge"/>
    </a>
</div>

<br>

<div align="center">
    <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Badge"/>
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5.9-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot Badge"/>
    <img src="https://img.shields.io/badge/Spring%20Security-6-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security Badge"/>
    <img src="https://img.shields.io/badge/JWT-0.11.5-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT Badge"/>
    <img src="https://img.shields.io/badge/PostgreSQL-Latest-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL Badge"/>
    <img src="https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate Badge"/>
    <img src="https://img.shields.io/badge/Apache%20Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven Badge"/>
    <img src="https://img.shields.io/badge/Project%20Lombok-Enables-BC0230?style=for-the-badge&logo=lombok&logoColor=white" alt="Lombok Badge"/>
    <img src="https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit Badge"/>
</div>

<div align="center">
    <a href="https://drive.google.com/drive/folders/1Yoln2wLucIvrbcWCbQ_bY-hZ4Z1ENIdD" target="_blank">
        <img src="https://img.shields.io/badge/📂%20Documentación%20del%20Proyecto-4285F4?style=for-the-badge&logo=googledrive&logoColor=white" alt="Docs Badge"/>
    </a>
</div>

<hr>

<h2>🎯 Objetivo</h2>

<p>Proveer una API RESTful robusta, segura y escalable que actúe como el cerebro de <strong>Sysacad Next</strong>, gestionando la lógica de negocio compleja (correlatividades, actas, inscripciones) y asegurando la integridad de los datos académicos.</p>

<h2>🧠 Arquitectura y Diseño</h2>

<p>Este backend está construido siguiendo principios de <strong>Clean Architecture</strong> y <strong>SOLID</strong>, priorizando la desacoplación y la testabilidad.</p>
<ul>
    <li><strong>Seguridad Stateless:</strong> Autenticación vía JWT (JSON Web Tokens) con Spring Security.</li>
    <li><strong>Validación Robusta:</strong> Reglas de negocio forzadas en la capa de servicio (Domain Driven Design).</li>
    <li><strong>Optimización:</strong> Consultas JPA optimizadas y uso de DTOs (Projections) para evitar el problema N+1.</li>
    <li><strong>Manejo de Errores Global:</strong> <code>@ControllerAdvice</code> para respuestas de error estandarizadas.</li>
</ul>

<hr>

<h2>🗂️ Modelo de Dominio</h2>

<table>
    <thead>
        <tr>
            <th>Área</th>
            <th>Entidades (Tablas)</th>
            <th>Responsabilidad</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><strong>🏢 Infraestructura</strong></td>
            <td><code>facultades_regionales</code>, <code>salones</code></td>
            <td>Sedes y aulas físicas. Raíz de la jerarquía (todo depende de una facultad).</td>
        </tr>
        <tr>
            <td><strong>👤 Actores y Comunicación</strong></td>
            <td><code>usuarios</code>, <code>sanciones</code>, <code>avisos</code></td>
            <td>Gestión de perfiles, roles, registro disciplinario y <strong>cartelera de novedades</strong>.</td>
        </tr>
        <tr>
            <td><strong>📜 Jerarquía Académica</strong></td>
            <td><code>carreras</code>, <code>planes_de_estudios</code></td>
            <td>Definición estructural. Cadena de dependencia estricta: <strong>Facultad → Carrera → Plan</strong>.</td>
        </tr>
        <tr>
            <td><strong>📚 Curricular</strong></td>
            <td><code>materias</code>, <code>plan_materias</code>, <code>correlativas</code></td>
            <td>Catálogo de asignaturas (fuerte), su contextualización en planes (año/carga) y sistema de correlatividades.</td>
        </tr>
        <tr>
            <td><strong>📅 Gestión de Cursada</strong></td>
            <td><code>comisiones</code>, <code>materias_comisiones</code>, <code>profesores_comisiones</code>, <code>asignaciones_materia</code>, <code>horarios_cursado</code></td>
            <td>Oferta operativa. Soporta relación N:M, asignación de roles docentes y <strong>agenda semanal de cursado</strong>.</td>
        </tr>
        <tr>
            <td><strong>📝 Ciclo del Alumno</strong></td>
            <td><code>estudios_usuario</code>, <code>inscripciones</code>, <code>calificaciones</code></td>
            <td>Trazabilidad total: Matriculación en carrera, inscripción a cursada/examen y registro de historia académica.</td>
        </tr>
    </tbody>
</table>
<hr>

<h2>🛠️ Stack Tecnológico</h2>

<table>
    <thead>
        <tr>
            <th>Capa / Área</th>
            <th>Tecnología</th>
            <th>Versión / Detalle</th>
            <th>Uso Principal</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><strong>Core</strong></td>
            <td>Java (JDK)</td>
            <td><strong>21</strong> (LTS)</td>
            <td>Lenguaje base, Records, Pattern Matching.</td>
        </tr>
        <tr>
            <td><strong>Framework</strong></td>
            <td>Spring Boot</td>
            <td><strong>3.5.9</strong></td>
            <td>Base del Backend, DI, Auto-configuration.</td>
        </tr>
        <tr>
            <td><strong>Seguridad</strong></td>
            <td>Spring Security + JWT</td>
            <td><strong>6.x</strong> / JJWT <strong>0.11.5</strong></td>
            <td>Autenticación Stateless, Filtros JWT, CORS, BCrypt.</td>
        </tr>
        <tr>
            <td><strong>Base de Datos</strong></td>
            <td>PostgreSQL Driver</td>
            <td>Latest</td>
            <td>Motor de base de datos relacional.</td>
        </tr>
        <tr>
            <td><strong>ORM</strong></td>
            <td>Spring Data JPA</td>
            <td>Hibernate Core</td>
            <td>Abstracción de datos, Repositorios, Entidades.</td>
        </tr>
        <tr>
            <td><strong>Build Tool</strong></td>
            <td>Apache Maven</td>
            <td>3.8+</td>
            <td>Gestión de dependencias y ciclo de vida.</td>
        </tr>
        <tr>
            <td><strong>Testing</strong></td>
            <td>JUnit 5 & Mockito</td>
            <td>(Starter Test)</td>
            <td>Pruebas unitarias y de integración.</td>
        </tr>
        <tr>
            <td><strong>Herramientas</strong></td>
            <td>Lombok</td>
            <td>Latest</td>
            <td>Reducción de código (Data, Builder, Slf4j).</td>
        </tr>
        <tr>
            <td><strong>Dev Experience</strong></td>
            <td>Spring Boot DevTools</td>
            <td>-</td>
            <td>Reinicio rápido y LiveReload en desarrollo.</td>
        </tr>
    </tbody>
</table>

<hr>

<h2>📦 Estructura del Proyecto</h2>

<pre><code>Sysacad-Next-BackEnd/
├── .mvn/                                      # Archivos del Wrapper de Maven
├── src/
│   ├── main/
│   │   ├── java/com/sysacad/backend/
│   │   │   ├── config/                        # Configuración global (ej. Seeder)
│   │   │   ├── controller/                    # Controladores REST (Entry Points)
│   │   │   ├── modelo/                        # Entidades del dominio (JPA)
│   │   │   │   └── enums/                     # Enumeraciones
│   │   │   ├── repository/                    # Repositorios (Acceso a Datos)
│   │   │   ├── service/                       # Lógica de Negocio (Service Layer)
│   │   │   └── BackendApplication.java        # Punto de entrada de la aplicación
│   │   └── resources/
│   │       ├── static/                        # Archivos estáticos públicos
│   │       ├── templates/                     # Plantillas de vista
│   │       ├── application.properties         # Configuración de Spring Boot
│   │       └── application-secret.properties  # Credenciales sensibles (No versionado)
│   └── test/                                  # Tests unitarios y de integración
├── target/                                    # Salida de compilación (ignorar)
├── .gitignore                                 # Archivos ignorados por Git
├── HELP.md                                    # Documentación de ayuda de Spring Boot
├── mvnw                                       # Script Maven Wrapper (Linux/Mac)
├── mvnw.cmd                                   # Script Maven Wrapper (Windows)
├── pom.xml                                    # Definición de dependencias y build (Maven)
├── README.md                                  # Documentación del proyecto
└── sysacad-next.sql                           # Script SQL de base de datos
</code></pre>

<hr>

<h2>💻 Setup Local</h2>

<p>Requisitos: JDK 21+, Maven 3.8+ y PostgreSQL.</p>

<h3>🚀 Ejecución</h3>

1.  **Clonar el repositorio.**
2.  **Configurar Base de Datos**: Asegúrate de tener PostgreSQL corriendo en el puerto `5432`. La base de datos `sysacad_db` se creará automáticamente si no existe (gracias a `update` en properties), pero se recomienda crearla manualmente si falla.
3.  **Compilar y Correr**:
    ```bash
    mvn spring-boot:run
    ```
4.  **Puerto**: El servidor iniciará en el puerto **8081** (`http://localhost:8081`).
5.  **CORS**: Configurado para aceptar peticiones desde `http://localhost:4200` (Frontend Angular).

<h3>🌱 Base de Datos y Seeding Automático</h3>

El sistema cuenta con un `DbSeeder` (`src/main/java/com/sysacad/backend/config/DbSeeder.java`) que se ejecuta al iniciar la aplicación. Realiza dos comprobaciones principales:

1.  **Facultades:** Si la tabla `facultades_regionales` está vacía, carga toda la estructura académica (Facultad Rosario, Carreras, **Planes de Estudio Reales y Oficiales de la UTN Facultad Regional Rosario**, Materias y Correlatividades).
2.  **Usuarios:** Si la tabla `usuarios` está vacía, crea los usuarios de prueba por defecto.

<h3>🔐 Usuarios de Prueba</h3>

Para facilitar el desarrollo y testing, se crean los siguientes usuarios por defecto (Password para todos: `123456`):

| Rol | Legajo / User | Email | Password |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `1` | `admin@sysacad.com` | `123456` |
| **PROFESOR** | `51111` | `nic@sysacad.com` | `123456` |
| **ESTUDIANTE** | `55555` | `agus@sysacad.com` | `123456` |

---

<h2 align="center">🚀 Catálogo Completo de Endpoints</h2>
<p align="center"><em>Generado a partir del análisis del código fuente en `com.sysacad.backend.controller`</em></p>

<h3>🔐 Autenticación</h3>
<table>
  <thead><tr><th>Método</th><th>Recurso</th><th>Acción</th></tr></thead>
  <tbody>
    <tr><td><code>POST</code></td><td><code>/api/auth/login</code></td><td>Inicia sesión y genera token JWT.</td></tr>
  </tbody>
</table>

<h3>👥 Usuarios</h3>
<table>
  <thead><tr><th>Método</th><th>Recurso</th><th>Acción</th></tr></thead>
  <tbody>
    <tr><td><code>POST</code></td><td><code>/api/usuarios</code></td><td>Registra un nuevo usuario (Solo Admin).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/usuarios</code></td><td>Lista todos los usuarios, opcionalmente filtrados por Rol (Solo Admin).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/usuarios/{id}</code></td><td>Obtiene perfil de usuario por ID (Admin, Profesor).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/usuarios/buscar/legajo/{legajo}</code></td><td>Busca usuario por legajo (Admin, Profesor).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/usuarios/materia/{idMateria}</code></td><td>Lista docentes asignados a una materia (Admin, Profesor).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/usuarios/alumnos/materia/{idMateria}</code></td><td>Lista alumnos inscriptos en una materia (Admin, Profesor).</td></tr>
    <tr><td><code>DELETE</code></td><td><code>/api/usuarios/{id}</code></td><td>Elimina un usuario (Solo Admin).</td></tr>
  </tbody>
</table>

<h3>🏫 Facultades y Carreras</h3>
<table>
  <thead><tr><th>Método</th><th>Recurso</th><th>Acción</th></tr></thead>
  <tbody>
    <tr><td><code>POST</code></td><td><code>/api/facultades</code></td><td>Crea una nueva facultad (Solo Admin).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/facultades</code></td><td>Lista todas las facultades.</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/facultades/{id}</code></td><td>Obtiene detalle de una facultad.</td></tr>
    <tr><td><code>POST</code></td><td><code>/api/carreras</code></td><td>Registra una nueva carrera (Solo Admin).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/carreras/facultad/{idFacultad}</code></td><td>Lista carreras de una facultad específica.</td></tr>
    <tr><td><code>POST</code></td><td><code>/api/carreras/planes</code></td><td>Crea un nuevo plan de estudio (Solo Admin).</td></tr>
    <tr><td><code>POST</code></td><td><code>/api/carreras/planes/materias</code></td><td>Agrega materia a un plan (Solo Admin).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/carreras/{idCarrera}/planes/vigentes</code></td><td>Lista planes vigentes de una carrera.</td></tr>
    <tr><td><code>POST</code></td><td><code>/api/planes</code></td><td>Crea un nuevo plan de estudio (Solo Admin).</td></tr>
    <tr><td><code>POST</code></td><td><code>/api/planes/materias</code></td><td>Agrega materia a un plan de estudio (Solo Admin).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/planes/vigentes/{idCarrera}</code></td><td>Lista planes vigentes (Endpoint alternativo/duplicado lógica).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/planes/carrera/{idCarrera}</code></td><td>Lista todos los planes de una carrera.</td></tr>
  </tbody>
</table>

<h3>📚 Materias y Comisiones</h3>
<table>
  <thead><tr><th>Método</th><th>Recurso</th><th>Acción</th></tr></thead>
  <tbody>
    <tr><td><code>POST</code></td><td><code>/api/materias</code></td><td>Crea una nueva materia (Solo Admin).</td></tr>
    <tr><td><code>PUT</code></td><td><code>/api/materias/{id}</code></td><td>Actualiza datos de una materia (Admin, Profesor).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/materias</code></td><td>Lista materias, opcional filtro por Tipo.</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/materias/{id}</code></td><td>Obtiene detalle de una materia.</td></tr>
    <tr><td><code>DELETE</code></td><td><code>/api/materias/{id}</code></td><td>Elimina una materia (Solo Admin).</td></tr>
    <tr><td><code>POST</code></td><td><code>/api/comisiones</code></td><td>Crea una nueva comisión (Solo Admin).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/comisiones</code></td><td>Lista comisiones por año (Admin, Profesor).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/comisiones/{id}</code></td><td>Obtiene detalle de comisión (Solo Admin).</td></tr>
    <tr><td><code>POST</code></td><td><code>/api/comisiones/{id}/profesores</code></td><td>Asigna profesor a una comisión (Admin, Profesor).</td></tr>
  </tbody>
</table>

<h3>📝 Inscripciones y Notas</h3>
<table>
  <thead><tr><th>Método</th><th>Recurso</th><th>Acción</th></tr></thead>
  <tbody>
    <tr><td><code>POST</code></td><td><code>/api/inscripciones</code></td><td>Inscribe alumno a cursada/final (Admin, Estudiante).</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/inscripciones/validar-correlatividad</code></td><td>Verifica si alumno puede cursar materia.</td></tr>
    <tr><td><code>GET</code></td><td><code>/api/inscripciones/alumno/{idAlumno}</code></td><td>Obtiene historial académico del alumno.</td></tr>
    <tr><td><code>POST</code></td><td><code>/api/inscripciones/notas</code></td><td>Carga nota a una inscripción (Admin, Profesor).</td></tr>
  </tbody>
</table>

<h2>🤝 Contribuciones</h2>

<p>Para proponer cambios en la lógica de negocio o nuevos endpoints:</p>
<ol>
    <li>Crea una rama siguiendo la convención: <code>feature/nombre-funcionalidad</code> o <code>fix/nombre-bug</code>.</li>
    <li>Asegúrate de que los tests pasen (<code>mvn test</code>).</li>
    <li>Abre un PR hacia `develop`.</li>
</ol>

<p align="center">Desarrollado con ❤️ y mucho ☕ para la comunidad académica.</p>
