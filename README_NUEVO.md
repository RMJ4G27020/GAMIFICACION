# 🚀 Gestor de Tareas Gamificado - Versión Mejorada

## 📱 ¿Qué es esta aplicación?

Una aplicación Android moderna para gestión de tareas estudiantiles con **gamificación** y **calendario integrado**. Combina productividad con elementos motivacionales para ayudar a los estudiantes a mantener sus hábitos de estudio.

---

## ✨ Nuevas Funcionalidades Agregadas

### 🎬 **1. Animaciones de Transición Fluidas**

#### **Navegación Compose**
- **Transiciones suaves** entre pantallas (300ms)
- **Slide horizontal + fade** con efecto profesional
- **Navegación bidireccional**: forward (derecha) y backward (izquierda)
- **Sin dependencias externas**: APIs nativas de Compose

```kotlin
// Ejemplo de animación implementada
enterTransition = {
    slideInHorizontally(animationSpec = tween(300)) + fadeIn()
}
```

#### **Galería con Zoom Mejorada**
- **Double-tap animado** con transición suave (250ms)
- **Interpolador DecelerateInterpolator** para sensación natural
- **Cancelación automática** de animaciones en conflicto

### 📅 **2. Servicio de Calendario Integrado**

#### **Programación Automática**
- **Auto-calendario**: Las tareas se programan automáticamente al crearlas
- **Recordatorios inteligentes**: 1 día antes y 2 horas antes de fechas límite
- **Eventos personalizados**: Títulos con emojis para fácil identificación

#### **Sesiones de Estudio**
- **Programación rápida**: Dialog interactivo desde el Dashboard
- **Materias predefinidas**: Matemáticas, Historia, Ciencias, Literatura, Idiomas
- **Duraciones flexibles**: 30, 60, 90 o 120 minutos
- **Recordatorios**: 15 minutos antes del inicio

#### **Integración Nativa**
- **Calendar Provider**: Usa el calendario nativo de Android
- **Sin apps externas**: Funciona con cualquier app de calendario instalada
- **Permisos granulares**: Solo solicita acceso cuando es necesario

### 🎨 **3. Interfaz Mejorada**

#### **Dashboard Actualizado**
- **Sección "Sesiones de Estudio"** con estado visual del calendario
- **Indicadores de conexión**: Verde si conectado, naranja si necesita permisos
- **Botón "Programar"**: Acceso rápido a agendar sesiones

#### **Feedback Visual**
- **Estados del calendario**: Conectado ✅ / Permisos necesarios ⚠️
- **Emojis informativos**: 📚📅⚠️🎯 para mejor comprensión
- **Cards coloridas**: Verde para éxito, naranja para advertencias

---

## 🔧 Implementación Técnica

### **Arquitectura Sin Cambios**
- ✅ **Estructura preservada**: MVVM + Jetpack Compose
- ✅ **Sin dependencias nuevas**: Solo APIs nativas de Android
- ✅ **Compatibilidad**: Funciona con el código existente

### **Archivos Nuevos**
```
📁 app/src/main/java/com/example/ejercicio2/
└── 📁 calendar/
    └── 📄 CalendarService.kt    # Servicio de calendario nativo
```

### **Archivos Modificados**
```
📄 MainActivity.kt               # Animaciones de navegación + init calendario
📄 TaskManagerViewModel.kt      # Integración con calendario
📄 ZoomableImageView.kt         # Animaciones suaves de zoom
📄 DashboardScreen.kt          # Nueva sección de sesiones
📄 AndroidManifest.xml         # Permisos de calendario
```

### **Permisos Agregados**
```xml
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.WRITE_CALENDAR" />
```

---

## 🎯 Funcionalidades Principales

### **Gestión de Tareas Gamificada**
- ✅ **CRUD completo**: Crear, leer, actualizar, eliminar tareas
- ✅ **Sistema XP**: Puntos por completar tareas
- ✅ **Niveles y badges**: Progresión motivacional
- ✅ **Categorías**: 8 tipos de tareas con colores distintivos
- ✅ **Prioridades**: Alta, Media, Baja con indicadores visuales

### **Galería Interactiva**
- ✅ **Zoom avanzado**: Pinch, double-tap, drag
- ✅ **Tres imágenes temáticas**: Estudio, ejercicio, alimentación
- ✅ **Animaciones fluidas**: Transiciones suaves
- ✅ **Límites inteligentes**: Escala 1x a 4x

