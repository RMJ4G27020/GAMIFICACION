# Gestor Integral de Tareas Estudiantiles - Proyecto Final

## 📱 Descripción del Proyecto

Esta aplicación Android es un **Gestor Integral de Tareas** diseñado específicamente para estudiantes, con un enfoque en la **gamificación** y la **organización visual**. El proyecto ha evolucionado a través de múltiples iteraciones para incorporar las mejores prácticas de desarrollo Android.

## 🏗️ Arquitectura y Tecnologías

### Framework Principal
- **Hybrid Architecture**: Combina **Jetpack Compose** (pantallas originales) con **Android Fragments** (nueva navegación)
- **Navigation Drawer**: Implementado con `DrawerLayout` y `NavigationView`
- **Material Design**: Componentes modernos y consistentes

### Componentes Técnicos Clave
1. **MainActivity.kt**: Controlador principal con Navigation Drawer
2. **Fragments**: `DashboardFragment`, `GalleryFragment`, `TasksFragment`, `ProfileFragment`
3. **ZoomableImageView**: Componente personalizado para zoom interactivo
4. **ImageInfoDialogFragment**: Dialogo informativo para las imágenes

### Funcionalidades Implementadas

#### 🎯 Sistema de Gamificación
- **Puntos XP**: Sistema de experiencia por completar tareas
- **Niveles**: Progresión basada en XP acumulado
- **Badges**: Logros desbloqueables por diferentes actividades
- **Streaks**: Conteo de días consecutivos completando tareas

#### 📋 Gestión de Tareas
- **CRUD Completo**: Crear, leer, actualizar y eliminar tareas
- **Categorías**: Matemáticas, Historia, Ciencias, etc.
- **Prioridades**: Alta, Media, Baja con colores distintivos
- **Estados**: Pendiente, En Proceso, Completada
- **Fechas límite**: Con recordatorios visuales

#### 🖼️ Galería Interactiva
- **Imágenes Zoomables**: Pinch-to-zoom, double-tap, drag
- **Información Detallada**: Dialog con metadatos de cada imagen
- **Integración Fluida**: Navegación natural con el Drawer

#### 📊 Panel de Control
- **Estadísticas**: Tareas completadas, pendientes, progreso
- **Gráficos Visuales**: Indicadores de progreso animados
- **Motivación**: Mensajes dinámicos basados en el rendimiento

## 🛠️ Implementación Técnica

### Navigation Drawer con Fragments
```kotlin
// MainActivity maneja la navegación entre fragments
private fun navigateToFragment(fragment: Fragment, fragmentTag: String) {
    supportFragmentManager.beginTransaction()
        .setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right,
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        .replace(R.id.fragment_container, fragment, fragmentTag)
        .commit()
}
```

### ZoomableImageView Personalizada
- **Gestos Multi-touch**: Soporte completo para zoom y pan
- **Límites Inteligentes**: Previene zoom excesivo o fuera de bounds
- **Animaciones Suaves**: Transiciones fluidas entre estados
- **Reset Automático**: Double-tap para restablecer zoom

### Sistema de Data Models
```kotlin
data class Task(
    val id: String,
    val title: String,
    val category: TaskCategory,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueDate: LocalDate,
    val xpReward: Int
)
```

## 📁 Estructura del Proyecto

```
app/src/main/
├── java/com/example/ejercicio2/
│   ├── MainActivity.kt                 # Controlador principal con Drawer
│   ├── ZoomableImageView.kt           # Componente de imagen interactiva
│   ├── data/
│   │   └── Models.kt                  # Modelos de datos
│   ├── fragments/
│   │   ├── BaseFragment.kt            # Fragment base con funcionalidades comunes
│   │   ├── DashboardFragment.kt       # Panel de control principal
│   │   ├── GalleryFragment.kt         # Galería de imágenes zoomables
│   │   ├── TasksFragment.kt           # Gestión de tareas
│   │   ├── ProfileFragment.kt         # Perfil de usuario y estadísticas
│   │   └── ImageInfoDialogFragment.kt # Dialog con info de imágenes
│   ├── screens/ (Compose)
│   │   ├── DashboardScreen.kt         # Pantalla principal gamificada
│   │   ├── TaskListScreen.kt          # Lista de tareas
│   │   ├── AddTaskScreen.kt           # Creación de tareas
│   │   ├── ProfileScreen.kt           # Perfil y logros
│   │   └── ReportsScreen.kt           # Reportes y estadísticas
│   └── ui/theme/                      # Tema Material Design
└── res/
    ├── layout/
    │   ├── activity_main_drawer.xml   # Layout principal con Drawer
    │   ├── nav_header_main.xml        # Header del Navigation Drawer
    │   └── fragment_*.xml             # Layouts de fragments
    ├── menu/
    │   └── drawer_menu.xml            # Menú del Navigation Drawer
    ├── drawable/                      # Iconos y recursos gráficos
    ├── mipmap-*/                      # Iconos de la aplicación
    └── values/                        # Strings, colores, estilos
```

