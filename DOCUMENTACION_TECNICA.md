# 🧑‍💻 Documentación Técnica del Proyecto — Gestor de Tareas Gamificado

Esta guía técnica explica de forma clara la arquitectura, los componentes y los procesos del proyecto para que cualquier persona nueva pueda comprender qué es cada cosa y para qué sirve.

## 1) Resumen del Proyecto

- Tipo: Aplicación Android nativa, módulo único (app)
- Paradigma UI: Jetpack Compose (Material 3) + Navigation Compose
- Arquitectura: MVVM (Model–View–ViewModel)
- Funcionalidades clave: gestión de tareas, perfil gamificado (XP, niveles, insignias), reportes, galería con zoom (ImageView personalizado)

## 2) Stack Tecnológico y Versiones

- Lenguaje: Kotlin (libs.versions.toml → kotlin = 2.0.21)
- Android Gradle Plugin: 8.12.2
- SDKs: compileSdk = 36, targetSdk = 36, minSdk = 24
- JVM Target: 11
- UI: Jetpack Compose (BOM 2024.09.00), Material3
- Navegación: androidx.navigation:navigation-compose:2.7.6
- ViewModel Compose: androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0
- Otros: AppCompat, Material Components, DrawerLayout, Fragment KTX (compatibilidad con recursos de Drawer y Fragments)

Los plugins y dependencias están declarados en `gradle/libs.versions.toml` y referenciados desde `app/build.gradle.kts`.

## 3) Estructura del Repositorio

```
.
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/                    # Catálogo de versiones y wrapper
│   └── libs.versions.toml
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/example/ejercicio2/
        │   │   ├── MainActivity.kt                 # Punto de entrada (Compose Navigation)
        │   │   ├── ImageZoomActivity.kt            # Galería independiente con zoom
        │   │   ├── ZoomableImageView.kt            # ImageView personalizado para gestos (pinch/double-tap/drag)
        │   │   ├── data/Models.kt                  # Modelos de dominio (Task, User, enums)
        │   │   ├── viewmodel/TaskManagerViewModel.kt  # Lógica de negocio/estado (MVVM)
        │   │   └── screens/                        # Pantallas Compose
        │   │       ├── DashboardScreen.kt
        │   │       ├── TaskListScreen.kt
        │   │       ├── AddTaskScreen.kt
        │   │       ├── ProfileScreen.kt
        │   │       └── ReportsScreen.kt
        │   └── res/
        │       ├── drawable/                       # Íconos, vectores e imágenes (img_task1/2/3)
        │       ├── layout/                         # activity_main_drawer.xml, nav_header_main.xml
        │       ├── menu/                           # drawer_menu.xml
        │       ├── anim/                           # slide_in_right.xml, slide_out_left.xml
        │       └── values/                         # strings.xml, colors.xml, dimens.xml, themes.xml
        ├── test/java/...                           # Tests unitarios (ExampleUnitTest.kt)
        └── androidTest/java/...                    # Tests instrumentados (ExampleInstrumentedTest.kt)
```

## 4) Arquitectura y Flujo de Datos

- Patrón: MVVM con navegación por Compose.
- Origen de verdad: `TaskManagerViewModel` expone estado (listas de tareas, usuario) que consumen las screens.
- Render: Pantallas Compose (View) leen estado y emiten acciones (events) hacia el ViewModel.
- Navegación: `MainActivity` configura el grafo con Navigation Compose y un BottomNavigation para moverse entre screens.
- Extras: Se mantienen recursos de Navigation Drawer con Fragments para compatibilidad/legacy, pero la versión final usa Compose puro.

### Componentes principales

- Modelos (`data/Models.kt`):
  - `User`: currentXP, level, badges, currentStreak, tasksCompleted.
  - `Task`: id, title, description, category, priority, dueDate, status, xpReward.
  - Enums: `TaskCategory`, `TaskPriority`, `TaskStatus`.

- ViewModel (`viewmodel/TaskManagerViewModel.kt`):
  - `addTask(task)`: agrega una tarea a la lista.
  - `completeTask(task)`: marca como completada, suma XP y contabiliza progreso.
  - `deleteTask(taskId)`: elimina por identificador.
  - Selectores: `getPendingTasks()`, `getCompletedTasks()`, `getTasksByCategory()`.