### **Calendario Inteligente**
- 🆕 **Programación automática** de tareas y fechas límite
- 🆕 **Sesiones de estudio** personalizables
- 🆕 **Recordatorios múltiples** (15min, 2h, 1 día antes)
- 🆕 **Integración nativa** con calendario del dispositivo

### **Navegación Moderna**
- 🆕 **Animaciones fluidas** entre pantallas
- ✅ **Bottom Navigation** intuitiva
- ✅ **FAB** para acceso rápido a galería
- ✅ **5 pantallas principales**: Dashboard, Tareas, Agregar, Perfil, Reportes

---

## 🚀 Cómo Usar las Nuevas Funciones

### **1. Activar Calendario**
1. Abrir la app por primera vez
2. Ir al **Dashboard**
3. En la sección "📅 Sesiones de Estudio":
   - Si aparece ⚠️ "Permisos necesarios" → Permitir acceso al calendario
   - Si aparece ✅ "Calendario conectado" → ¡Listo para usar!

### **2. Programar Sesión de Estudio**
1. En **Dashboard**, presionar botón **"Programar"**
2. Seleccionar **materia** (Matemáticas, Historia, etc.)
3. Elegir **duración** (30-120 minutos)
4. Presionar **"Programar"**
5. ✅ Se crea evento 1 hora después con recordatorio

### **3. Crear Tarea con Calendario**
1. Ir a **"Agregar Tarea"**
2. Completar información (título, descripción, fecha límite)
3. Presionar **"Guardar"**
4. ✅ Automáticamente se programa en calendario con recordatorios

### **4. Disfrutar Animaciones**
- **Navegar** entre pantallas → Transiciones slide + fade
- **Hacer double-tap** en galería → Zoom animado suave
- **Cambiar de pantalla** → Efectos visuales fluidos

---

## 📊 Beneficios de las Mejoras

### **Para Estudiantes**
- 🎯 **Mejor organización**: Calendario automático sin esfuerzo manual
- ⏰ **Nunca olvides**: Recordatorios inteligentes de tareas
- 📚 **Sesiones planificadas**: Estructura de estudio más efectiva
- 🎮 **Más motivación**: Animaciones fluidas mejoran la experiencia

### **Para Desarrolladores**
- 🏗️ **Código limpio**: Sin dependencias externas, APIs nativas
- 🔧 **Mantenible**: Estructura MVVM preservada
- 📱 **Compatible**: Funciona en cualquier dispositivo Android
- 🚀 **Performante**: Animaciones optimizadas, fluidas y ligeras

---

## 🛠️ Instalación y Ejecución

### **Requisitos**
- Android Studio Giraffe o superior
- JDK 11
- SDK Android 24+ (API Level 24+)
- Dispositivo/Emulador con calendario

### **Comandos**
```bash
# Clonar repositorio
git clone https://github.com/RMJ4G27020/GAMIFICACION.git

# Abrir en Android Studio y ejecutar, o usar Gradle:
./gradlew assembleDebug

# Para ejecutar en dispositivo
./gradlew installDebug
```

### **Permisos al Ejecutar**
- Al abrir la app por primera vez, **permitir acceso al calendario** para:
  - ✅ Programación automática de tareas
  - ✅ Creación de sesiones de estudio
  - ✅ Recordatorios inteligentes

---

## 🔮 Próximas Mejoras Sugeridas

- 🔔 **Notificaciones push** personalizadas
- 🌙 **Modo oscuro** automático
- 📊 **Análisis de productividad** avanzado
- 🔄 **Sincronización en la nube**
- 🏆 **Más badges y logros**
- 🤝 **Colaboración** entre estudiantes

---

## 📝 Notas Técnicas

### **Compatibilidad**
- ✅ **Android 7.0+** (API 24+)
- ✅ **Tablets y móviles**
- ✅ **Orientación portrait/landscape**

### **Rendimiento**
- ⚡ **Animaciones 60fps** optimizadas
- 🧠 **Memoria eficiente** con ViewModel
- 📱 **Tamaño compacto** ~15MB APK

### **Seguridad**
- 🔒 **Permisos granulares** solo cuando necesario
- 🛡️ **Datos locales** sin conexión requerida
- 🔐 **Sin rastreo** ni analytics externos

---

## 👥 Contribuir

¿Quieres mejorar la app? ¡Contribuciones bienvenidas!

1. **Fork** del repositorio
2. **Crear branch** feature/nueva-funcionalidad
3. **Commit** cambios con mensajes descriptivos
4. **Pull Request** con descripción detallada

---

**Desarrollado con ❤️ usando Kotlin, Jetpack Compose y APIs nativas de Android**

*Versión mejorada - Octubre 2025*