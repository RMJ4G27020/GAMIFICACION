# 🎮 GAMIFICACIÓN - Gestor Integral de Tareas para Estudiantes

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=material-design&logoColor=white)](https://m3.material.io/)

## 📖 Descripción

**GAMIFICACIÓN** es una aplicación móvil Android innovadora que transforma la gestión de tareas estudiantiles en una experiencia gamificada y motivacional. Desarrollada con **Jetpack Compose** y **Material Design 3**, combina productividad estudiantil con elementos de juego para mantener a los usuarios comprometidos y motivados.

### 🎯 Características Principales

- **🎮 Sistema de Gamificación**: XP, niveles, rachas diarias y logros
- **📋 Gestión Completa de Tareas**: Crear, editar, completar y organizar tareas
- **🎨 Categorías Temáticas**: Estudios, Ejercicio, Comida/Salud, Trabajo, Entretenimiento
- **📊 Reportes Visuales**: Estadísticas de progreso y tendencias de productividad
- **🖼️ Galería con Zoom**: ImageView interactivo con gestos de zoom funcional
- **📱 Interfaz Moderna**: Material 3 con navegación fluida y responsive

## 🚀 Tecnologías Utilizadas

### 🏗️ Arquitectura y Frameworks
- **Android SDK 36** (API Level 36)
- **Kotlin 1.9+** - Lenguaje principal
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño
- **MVVM Architecture** - ViewModel + State Management

### 📚 Librerías y Dependencias
```gradle
// Jetpack Compose BOM
implementation platform('androidx.compose:compose-bom:2024.02.00')

// Core Compose
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material3:material3'
implementation 'androidx.compose.ui:ui-tooling-preview'

// Navigation & Lifecycle
implementation 'androidx.navigation:navigation-compose:2.7.6'
implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'

// Icons Extended
implementation 'androidx.compose.material:material-icons-extended:1.5.8'

// AppCompat (para ImageView personalizado)
implementation 'androidx.appcompat:appcompat:1.6.1'
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