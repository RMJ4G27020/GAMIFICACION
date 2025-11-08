# 🎮 GAMIFICACIÓN - Gestor Integral de Tareas para Estudiantes

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://m3.material.io/)
[![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![License](https://img.shields.io/badge/License-Academic-blue?style=for-the-badge)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0-green?style=for-the-badge)](CHANGELOG.md)

## 📖 Descripción

**GAMIFICACIÓN** es una aplicación móvil Android innovadora que transforma la gestión de tareas estudiantiles en una experiencia gamificada y motivacional. Desarrollada con **Jetpack Compose** y **Material Design 3**, combina productividad estudiantil con elementos de juego para mantener a los usuarios comprometidos y motivados.

### ✨ ¿Por qué GAMIFICACIÓN?

- 🎯 **Productividad con Propósito**: Convierte tareas aburridas en desafíos emocionantes
- 🏆 **Sistema de Recompensas**: Gana XP y desbloquea badges mientras estudias
- 📈 **Seguimiento Inteligente**: Estadísticas detalladas de tu progreso académico
- 🎨 **Experiencia Visual**: UI moderna y atractiva con Material 3
- 💾 **Base de Datos Robusta**: SQLite con 9 tablas normalizadas y triggers automáticos

### 🎯 Características Principales

#### 🎮 Sistema de Gamificación Completo
- **Sistema de XP y Niveles**: Gana experiencia completando tareas
- **16 Badges Desbloqueables**: Desde "Primer Paso" hasta "Súper Estudiante"
- **Rachas Diarias**: Mantén tu racha completando tareas cada día
- **Triggers Automáticos**: La base de datos actualiza tu progreso automáticamente

#### 📋 Gestión Avanzada de Tareas
- Crear, editar, completar y eliminar tareas
- **5 Categorías**: MATHEMATICS, SCIENCE, HISTORY, STUDY, EXERCISE
- **3 Prioridades**: HIGH, MEDIUM, LOW con colores distintivos
- **3 Estados**: PENDING, IN_PROGRESS, COMPLETED
- Fechas de vencimiento con alertas visuales
- Recompensas de XP personalizables por tarea

#### 📊 Reportes y Estadísticas
- Dashboard con métricas en tiempo real
- Gráficos de progreso semanal/mensual
- Análisis de productividad por categoría
- Historial de tareas completadas
- Vista de actividad reciente

#### 🖼️ Galería con Zoom Interactivo
- ZoomableImageView personalizado
- Pinch-to-zoom gesture support
- Double-tap para zoom rápido
- Transiciones suaves entre imágenes

#### 📅 Integración con Calendario
- Sincronización automática con Google Calendar
- Creación de eventos al agregar tareas
- Gestión de permisos dinámica

## 🚀 Quick Start

### Prerrequisitos

```bash
- Android Studio Ladybug | 2024.3.1 o superior
- JDK 11 o superior
- Android SDK 36 (API Level 36)
- Emulador Android o dispositivo físico con Android 7.0+
```

### Instalación

```bash
# 1. Clonar el repositorio
git clone https://github.com/RMJ4G27020/GAMIFICACION.git
cd GAMIFICACION

# 2. Abrir en Android Studio
# File > Open > Seleccionar carpeta del proyecto

# 3. Sync Gradle
# Android Studio sincronizará automáticamente

# 4. Ejecutar
# Click en Run ▶️ o Shift+F10
```

### Configuración de Base de Datos

La base de datos se inicializa automáticamente la primera vez que ejecutas la app:

```kotlin
// MainActivity.kt - Se ejecuta automáticamente en onCreate()
private fun initializeDatabase() {
    DatabaseInitializer.initialize(this)
}
```

Para visualizar la base de datos:
- **Opción 1**: Database Inspector de Android Studio (RECOMENDADO)
- **Opción 2**: Botón "🗄️ Ver Estado de Base de Datos" en el Dashboard

## 🏗️ Arquitectura

### 🎨 MVVM Pattern

```
📁 ejercicio2/
├── 🎨 ui/
│   └── theme/           # Material 3 theming
├── 📱 screens/          # Composables de UI
│   ├── DashboardScreen.kt
│   ├── TaskListScreen.kt
│   ├── AddTaskScreen.kt
│   ├── ProfileScreen.kt
│   └── ReportsScreen.kt
├── 🧠 viewmodel/
│   └── TaskManagerViewModel.kt
├── 💾 database/
│   ├── DatabaseHelper.kt
│   └── DatabaseInitializer.kt
└── 🏠 MainActivity.kt
```

### 💾 Base de Datos (SQLite)

**9 Tablas Normalizadas (3NF)**:

| Tabla | Descripción | Registros Iniciales |
|-------|-------------|---------------------|
| `users` | Información de usuarios | 1 |
| `tasks` | Tareas del usuario | 5 ejemplos |
| `badges` | Logros disponibles | 16 badges |
| `user_badges` | Progreso de badges | 16 entradas |
| `study_sessions` | Sesiones de estudio | 0 |
| `daily_stats` | Estadísticas diarias | Dinámicas |
| `activity_log` | Log de actividades | Dinámicas |
| `app_settings` | Configuración | 4 settings |
| `sync_queue` | Cola de sincronización | 0 |

**Features de la Base de Datos**:
- ✅ 4 Triggers automáticos (update_user_on_task_complete, etc.)
- ✅ 4 Views optimizadas (user_performance, badge_progress, etc.)
- ✅ 20+ Índices estratégicos
- ✅ Foreign keys con ON DELETE CASCADE
- ✅ UUID support para sync futuro

## 🚀 Tecnologías Utilizadas

### 🏗️ Core Technologies
- **Android SDK 36** (compileSdk 36, targetSdk 36, minSdk 24)
- **Kotlin 2.0.21** - Lenguaje principal
- **Jetpack Compose BOM 2024.09.00** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño
- **SQLite 3** - Base de datos local

### 📚 Librerías Principales (Actualizadas)

```gradle
// Jetpack Compose & Material 3
implementation platform('androidx.compose:compose-bom:2024.09.00')
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material3:material3'
implementation 'androidx.compose.ui:ui-tooling-preview'

// Navigation & Lifecycle
implementation 'androidx.navigation:navigation-compose:2.8.5'
implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7'

// Icons Extended
implementation 'androidx.compose.material:material-icons-extended:1.7.5'

// UI Components
implementation 'androidx.constraintlayout:constraintlayout-compose:1.1.0'
implementation 'androidx.appcompat:appcompat:1.7.0'
implementation 'com.google.android.material:material:1.12.0'

// Fragments & Drawer
implementation 'androidx.drawerlayout:drawerlayout:1.2.0'
implementation 'androidx.fragment:fragment-ktx:1.8.5'
```

## 🏛️ Arquitectura del Proyecto

```
app/src/main/java/com/example/ejercicio2/
├── 📁 data/
│   ├── Models.kt                 # Modelos de datos (Task, UserProfile, Badge, etc.)
│   └── TaskManagerViewModel.kt   # ViewModel principal con lógica de negocio
├── 📁 screens/
│   ├── DashboardScreen.kt        # Pantalla principal con estadísticas
│   ├── AddTaskScreen.kt          # Formulario para crear tareas
│   ├── TaskListScreen.kt         # Lista y filtrado de tareas
│   ├── ProfileScreen.kt          # Perfil del usuario y logros
│   └── ReportsScreen.kt          # Reportes visuales y motivación
├── 📁 ui/theme/
│   ├── Color.kt                  # Paleta de colores gamificada
│   ├── Theme.kt                  # Tema Material 3
│   └── Type.kt                   # Tipografía
├── MainActivity.kt               # Activity principal con navegación
├── ZoomableImageView.kt          # ImageView personalizado con zoom
└── ImageZoomActivity.kt          # Activity para galería con zoom
```

## ✨ Funcionalidades Detalladas

### 🏠 Dashboard Principal
- **Barra de progreso XP** con animaciones
- **Estadísticas en tiempo real**: tareas completadas, pendientes, racha diaria
- **Vista de categorías** con iconos y colores distintivos
- **Lista de tareas recientes** y próximas a vencer
- **Acceso rápido** a todas las secciones

### ✅ Sistema de Tareas
- **Creación intuitiva** con formulario Material 3
- **Categorización**: 6 categorías predefinidas con iconos únicos
- **Prioridades**: Alta, Media, Baja con indicadores visuales
- **Fechas límite** con recordatorios
- **Estados**: Pendiente, En Progreso, Completada
- **Filtros avanzados** por categoría y estado

### 🎮 Gamificación Integrada
- **Sistema XP**: 50 puntos por tarea completada
- **Niveles**: Basados en experiencia acumulada
- **Rachas**: Contador de días consecutivos activos
- **Badges y Logros**: Sistema de recompensas visual
- **Motivación**: Mensajes dinámicos según progreso

### 📊 Reportes y Analytics
- **Gráficos de progreso** semanal y mensual
- **Estadísticas por categoría** con distribución visual
- **Análisis de productividad** y tendencias
- **Motivación personalizada** basada en rendimiento

### 🖼️ Galería con Zoom Interactivo
- **ZoomableImageView personalizado** con gestos completos
- **Pinch to Zoom**: Ampliar/reducir con pellizco
- **Double Tap**: Alternar zoom con doble toque
- **Drag & Pan**: Arrastrar imagen ampliada
- **Límites de zoom**: 1x a 4x para experiencia óptima
- **3 imágenes temáticas**: Estudios, Ejercicio, Comida

## 🛠️ Instalación y Configuración

### 📋 Prerrequisitos
- **Android Studio Arctic Fox** o superior
- **JDK 11** o superior
- **Android SDK 36** (API Level 36)
- **Emulador Android** o dispositivo físico

### ⚡ Instalación Rápida

1. **Clonar el repositorio**
```bash
git clone https://github.com/RMJ4G27020/GAMIFICACION.git
cd GAMIFICACION
```

2. **Abrir en Android Studio**
```bash
# Abrir Android Studio y seleccionar 'Open an existing project'
# Navegar hasta la carpeta clonada
```

3. **Sincronizar dependencias**
```bash
# Android Studio sincronizará automáticamente
# O manualmente: Tools > Sync Project with Gradle Files
```

4. **Compilar el proyecto**
```bash
./gradlew build
```

5. **Ejecutar en emulador/dispositivo**
```bash
./gradlew installDebug
```

### 🔧 Configuración de Desarrollo

#### Variables de Entorno
```bash
# Android SDK Path
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

#### Gradle Properties
```properties
# gradle.properties
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
```

## 📱 Uso de la Aplicación

### 🚀 Inicio Rápido

1. **Pantalla Principal**: Visualiza tu progreso y estadísticas
2. **Crear Tarea**: Usa el botón "+" para añadir nuevas tareas
3. **Explorar Categorías**: Toca cualquier categoría para ver tareas filtradas
4. **Completar Tareas**: Marca como completada para ganar XP
5. **Ver Progreso**: Navega a "Reportes" para análisis detallado
6. **Galería Zoom**: Toca el botón de foto para acceder a imágenes interactivas

### 🎯 Navegación

| Pantalla | Icono | Descripción |
|----------|-------|-------------|
| **Inicio** | 🏠 | Dashboard principal con resumen |
| **Tareas** | 📋 | Lista completa y filtros |
| **Reportes** | 📊 | Estadísticas y análisis |
| **Perfil** | 👤 | Usuario, logros y configuración |

### 🖼️ Funciones de Zoom

- **Pinch Gesture**: Pellizca con dos dedos para ampliar/reducir
- **Double Tap**: Doble toque para alternar entre zoom normal y 2x
- **Drag**: Arrastra la imagen cuando esté ampliada para navegarla
- **Reset**: Doble toque nuevamente para volver al tamaño original

## 🎨 Diseño y UI/UX

### 🌈 Paleta de Colores
```kotlin
// Colores principales del sistema de gamificación
val PrimaryBlue = Color(0xFF1976D2)        // Azul principal
val AccentOrange = Color(0xFFFF9800)       // Naranja de acento
val SuccessGreen = Color(0xFF4CAF50)       // Verde de éxito
val WarningAmber = Color(0xFFFFC107)       // Ámbar de advertencia
val ErrorRed = Color(0xFFF44336)           // Rojo de error

// Colores por categoría
val StudyBlue = Color(0xFF2196F3)          // Estudios
val ExerciseGreen = Color(0xFF4CAF50)      // Ejercicio
val FoodOrange = Color(0xFFFF9800)         // Comida
val WorkPurple = Color(0xFF9C27B0)         // Trabajo
val FunPink = Color(0xFFE91E63)            // Entretenimiento
```

### 🎭 Principios de Diseño
- **Material Design 3**: Interfaz moderna y consistente
- **Gamificación Visual**: Colores vibrantes y feedback inmediato
- **Responsive Design**: Adaptable a diferentes tamaños de pantalla
- **Accesibilidad**: Contraste adecuado y navegación por teclado
- **Microinteracciones**: Animaciones sutiles para mejor UX

## 🧪 Testing y Calidad

### ✅ Compilación
```bash
# Compilar proyecto completo
./gradlew build

# Solo compilar debug
./gradlew assembleDebug

# Verificar sintaxis Kotlin
./gradlew compileDebugKotlin
```

### 🔍 Lint y Code Quality
```bash
# Ejecutar Android Lint
./gradlew lint

# Generar reporte HTML
./gradlew lintDebug
# Reporte en: app/build/reports/lint-results-debug.html
```

### 📦 Generación de APK
```bash
# APK Debug
./gradlew assembleDebug
# Ubicación: app/build/outputs/apk/debug/app-debug.apk

# APK Release (firmado)
./gradlew assembleRelease
# Ubicación: app/build/outputs/apk/release/app-release.apk
```

## 🔮 Roadmap y Futuras Mejoras

### 🚧 En Desarrollo
- [ ] **Integración de Cámara**: Capturar fotos como prueba de completación
- [ ] **Notificaciones Push**: Recordatorios inteligentes
- [ ] **Sincronización Cloud**: Backup automático de datos
- [ ] **Modo Oscuro**: Tema alternativo para uso nocturno

### 💡 Ideas Futuras
- [ ] **Multiplayer**: Competencias con amigos
- [ ] **AI Assistant**: Sugerencias inteligentes de tareas
- [ ] **Widgets**: Acceso rápido desde pantalla de inicio
- [ ] **Integración Calendario**: Sincronizar con Google Calendar
- [ ] **Estadísticas Avanzadas**: Machine Learning para insights
- [ ] **Personalización**: Temas y avatares personalizables

## 🤝 Contribución

### 📝 Guías de Contribución
1. **Fork** el repositorio
2. **Crear** una rama para tu feature (`git checkout -b feature/amazing-feature`)
3. **Commit** tus cambios (`git commit -m 'Add amazing feature'`)
4. **Push** a la rama (`git push origin feature/amazing-feature`)
5. **Abrir** un Pull Request

### 🏗️ Estándares de Código
- **Kotlin Style Guide**: Seguir convenciones oficiales
- **Compose Best Practices**: Componentes reutilizables y performance
- **Material 3 Guidelines**: Consistency en diseño
- **Documentación**: Comentarios claros en código complejo

## 📄 Licencia

Este proyecto está bajo la **Licencia MIT** - ver el archivo [LICENSE](LICENSE) para detalles.

```
MIT License

Copyright (c) 2025 RMJ4G27020

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## 👨‍💻 Autor

**RMJ4G27020**
- GitHub: [@RMJ4G27020](https://github.com/RMJ4G27020)
- Repositorio: [GAMIFICACION](https://github.com/RMJ4G27020/GAMIFICACION)

## 🙏 Agradecimientos

- **Android Team** por Jetpack Compose
- **Material Design** por las guías de diseño
- **Kotlin Team** por el excelente lenguaje
- **Comunidad Open Source** por inspiración y recursos

---

<div align="center">

**⭐ Si te gusta este proyecto, ¡dale una estrella! ⭐**

[![GitHub stars](https://img.shields.io/github/stars/RMJ4G27020/GAMIFICACION.svg?style=social&label=Star)](https://github.com/RMJ4G27020/GAMIFICACION)
[![GitHub forks](https://img.shields.io/github/forks/RMJ4G27020/GAMIFICACION.svg?style=social&label=Fork)](https://github.com/RMJ4G27020/GAMIFICACION/fork)

</div>