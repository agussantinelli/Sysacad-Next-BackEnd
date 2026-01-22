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
        <img src="https://img.shields.io/badge/📂%20Drive%20Documentación-4285F4?style=for-the-badge&logo=googledrive&logoColor=white" alt="Drive Docs Badge"/>
    </a>
    <br>
    <a href="docs/endpoints_catalog.md" target="_blank">
        <img src="https://img.shields.io/badge/📄%20Catálogo%20Endpoints-4285F4?style=for-the-badge&logo=markdown&logoColor=white" alt="Endpoints Badge"/>
    </a>
    <a href="docs/dtos_catalog.md" target="_blank">
        <img src="https://img.shields.io/badge/📄%20Catálogo%20DTOs-4285F4?style=for-the-badge&logo=markdown&logoColor=white" alt="DTOs Badge"/>
    </a>
    <a href="docs/enums_catalog.md" target="_blank">
        <img src="https://img.shields.io/badge/📄%20Catálogo%20Enums-4285F4?style=for-the-badge&logo=markdown&logoColor=white" alt="Enums Badge"/>
    </a>
    <a href="docs/Consideraciones.md" target="_blank">
        <img src="https://img.shields.io/badge/📄%20Consideraciones%20Negocio-FF8800?style=for-the-badge&logo=markdown&logoColor=white" alt="Consideraciones Badge"/>
    </a>
</div>

<hr>

<hr>

<h2>🎯 Objetivo</h2>

<p>Proveer una API RESTful robusta, segura y escalable que actúe como el cerebro de <strong>Sysacad Next</strong>, gestionando la lógica de negocio compleja (correlatividades, actas, inscripciones, auditoría) y asegurando la integridad de los datos académicos.</p>

> **Nota:** Este proyecto ha sido desarrollado mediante **ingeniería inversa**, analizando el sitio y flujos originales para replicar y mejorar la lógica de negocio. Para más detalles sobre las reglas deducidas, consulta las [Consideraciones del Negocio](docs/Consideraciones.md).

<h2>🧠 Arquitectura y Diseño</h2>

<p>Este backend está construido siguiendo principios de <strong>Clean Architecture</strong> y <strong>SOLID</strong>, priorizando la desacoplación y la testabilidad.</p>
<ul>
    <li><strong>Seguridad Stateless:</strong> Autenticación vía JWT (JSON Web Tokens) con Spring Security (v6+).</li>
    <li><strong>Validación Robusta:</strong> Reglas de negocio forzadas en la capa de servicio (Domain Driven Design).</li>
    <li><strong>Optimización (N+1):</strong> Uso estratégico de <code>JOIN FETCH</code> en JPQL y DTOs projections.</li>
    <li><strong>Manejo de Errores Global:</strong> <code>@ControllerAdvice</code> para respuestas JSON estandarizadas en excepciones.</li>
</ul>

<hr>

<h2>🗂️ Modelo de Dominio</h2>

<p>La estructura de base de datos refleja la complejidad de una institución académica real:</p>

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
            <td>Gestión de perfiles, roles (Admin/Estudiante/Profesor), registro disciplinario y cartelera.</td>
        </tr>
        <tr>
            <td><strong>📜 Jerarquía Académica</strong></td>
            <td><code>carreras</code>, <code>planes_de_estudios</code></td>
            <td>Definición estructural. Cadena de dependencia estricta: <strong>Facultad → Carrera → Plan</strong>.</td>
        </tr>
        <tr>
            <td><strong>📚 Curricular</strong></td>
            <td><code>materias</code>, <code>plan_materias</code>, <code>correlativas</code></td>
            <td>Asignaturas con tipología (Básica/Específica/Compartida) y <strong>nueva gestión de Modalidad (Presencial/Virtual/Mixto)</strong>.</td>
        </tr>
        <tr>
            <td><strong>📅 Gestión de Cursada</strong></td>
            <td><code>comisiones</code>, <code>materias_comisiones</code>, <code>profesores_comisiones</code>, <code>asignaciones_materia</code>, <code>horarios_cursado</code></td>
            <td>Oferta operativa anual/cuatrimestral. Soporta relación N:M, asignación de roles docentes y <strong>agenda semanal de cursado</strong>.</td>
        </tr>
        <tr>
            <td><strong>🗓️ Exámenes Finales</strong></td>
            <td><code>mesas_examen</code>, <code>detalle_mesa_examen</code>, <code>inscripciones_examen</code></td>
            <td>Gestión de turnos de examen (periodos), cronograma de fechas por materia e inscripciones de alumnos a mesas.</td>
        </tr>
        <tr>
            <td><strong>📝 Ciclo del Alumno</strong></td>
            <td><code>matriculaciones</code>, <code>inscripciones</code>, <code>calificaciones</code></td>
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
            <td><strong>Herramientas</strong></td>
            <td>Lombok</td>
            <td>Latest</td>
            <td>Reducción de código (Data, Builder, Slf4j).</td>
        </tr>
    </tbody>
