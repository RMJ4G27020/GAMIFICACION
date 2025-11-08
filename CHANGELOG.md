# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-11-07

### 🎉 Added - Nuevas Características
- **Sistema de Gestión de Tareas Gamificado**
  - Dashboard con estadísticas de usuario (XP, nivel, tareas completadas, racha)
  - Creación y gestión de tareas con categorías y prioridades
  - Sistema de recompensas con XP por tarea completada
  - Lista de tareas con filtros por estado, prioridad y categoría
  - Perfil de usuario con estadísticas detalladas
  - Reportes y análisis de productividad

- **Base de Datos SQLite**
  - 9 tablas normalizadas (3NF): users, tasks, badges, user_badges, study_sessions, daily_stats, activity_log, app_settings, sync_queue
  - 4 triggers automáticos para lógica de gamificación
  - 4 vistas optimizadas para consultas complejas
  - 20+ índices estratégicos para performance
  - 16 badges predefinidos con sistema de progreso
  - Soporte para UUID para sincronización futura
  - Foreign keys con ON DELETE CASCADE

- **DatabaseHelper**
  - Singleton pattern para gestión eficiente de conexiones
  - Método `clearDatabase()` para testing
  - Método `close()` para liberar recursos
  - Método `isDatabaseCorrupted()` para verificación de integridad
  - Método `getDatabaseSize()` para monitoreo
  - PRAGMA foreign_keys enabled automáticamente

- **DatabaseInitializer**
  - Inicialización automática con usuario por defecto
  - Creación de 5 tareas de ejemplo
  - Inicialización de progreso para todos los badges
  - Método `getDatabaseInfo()` para estadísticas

- **DatabaseDebugActivity**
  - Vista en la app del estado de la base de datos
  - Estadísticas: tamaño, tablas, usuarios, tareas, badges
  - Botón para reinicializar la base de datos
  - Instrucciones para visualizar con Database Inspector

- **Galería de Imágenes con Zoom**
  - ZoomableImageView personalizado con gestos
  - Soporte para pinch-to-zoom
  - Double-tap para zoom
  - Galería con 3 imágenes de ejemplo
  - Botón flotante para acceso rápido

- **Integración con Calendario**
  - Sincronización automática de tareas con Google Calendar
  - Permisos de calendario gestionados dinámicamente
  - Creación de eventos al agregar tareas

- **Navegación y UI**
  - Bottom Navigation Bar con 5 secciones
  - Navigation Drawer con menú lateral
  - Animaciones de transición entre pantallas
  - Material 3 Design con tema personalizado
  - Dark mode support

### 🔧 Changed - Mejoras y Cambios

- **Actualización de Dependencias**
  - androidx.navigation 2.7.6 → 2.8.5
  - androidx.lifecycle 2.7.0 → 2.8.7
  - androidx.constraintlayout-compose 1.0.1 → 1.1.0
  - androidx.compose.material-icons-extended 1.5.8 → 1.7.5
  - androidx.appcompat 1.6.1 → 1.7.0
  - com.google.android.material 1.11.0 → 1.12.0
  - androidx.fragment 1.6.2 → 1.8.5

- **Optimización de Código**
  - Eliminación de imports duplicados en MainActivity
  - Reorganización de imports en orden alfabético
  - Limpieza de código muerto y comentarios obsoletos

- **ProGuard Rules**
  - Agregadas reglas específicas para SQLite
  - Reglas para Jetpack Compose y Material 3
  - Protección de ViewModels y clases de base de datos
  - Reglas para reflection y coroutines

### 🗑️ Removed - Archivos Eliminados

- **Documentación Redundante**
  - ❌ README_NUEVO.md (duplicado)
  - ❌ README_FINAL.md (duplicado)
  - ❌ README_ZOOM.md (duplicado)
  - ❌ DOCUMENTACION_ACTIVIDAD_COMPLETA.md (consolidado en Actividad_9.md)
  - ❌ DOCUMENTACION_TECNICA.md (consolidado en Actividad_9.md)
  - ❌ TECHNICAL_DOCS.md (consolidado en Actividad_9.md)
  - ❌ database/COMO_VER_LA_BD.md (consolidado)
  - ❌ database/INSTRUCCIONES_URGENTE.md (consolidado)

- **Scripts Python Redundantes**
  - ❌ database/insert_sample_data.py (duplicado de test_initializer.py)

### 🐛 Fixed - Correcciones

- Corregido foreign key error en schema.sql (badges.badge_id → badges.id)
- Eliminada importación no utilizada de NavHostController en MainActivity
- Corregidos paths relativos en scripts Python
- Corregida inicialización de base de datos en MainActivity

### 📚 Documentation - Documentación

- ✅ **Actividad_9.md** - Documentación académica completa (928 líneas)
- ✅ **DATABASE_DOCUMENTATION.md** - Referencia técnica de la base de datos
- ✅ **database/README.md** - Quick start para scripts Python
- ✅ **README.md** - Documentación principal del proyecto
- ✅ **CHANGELOG.md** - Este archivo

### 🧪 Testing - Pruebas

- Scripts Python para testing local:
  - `create_database.py` - Crea base de datos local desde schema.sql
  - `explore_database.py` - Interfaz interactiva para explorar la DB
  - `test_initializer.py` - Simula DatabaseInitializer y prueba triggers

### 🛠️ Technical Details - Detalles Técnicos

- **Tecnologías**:
  - Kotlin 2.0.21
  - Android Gradle Plugin 8.12.2
  - Jetpack Compose (BOM 2024.09.00)
  - Material 3
  - SQLite 3
  - Python 3 (para scripts de desarrollo)

- **Arquitectura**:
  - MVVM (Model-View-ViewModel)
  - Single Activity con Compose Navigation
  - Repository pattern para base de datos
  - Singleton pattern para DatabaseHelper

- **Mínimos Requerimientos**:
  - minSdk: 24 (Android 7.0 Nougat)
  - targetSdk: 36 (Android 15)
  - compileSdk: 36

## [Unreleased] - Próximas Funcionalidades

### 🚀 Planned Features

- [ ] Sistema de notificaciones push para recordatorios
- [ ] Sincronización en la nube (Firebase/Supabase)
- [ ] Estadísticas avanzadas con gráficos
- [ ] Modo offline-first con sync queue
- [ ] Compartir logros en redes sociales
- [ ] Widget para pantalla de inicio
- [ ] Modo pomodoro integrado
- [ ] Temas personalizados
- [ ] Backup y restauración de datos
- [ ] Exportar reportes en PDF

---

## Versioning

Usamos [SemVer](http://semver.org/) para versionado:

- **MAJOR** version: Cambios incompatibles en la API
- **MINOR** version: Nuevas funcionalidades compatibles con versiones anteriores
- **PATCH** version: Correcciones de bugs compatibles

## Mantainer

- **Ricardo Jiménez** - [@RMJ4G27020](https://github.com/RMJ4G27020)

## Licencia

Este proyecto es parte de un ejercicio académico.