- Navegación/UI:
  - `MainActivity.kt`: hospeda `NavHost` Compose, BottomNavigation y rutas.
  - Screens: Dashboard, Task List, Add Task, Profile, Reports.
  - Galería: `ImageZoomActivity` + `ZoomableImageView` (no Compose) para zoom avanzado.

## 5) Descripción de Pantallas

- DashboardScreen: Resumen del perfil (XP/Level/Streak), tareas destacadas, acciones rápidas.
- TaskListScreen: Lista de tareas con filtros por estado/categoría/prioridad; acciones completar/eliminar.
- AddTaskScreen: Formulario de alta; calcula `xpReward` según prioridad y captura `dueDate`.
- ProfileScreen: Métricas del usuario (XP, nivel, tareas completadas, racha) y progreso visual.
- ReportsScreen: Estadísticas agregadas; barras/indicadores; uso de `HorizontalDivider`.
- ImageZoomActivity: Muestra tres imágenes temáticas (estudio, ejercicio, comida) con zoom y arrastre.

## 6) Recursos y Configuración (res/)

- drawable/: íconos (ic_*), vectores img_task1/2/3, fondos (nav_header_background), avatar.
- anim/: transiciones `slide_in_right.xml`, `slide_out_left.xml`.
- layout/: `activity_main_drawer.xml`, `nav_header_main.xml` (legado Navigation Drawer).
- menu/: `drawer_menu.xml` (opciones del cajón legadas).
- values/: `strings.xml` (textos), `colors.xml`, `themes.xml` (Material 3), `dimens.xml`.

## 7) Build, Ejecución y Entorno

Requisitos:
- Android Studio (Giraffe o superior recomendado)
- JDK 11
- SDK Android 36 instalado

Comandos (PowerShell, en la raíz del proyecto):

```powershell
# Limpieza y build
./gradlew clean ; ./gradlew assembleDebug

# Pruebas unitarias
./gradlew test

# Pruebas instrumentadas (requiere dispositivo/emulador)
./gradlew connectedAndroidTest
```

Ejecución:
- Desde Android Studio: Run ▶ Selecciona un emulador/dispositivo.
- El artifact debug se genera en `app/build/outputs/apk/debug/`.

## 8) Estándares de Código y Buenas Prácticas

- Kotlin idiomático, null-safety, funciones puras donde aplique.
- Compose: estados inmutables desde ViewModel; evitar lógica pesada en Composables.
- Nombres consistentes en modelos (`xpReward`, `currentXP`, `currentStreak`).
- Convenciones de commits: Conventional Commits (ej. `feat:`, `fix:`, `docs:`).
- Dependencias: centralizadas en `libs.versions.toml`.

## 9) Pruebas

- Unit tests: `src/test/java/.../ExampleUnitTest.kt` (base para ampliar lógica de ViewModel y selectores).
- Instrumented tests: `src/androidTest/java/.../ExampleInstrumentedTest.kt`.
- Recomendado: agregar tests para cálculos de XP/levels y filtros de tareas.

## 10) Guía de Contribución

1. Crear rama feature: `feature/<breve-descripcion>`
2. Implementar cambios, actualizar docs si corresponde.
3. Ejecutar build y tests localmente.
4. Commit con mensaje claro (Conventional Commits).
5. Pull Request a `main` con descripción y capturas si aplica.

## 11) Solución de Problemas (FAQ)

- CRLF/LF warnings al hacer git add: normales en Windows; no afectan la build.
- Error de credenciales Git: instala/activa Git Credential Manager o usa PAT HTTPS.
- Fallos de Preview Compose: invalida cachés y reinicia, o usa build Gradle.
- SDK no encontrado: asegúrate de tener API 36 instalada y `local.properties` configurado.

## 12) Seguridad y Privacidad

- Sin acceso a red ni almacenamiento sensible por defecto.
- Datos en memoria volatil gestionados por ViewModel.

## 13) Internacionalización

- Textos centralizados en `res/values/strings.xml` para facilitar i18n.

## 14) Roadmap sugerido

- Persistencia con Room (tareas y perfil) + repositorios.
- Sincronización opcional/cloud y login.
- Tests de UI con Espresso/Compose Testing.
- Drawer en Compose (ModalNavigationDrawer) alineado con navegación actual.

---

Si necesitas un mapa rápido: `MainActivity` (navegación), `TaskManagerViewModel` (estado/lógica), `Models.kt` (dominio), screens en `screens/`, y galería en `ImageZoomActivity`/`ZoomableImageView`.