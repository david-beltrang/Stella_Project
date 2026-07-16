# Stella -- Study Tracker and Gamified Learning App

## Description

Stella is a Java/JavaFX desktop application that combines a Pomodoro study timer with structured programming courses (Java, Python, SQL, C++), quizzes, code exercises, a virtual store, and a penguin avatar ("Stella") that users customize with purchased items. The app uses an embedded H2 database for persistence and runs entirely locally.

Built as the final project for a Fundamentals of Software Engineering course, it applies SDLC phases including requirements, design, implementation, testing, and version control with Git/GitHub.

## Features

- **Pomodoro Timer** (`PomodoroController`, `PomodoroTimer`): countdown-based focus/break cycling with configurable durations (25, 30, 45, 60 min).
- **Structured Courses** (`CursoController`, `LeccionController`, `SeccionesService`): multi-section courses with theory, video, and practice lessons. Currently ships with seed data for Java, Python, SQL, and C++.
- **Code Exercises** (`EjercicioService`, `CodeExecutionService`): Java exercises evaluated via JShell at runtime.
- **Quizzes** (`QuizController`, `PruebaService`): multiple-choice section-end quizzes with scoring and attempt tracking.
- **Virtual Store** (`TiendaController`, `TiendaService`): spend in-app currency ("pescaditos") on cosmetic items (hats, glasses, hoodies) for the penguin avatar.
- **Penguin Avatar / Igloo** (`IgluController`, `InventarioAvatarController`, `UsuarioStellaService`): visual feedback showing character growth and igloo construction progress as study goals are met.
- **Forum Q&A** (`ForoController`, `PreguntasRespuestasForoService`): questions, answers, comments, likes/dislikes.
- **Chatbot** (`ChatbotController`, `ChatbotService`): AI assistant integrated via OpenRouter API (Mistral 7B). Requires `CHATBOT_API_KEY` env var (see Environment Variables).
- **Profile & Stats** (`PerfilController`, `UsuarioStatsService`): streak tracking, total study time, per-user statistics.

## Architecture

```
Main.java (entry point, DI wiring)
  |
  +-- Application.config.AppServices  (global singleton service registry)
  |
  +-- Application.services.*           (business logic layer)
  |     +-- DarAcceso/                  (LoginService, RegistroService)
  |     +-- dtos/                       (request/response DTOs per feature)
  |
  +-- Domain/                           (domain layer)
  |     +-- models/                     (Usuario, Curso, Leccion, Item, etc.)
  |     +-- models/*ValueObjects/       (Tipo, PrecioPescaditos, TiempoEstudio, etc.)
  |     +-- repositoriesInterfaces/     (repository contracts)
  |     +-- strategies/                 (ValidationStrategy pattern)
  |     +-- exceptions/                 (domain exceptions)
  |
  +-- Infrastructure/                   (infrastructure layer)
        +-- persistence/               (ConexionBD, H2DataBaseInitializer)
        +-- repositories/              (JDBC repository implementations)
        +-- controllers/               (JavaFX controllers, 17 classes)
        +-- ui/                        (Navegacion, ImageLoader, AyudaUI)
        +-- test_temporal/             (temporary/test code)
```

The project follows a **layered architecture** with a manual dependency injection wiring in `Main.java`. Controllers are managed through `ControllerControladores` which acts as both a registry and a controller factory for JavaFX's `FXMLLoader`. The `Domain` layer contains pure models with factory methods (`crearNueva`, `reconstruir`) and value objects; `Application` services orchestrate domain logic and translate to/from DTOs; `Infrastructure` handles persistence (JDBC), UI (JavaFX FXML), and external integrations (OpenRouter HTTP client, JShell).

## Data Model (H2, 27 tables)

The schema is defined in `src/main/resources/sql/schema.sql` and seed data in `src/main/resources/sql/data.sql`. Both are executed at startup by `H2DataBaseInitializer`.

Key tables:

| Entity | Table | Notes |
|---|---|---|
| User | `usuario` | username, email (unique), hashed? plaintext password |
| User stats | `usuario_stats` | pescaditos (coins), streak, total study time |
| Course | `curso` | title, level, category, duration |
| Enrollment | `usuario_curso` | many-to-many user-course |
| Section | `seccion` | ordered within course |
| Lesson | `leccion` | supports TYPES: TEORIA, VIDEO, PRACTICA |
| Exercise | `ejercicio` | code template, expected solution |
| User exercise attempt | `usuario_ejercicio` | submitted code, correctness flag |
| Quiz | `prueba` | typed FINAL quizzes per section |
| Question | `pregunta` | multiple choice, linked to lesson or quiz |
| Option | `opcion` | answer choice with correct flag |
| Quiz attempt | `intento` | score per user per quiz |
| Study session | `sesion_estudio` | pomodoro session with focus/break times |
| Study progress | `progreso_estudio` | daily boolean study marker |
| Gamification | `progreso_gamificacion` | points, level, streak |
| Forum post | `post` | community posts with tags |
| Forum comment | `comentario` | nested under posts |
| Forum Q&A | `pregunta_foro`, `respuesta_foro`, `comentario_foro`, `like_foro`, `dislike_foro` | separate Q&A module |
| Store item | `item` | name, description, price, image |
| Stella item | `stella_item` | maps items to penguin images |
| User item | `usuario_item` | purchase record, active flag |

Connection: `ConexionBD` (singleton, double-checked locking) wraps H2 embedded (`jdbc:h2:./stella`) via raw JDBC. No ORM.

## UI (JavaFX)