</table>

<hr>

<h2>📦 Estructura del Proyecto</h2>

<pre><code>Sysacad-Next-BackEnd/
├── .mvn/                                      # Archivos del Wrapper de Maven
├── docs/                                      # Documentación técnica adicional
├── src/
│   ├── main/
│   │   ├── java/com/sysacad/backend/
│   │   │   ├── config/                        # Configuración global (ej. Seeder)
│   │   │   ├── controller/                    # Controladores REST (Entry Points)
│   │   │   ├── dto/                           # Data Transfer Objects (DTOs)
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
├── sysacad-next.sql                           # Script SQL de base de datos
└── uploads/                                   # Archivos subidos (Avatares, etc.)
</code></pre>

<hr>

<h2>💻 Setup Local</h2>

<p>Requisitos: JDK 21+, Maven 3.8+ y PostgreSQL.</p>

<h3>🚀 Ejecución</h3>

1.  **Clonar el repositorio.**
2.  **Configurar Base de Datos**: Asegúrate de tener PostgreSQL corriendo en el puerto `5432`. El `application.properties` intentará crear la DB `sysacad_db` si no existe.
3.  **Compilar y Correr**:
    ```bash
    mvn spring-boot:run
    ```
4.  **Puerto**: El servidor iniciará en el puerto **8080** (`http://localhost:8080`).
5.  **CORS**: Configurado para aceptar peticiones desde `http://localhost:4200` (Frontend Angular).

<h3>🌱 Base de Datos y Seeding Automático</h3>

El sistema cuenta con un `DbSeeder` (`src/main/java/com/sysacad/backend/config/DbSeeder.java`) que pobla la base de datos automáticamente al inicio si detecta tablas vacías.
*   **Carga Estructural:** Crea la UTN Facultad Regional Rosario, carreras (ISI, IM, IQ, IE, IC) y la estructura de materias real.
*   **Simulación de Cursada y Exámenes:** Genera comisiones en distintos turnos, asigna docentes, define horarios, inscribe alumnos, carga notas y **crea mesas de examen en Febrero, Julio y Diciembre con alumnos inscriptos**.
*   **Usuarios:** Crea una población diversa de usuarios (Admin, Profesores, Estudiantes) para pruebas.

<h3>🔐 Usuarios de Prueba Generados</h3>