## 🎨 Diseño y UX

### Material Design 3
- **Color Scheme**: Paleta moderna con acentos naranjas
- **Typography**: Fuentes legibles y jerárquicas
- **Spacing**: Sistema de 8dp grid
- **Iconografía**: Material Icons con consistencia visual

### Navegación Intuitiva
- **Drawer Navigation**: Acceso rápido a todas las secciones
- **Bottom Navigation**: (en Compose screens) Navegación contextual
- **FAB**: Acción principal prominente
- **Breadcrumbs**: Orientación clara del usuario

### Feedback Visual
- **Animaciones**: Transiciones suaves entre estados
- **Loading States**: Indicadores de progreso
- **Success/Error States**: Feedback inmediato de acciones
- **Micro-interactions**: Detalles que mejoran la experiencia

## 🚀 Funcionalidades Avanzadas

### Gamificación Completa
- **Sistema de Logros**: 8 badges diferentes con criterios únicos
- **Progresión de Niveles**: Cálculo dinámico basado en XP
- **Motivación Contextual**: Mensajes adaptativos según el rendimiento
- **Streaks y Racha**: Incentivo para uso consistente

### Gestión Avanzada de Tareas
- **Filtrado Inteligente**: Por categoría, prioridad, estado
- **Búsqueda en Tiempo Real**: Localización rápida de tareas
- **Ordenamiento Dinámico**: Múltiples criterios de organización
- **Vistas Personalizables**: Adaptables a preferencias del usuario

### Características Técnicas Destacadas
- **Hybrid Architecture**: Aprovecha lo mejor de Compose y Views tradicionales
- **Custom Components**: ZoomableImageView con gestos avanzados
- **Responsive Design**: Adaptable a diferentes tamaños de pantalla
- **Performance Optimized**: Lazy loading y recycling eficiente

## 📊 Métricas y Logros

### Desarrollo
- **Líneas de Código**: ~2000+ líneas Kotlin
- **Componentes**: 15+ screens/fragments únicos
- **Layouts**: 10+ XML layouts responsivos
- **Recursos**: 20+ drawables y assets personalizados

### Funcionalidades
- **Gestión Completa**: CRUD de tareas con 4 estados
- **Gamificación**: 8 tipos de logros, sistema XP/niveles
- **Interactividad**: Zoom, gestos, animaciones
- **Navegación**: 2 sistemas de navegación integrados

## 🔧 Configuración y Compilación

### Dependencias Principales
```kotlin
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.activity.compose)
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.fragment.ktx)
implementation(libs.material)
implementation(libs.androidx.drawerlayout)
```

### Comandos de Compilación
```bash
# Compilar proyecto
./gradlew assembleDebug

# Limpiar proyecto
./gradlew clean

# Ejecutar tests
./gradlew test
```

## 🎓 Reflexión Educativa

### Aprendizajes Clave
1. **Arquitectura Híbrida**: Integración exitosa de paradigmas modernos y clásicos
2. **UX Centrada en el Usuario**: Diseño intuitivo con feedback constante
3. **Gamificación Efectiva**: Motivación a través de mecánicas de juego
4. **Desarrollo Iterativo**: Evolución progresiva basada en requisitos

### Mejores Prácticas Aplicadas
- **Separación de Responsabilidades**: Fragments especializados
- **Reutilización de Código**: Componentes base y utilidades
- **Performance**: Optimización de memoria y rendering
- **Accesibilidad**: Content descriptions y navegación por teclado

### Desafíos Superados
1. **Migración Compose-Fragments**: Integración sin pérdida de funcionalidad
2. **Custom Gestures**: Implementación de zoom avanzado
3. **State Management**: Consistencia entre diferentes arquitecturas
4. **Resource Management**: Optimización de drawables y layouts

## 📱 Resultado Final

La aplicación resultante es un **gestor de tareas integral** que combina:
- **Funcionalidad Completa**: Todas las características de un task manager profesional
- **Gamificación Motivacional**: Elementos de juego que incentivan el uso
- **Experiencia Visual Rica**: Interfaz moderna con interacciones avanzadas
- **Arquitectura Robusta**: Base técnica sólida para futuras expansiones

Este proyecto demuestra la capacidad de evolucionar una aplicación simple hacia un producto complejo, aplicando múltiples paradigmas de desarrollo Android de manera cohesiva y efectiva.

---

**Developed with ❤️ using Android Studio, Kotlin, Jetpack Compose, and Material Design**