- 21 FXML views in `src/main/resources/views/` (Login, Registro, Principal, Pomodoro, PomodoroDescanso, Curso/Leccion/Quiz templates, Tienda, Foro, ChatBot, Perfil, Iglu, InventarioAvatar, SemanasPopup, DiasSemanaPopup, ResponderForo, ProductoTienda, RecuperarContra, hello-view, PomodoroTiempoFinalizado)
- Controllers in `Infrastructure.controllers` (17 classes)
- View-switching via `Navegacion.java` (custom navigation utility using `FXMLLoader` and controller factory)
- Styles in `src/main/resources/styles/` (LoginStyle.css, TiendaStyle.css)
- Images in `src/main/resources/Image/General/` and `Image/stellas/`, `Image/TiendaStella/`
- All views use FXML; no programmatic UI construction beyond FXML loading

## Build & Run

- **Build system**: Maven (pom.xml)
- **Java version**: 17 required (`maven.compiler.source`/`target` = 17)
- **JavaFX version**: 18.0.2 (windows classifier)
- **H2 version**: 2.3.232

```bash
# Compile
mvn clean compile

# Run (via JavaFX Maven plugin)
mvn javafx:run

# Package
mvn package
```

The main class is `Main.Main` (configured in `javafx-maven-plugin`).

## Environment Variables

| Variable | Required | Description |
|---|---|---|
| `CHATBOT_API_KEY` | No (chatbot disabled if unset) | OpenRouter API key for the chatbot feature. Obtain one at https://openrouter.ai/keys. If unset, the chatbot returns an informative error message instead of crashing. |

## Project Structure

```
src/
  main/
    java/
      Main/Main.java                     -- Entry point, DI wiring
      Application/
        config/AppServices.java          -- Global service registry
        services/                        -- Business logic (15 services)
        services/DarAcceso/              -- LoginService, RegistroService
        dtos/                            -- 40+ DTOs across features
      Domain/
        models/                          -- 15+ domain entities
        models/*ValueObjects/            -- Value objects (Tipo, PrecioPescaditos, etc.)
        repositoriesInterfaces/          -- Repository contracts (20 interfaces)
        strategies/                      -- ValidationStrategy interface + 2 impls
        exceptions/                      -- Domain exceptions (6 classes)
      Infrastructure/
        persistence/                     -- ConexionBD, H2DataBaseInitializer
        repositories/                    -- JDBC impls (20 classes)
        controllers/                     -- JavaFX controllers (17 classes)
        ui/                              -- Navegacion, ImageLoader, AyudaUI
        test_temporal/                   -- Temp CLI chatbot test
    resources/
      views/                             -- 21 FXML files
      sql/                               -- schema.sql, data.sql
      styles/                            -- 2 CSS files
      Image/General/                     -- Backgrounds, icons
      Image/stellas/                     -- Penguin avatar variants
      Image/TiendaStella/                -- Store item images
  test/
    java/Application/services/           -- 13 test files (JUnit 5)
```

## Testing

13 test files under `src/test/java/Application/services/`, using JUnit 5 (`junit-jupiter:5.10.0`) and Mockito (`mockito-core:5.11.0`). Tests are a mix of integration tests (use real H2 database via `H2DataBaseInitializer`) and unit tests (mocked dependencies).

**Current status:** 89 tests passing, 0 failures, 0 errors.

Test classes:

| Test File | Type | Coverage Focus |
|---|---|---|
| `ChatbotService_IntegrationTest` | Integration (H2) | API key guard, invalid key, empty question, valid response |
| `CodeExecutionService_Test` | Unit (mocked) | Code compilation/execution pipeline |
| `ForoServiceTest` | Integration (H2) | Forum CRUD operations |
| `LeccionServiceTest` | Integration (H2) | Lesson retrieval and progression |
| `ListarCursosService_Test` | Unit (mocked) | Course listing logic |
| `LoginServiceIntegrationTest` | Integration (H2) | Login validation, credential check |
| `ProgresoServiceTest` | Integration (H2) | Progress tracking |
| `PruebaService_Test` | Unit (mocked) | Quiz evaluation logic |
| `RegistroServiceIntegrationTest` | Integration (H2) | User registration, duplicate prevention |
| `SeccionesService_Test` | Unit (mocked) | Section retrieval |
| `SesionPomodoroServiceTest` | Integration (H2) | Pomodoro session persistence |
| `TiendaService_Test` | Unit (mocked) | Store item management |
| `UsuarioStatsService_Test` | Integration (H2) | User statistics aggregation |

**JaCoCo coverage (last full report):** 29% instructions, 24% branches across 79 of 166 classes. Coverage is concentrated in service-layer tests; JavaFX controllers and DAO/repository classes are untested. Coverage thresholds are not enforced.

## Repository Hygiene

- `stella.mv.db` and `stella.trace.db` are no longer tracked by Git (removed in commit 29441f0).
- `.gitignore` patterns correctly exclude `*.mv.db`, `*.trace.db`, `stella.mv.db`, and `stella.trace.db`.

## Known Issues and Code Smells

(Not exhaustive -- see audit notes for full list.)

- `EmailValidationStrategy` and `PasswordValidationStrategy` only check null/blank; no format or regex validation.
- Passwords stored in plaintext in `data.sql` seed data. The app compares passwords in plaintext (`Usuario.java:129`); no hashing is implemented. Adding hashing would require changes to `Usuario.java`, `LoginService`, `RegistroService`, and seed data.
- `ForoService.java` is deprecated (replaced by `PreguntasRespuestasForoService`).
- No JaCoCo coverage thresholds configured, despite the plugin being present in `pom.xml`.
- C++ course seed data in `data.sql` was initially incomplete (commit 0cab22f fixed quiz `curso_id` from 1 to 4). Course now has 3 sections, 12 lessons, 3 exercises, and 3 quizzes.