| Rol | Legajo | Nombre | Email | Password |
| :--- | :--- | :--- | :--- | :--- |
| **ADMIN** | `1` | Homero Simpson | `admin@sysacad.com` | `123456` |
| **PROFESOR** | `51111` | Nicolas Cabello | `nic@sysacad.com` | `123456` |
| **PROFESOR** | `52222` | Laura Gomez | `laura@sysacad.com` | `123456` |
| **PROFESOR** | `53333` | Roberto Diaz | `roberto@sysacad.com` | `123456` |
| **PROFESOR** | `54444` | Ana Martinez | `ana@sysacad.com` | `123456` |
| **PROFESOR** | `55551` | Sandra Civiero | `sandra@sysacad.com` | `123456` |
| **PROFESOR** | `55552` | Cristian Milone | `cristian@sysacad.com` | `123456` |
| **ESTUDIANTE** | `55555` | Agustin Santinelli | `agus@sysacad.com` | `123456` |
| **ESTUDIANTE** | `56666` | Maria Rodriguez | `maria@sysacad.com` | `123456` |
| **ESTUDIANTE** | `57777` | Juan Perez | `juan@sysacad.com` | `123456` |
| **ESTUDIANTE** | `58888` | Sofia Lopez | `sofia@sysacad.com` | `123456` |
| **ESTUDIANTE** | `59999` | Miguel Torres | `miguel@sysacad.com` | `123456` |
| **ESTUDIANTE** | `60001` | Lucia Fernandez | `lucia@sysacad.com` | `123456` |
| **ESTUDIANTE** | `60002` | Carlos Tevez | `carlos@sysacad.com` | `123456` |
| **ESTUDIANTE** | `60003` | Martin Palermo | `martin@sysacad.com` | `123456` |
| **ESTUDIANTE** | `60004` | Flavia Avara | `flavia@sysacad.com` | `123456` |

---

<h2 align="center">📚 Documentación de API</h2>

<p align="center">Para ver el detalle estructura de los objetos de request y response, consulta:</p>

<div align="center">
    <a href="docs/endpoints_catalog.md" target="_blank">
        <img src="https://img.shields.io/badge/📄%20Catálogo%20Endpoints-4285F4?style=for-the-badge&logo=markdown&logoColor=white" alt="Endpoints Badge"/>
    </a>
    <a href="docs/dtos_catalog.md" target="_blank">
        <img src="https://img.shields.io/badge/📄%20Catálogo%20DTOs-4285F4?style=for-the-badge&logo=markdown&logoColor=white" alt="DTOs Badge"/>
    </a>
    <a href="docs/enums_catalog.md" target="_blank">
        <img src="https://img.shields.io/badge/📄%20Catálogo%20Enums-4285F4?style=for-the-badge&logo=markdown&logoColor=white" alt="Enums Badge"/>
    </a>
    <a href="docs/Consideraciones.md" target="_blank">
        <img src="https://img.shields.io/badge/📄%20Consideraciones%20Negocio-FF8800?style=for-the-badge&logo=markdown&logoColor=white" alt="Consideraciones Badge"/>
    </a>
</div>

<h3>Resumen de Endpoints Principales</h3>

| Recurso | Métodos | Descripción Breve |
| :--- | :--- | :--- |
| **/auth** | `POST` | Login y obtención de Token JWT. |
| **/usuarios** | `POST`, `GET`, `DELETE` | Gestión completa de usuarios (Admin). Búsqueda por legajo. |
| **/facultades** | `POST`, `GET` | Gestión de facultades regionales. |
| **/carreras** | `POST`, `GET` | Carreras y Planes de Estudio asociados. |
| **/planes** | `POST`, `GET` | Planes de estudio independientes. |
| **/materias** | `POST`, `GET`, `PUT` | ABM de materias, incluyendo correlatividades y <strong>Modalidad</strong>. |
| **/comisiones** | `POST`, `GET`, `PUT` | Comisiones anuales, asignación de docentes y horarios. |
| **/inscripciones** | `POST`, `GET` | Inscripción a cursada/finales y consulta de historia académica. |
| **/avisos** | `POST`, `GET` | Cartelera de novedades (Admin publica, todos leen). |
| **/salones** | `POST`, `GET` | Gestión de aulas físicas y asignación a facultades. |
| **/sanciones** | `POST`, `GET` | Registro disciplinario de estudiantes. |
| **/horarios** | `POST`, `GET`, `DELETE` | Gestión de agenda semanal por comisión y materia. |
| **/alumnos** | `POST`, `GET` | Matriculación en carreras y consulta de plan de estudio personal. |
| **/mesas** | `POST`, `GET` | Gestión de Turnos de Examen y cronograma de fechas. |
| **/inscripciones-examen** | `POST`, `GET`, `DELETE` | Inscripción específica a finales y consulta de inscripciones. |

<hr>

<p align="center">Desarrollado con ❤️ y mucho 🧉 para la comunidad académica.</p>
