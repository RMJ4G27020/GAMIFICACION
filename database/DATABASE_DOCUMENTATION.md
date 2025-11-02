# Documentación de Base de Datos - Gestor de Tareas Gamificado

## 📊 Información General

**Nombre:** task_gamification.db  
**Tipo:** SQLite 3  
**Versión:** 1.0  
**Plataforma:** Android (Compatible con API 24+)  
**ORM:** SQLite nativo con DatabaseHelper

---

## 🏗️ Arquitectura de la Base de Datos

### Principios de Diseño

1. **Normalización:** Base de datos normalizada hasta 3NF para evitar redundancia
2. **Integridad Referencial:** Foreign keys con ON DELETE CASCADE para mantener consistencia
3. **Índices Estratégicos:** Índices en columnas frecuentemente consultadas
4. **Triggers Automáticos:** Lógica de negocio automatizada para XP, niveles y estadísticas
5. **Vistas Optimizadas:** Views para consultas frecuentes y reportes

---

## 📋 Esquema de Tablas

### 1. `users` - Información del Usuario

Almacena el perfil completo del usuario con estadísticas gamificadas.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | INTEGER PK | ID único autoincremental |
| `uuid` | TEXT UNIQUE | UUID único para sincronización |
| `name` | TEXT | Nombre del usuario |
| `email` | TEXT UNIQUE | Email (opcional) |
| `avatar_url` | TEXT | URL del avatar |
| `current_xp` | INTEGER | Experiencia actual (≥0) |
| `level` | INTEGER | Nivel actual (≥1) |
| `current_streak` | INTEGER | Racha actual en días |
| `longest_streak` | INTEGER | Racha más larga alcanzada |
| `tasks_completed` | INTEGER | Total de tareas completadas |
| `total_xp_earned` | INTEGER | XP total acumulado histórico |
| `created_at` | TIMESTAMP | Fecha de creación |
| `updated_at` | TIMESTAMP | Última actualización |
| `last_login` | TIMESTAMP | Último acceso |
| `preferences_json` | TEXT | Preferencias en formato JSON |
| `is_active` | INTEGER | Estado activo (0/1) |

**Índices:**
- `idx_users_uuid` en `uuid`
- `idx_users_email` en `email`
- `idx_users_level` en `level`

**Constraints:**
- `current_xp >= 0`
- `level >= 1`
- `is_active IN (0, 1)`

---

### 2. `tasks` - Tareas del Usuario

Gestión completa de tareas con categorización y gamificación.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | INTEGER PK | ID único autoincremental |
| `uuid` | TEXT UNIQUE | UUID para sincronización |
| `user_id` | INTEGER FK | Referencia a `users(id)` |
| `title` | TEXT | Título de la tarea |
| `description` | TEXT | Descripción detallada |
| `category` | TEXT | Categoría (STUDY, MATHEMATICS, etc.) |
| `priority` | TEXT | Prioridad (LOW, MEDIUM, HIGH) |
| `status` | TEXT | Estado (PENDING, IN_PROGRESS, COMPLETED, OVERDUE) |
| `due_date` | DATE | Fecha de vencimiento |
| `xp_reward` | INTEGER | XP otorgado al completar |
| `image_proof_path` | TEXT | Ruta de imagen de evidencia |
| `calendar_event_id` | INTEGER | ID del evento en calendario nativo |
| `created_at` | TIMESTAMP | Fecha de creación |
| `updated_at` | TIMESTAMP | Última actualización |
| `completed_at` | TIMESTAMP | Fecha de completado |

**Categorías Válidas:**
- `STUDY` - Estudio general
- `MATHEMATICS` - Matemáticas
- `HISTORY` - Historia
- `SCIENCE` - Ciencias
- `EXERCISE` - Ejercicio físico
- `SOCIAL` - Actividades sociales
- `WORK` - Trabajo
- `PERSONAL` - Personal

**Índices:**
- `idx_tasks_uuid` en `uuid`
- `idx_tasks_user_id` en `user_id`
- `idx_tasks_status` en `status`
- `idx_tasks_due_date` en `due_date`
- `idx_tasks_category` en `category`
- `idx_tasks_priority` en `priority`
- `idx_tasks_search` en `(user_id, category, status)`

---

### 3. `badges` - Insignias/Logros

Catálogo de badges desbloqueables en el sistema.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | INTEGER PK | ID único |
| `badge_key` | TEXT UNIQUE | Clave única (FIRST_TASK, STREAK_7, etc.) |
| `name` | TEXT | Nombre del badge |
| `description` | TEXT | Descripción del logro |
| `icon_name` | TEXT | Nombre del ícono |
| `requirement_type` | TEXT | Tipo de requisito |
| `requirement_value` | INTEGER | Valor necesario para desbloquear |
| `xp_bonus` | INTEGER | XP bonus al desbloquear |
| `is_active` | INTEGER | Badge activo (0/1) |
| `created_at` | TIMESTAMP | Fecha de creación |

**Tipos de Requisitos:**
- `TASK_COUNT` - Número de tareas completadas
- `STREAK` - Días consecutivos
- `XP_MILESTONE` - Hito de XP total
- `CATEGORY_MASTER` - Tareas en categoría específica
- `SPECIAL` - Logros especiales

**Badges Predefinidos:**
- 🌟 `FIRST_TASK` - Primera tarea (1 tarea, +50 XP)
- 🏆 `TASK_10` - 10 tareas (+100 XP)
- 🥇 `TASK_50` - 50 tareas (+250 XP)
- 👑 `TASK_100` - 100 tareas (+500 XP)
- 🔥 `STREAK_3` - 3 días consecutivos (+75 XP)
- 🔥 `STREAK_7` - 7 días consecutivos (+150 XP)
- 🔥 `STREAK_30` - 30 días consecutivos (+500 XP)
- ⭐ `XP_1000` - 1,000 XP (+100 XP bonus)
- ⭐ `XP_5000` - 5,000 XP (+300 XP bonus)
- ⭐ `XP_10000` - 10,000 XP (+750 XP bonus)

---

### 4. `user_badges` - Progreso de Badges por Usuario

Relación muchos-a-muchos entre usuarios y badges con progreso.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | INTEGER PK | ID único |
| `user_id` | INTEGER FK | Referencia a `users(id)` |
| `badge_id` | INTEGER FK | Referencia a `badges(id)` |
| `unlocked_at` | TIMESTAMP | Fecha de desbloqueo |
| `progress` | INTEGER | Progreso actual hacia el badge |
| `is_unlocked` | INTEGER | Desbloqueado (0/1) |

**Constraint:** `UNIQUE(user_id, badge_id)` - Un usuario no puede tener duplicados

---

### 5. `study_sessions` - Sesiones de Estudio

Programación y seguimiento de sesiones de estudio.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | INTEGER PK | ID único |
| `uuid` | TEXT UNIQUE | UUID para sincronización |
| `user_id` | INTEGER FK | Referencia a `users(id)` |
| `subject` | TEXT | Materia/tema |
| `description` | TEXT | Descripción de la sesión |
| `scheduled_date` | TIMESTAMP | Fecha y hora programada |
| `duration_minutes` | INTEGER | Duración en minutos |
| `calendar_event_id` | INTEGER | ID en calendario nativo |
| `status` | TEXT | Estado de la sesión |
| `xp_earned` | INTEGER | XP ganado al completar |
| `actual_duration_minutes` | INTEGER | Duración real |
| `notes` | TEXT | Notas de la sesión |
| `created_at` | TIMESTAMP | Fecha de creación |
| `updated_at` | TIMESTAMP | Última actualización |
| `completed_at` | TIMESTAMP | Fecha de completado |

**Estados:**
- `SCHEDULED` - Programada
- `IN_PROGRESS` - En progreso
- `COMPLETED` - Completada
- `CANCELLED` - Cancelada
- `MISSED` - Perdida

---

### 6. `daily_stats` - Estadísticas Diarias

Métricas diarias por usuario para análisis y gráficos.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | INTEGER PK | ID único |
| `user_id` | INTEGER FK | Referencia a `users(id)` |
| `stat_date` | DATE | Fecha de las estadísticas |
| `tasks_completed` | INTEGER | Tareas completadas ese día |
| `xp_earned` | INTEGER | XP ganado ese día |
| `study_minutes` | INTEGER | Minutos de estudio |
| `streak_active` | INTEGER | Racha activa (0/1) |
| `created_at` | TIMESTAMP | Fecha de creación |

**Constraint:** `UNIQUE(user_id, stat_date)` - Una entrada por día por usuario

---

### 7. `activity_log` - Registro de Actividades

Auditoría completa de todas las acciones del usuario.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | INTEGER PK | ID único |
| `user_id` | INTEGER FK | Referencia a `users(id)` |
| `activity_type` | TEXT | Tipo de actividad |
| `entity_type` | TEXT | Tipo de entidad (task, badge, etc.) |
| `entity_id` | INTEGER | ID de la entidad |
| `description` | TEXT | Descripción legible |
| `xp_change` | INTEGER | Cambio en XP |
| `metadata_json` | TEXT | Metadata en JSON |
| `created_at` | TIMESTAMP | Fecha de actividad |

**Tipos de Actividad:**
- `TASK_CREATED` - Tarea creada
- `TASK_COMPLETED` - Tarea completada
- `TASK_DELETED` - Tarea eliminada
- `BADGE_UNLOCKED` - Badge desbloqueado
- `LEVEL_UP` - Subida de nivel
- `SESSION_COMPLETED` - Sesión completada
- `STREAK_MILESTONE` - Hito de racha
- `XP_EARNED` - XP ganado

---

### 8. `app_settings` - Configuración Global

Configuraciones y preferencias de la aplicación.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | INTEGER PK | ID único |
| `setting_key` | TEXT UNIQUE | Clave de configuración |
| `setting_value` | TEXT | Valor de configuración |
| `setting_type` | TEXT | Tipo (STRING, INTEGER, BOOLEAN, JSON) |
| `description` | TEXT | Descripción de la configuración |
| `updated_at` | TIMESTAMP | Última actualización |

**Configuraciones Predefinidas:**
- `db_version` - Versión del esquema
- `xp_per_level` - XP por nivel (100)
- `default_task_xp` - XP por defecto (10)
- `streak_reset_hours` - Horas para perder racha (24)
- `enable_notifications` - Notificaciones habilitadas
- `enable_calendar_sync` - Sincronización con calendario
- `theme_mode` - Tema (light/dark/auto)

---

### 9. `sync_queue` - Cola de Sincronización

Cola para sincronización con servicios cloud (futuro).

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | INTEGER PK | ID único |
| `user_id` | INTEGER FK | Referencia a `users(id)` |
| `entity_type` | TEXT | Tipo de entidad |
| `entity_id` | INTEGER | ID de la entidad |
| `operation` | TEXT | Operación (CREATE, UPDATE, DELETE) |
| `data_json` | TEXT | Datos en JSON |
| `sync_status` | TEXT | Estado (PENDING, IN_PROGRESS, COMPLETED, FAILED) |
| `retry_count` | INTEGER | Intentos de reintento |
| `error_message` | TEXT | Mensaje de error |
| `created_at` | TIMESTAMP | Fecha de creación |
| `synced_at` | TIMESTAMP | Fecha de sincronización |

---

## 🔄 Triggers Automáticos

### 1. `update_users_timestamp`
Actualiza `updated_at` cuando se modifica un usuario.

### 2. `update_tasks_timestamp`
Actualiza `updated_at` cuando se modifica una tarea.

### 3. `task_completed_stats`
**Trigger Principal de Gamificación**

Al completar una tarea (`status = 'COMPLETED'`):
1. ✅ Incrementa `tasks_completed` del usuario
2. ⭐ Suma `xp_reward` al `current_xp` y `total_xp_earned`
3. 📊 Actualiza/crea entrada en `daily_stats`
4. 📝 Registra actividad en `activity_log`

### 4. `check_level_up`
Calcula y actualiza el nivel automáticamente cuando cambia XP:
- **Fórmula:** `level = (current_xp / 100) + 1`
- Registra evento `LEVEL_UP` en `activity_log`

---

## 📊 Vistas (Views)

### 1. `v_user_summary` - Resumen del Usuario
Información completa del usuario con estadísticas calculadas.

**Columnas:**
- Datos básicos del usuario
- `badges_unlocked` - Total de badges desbloqueados
- `pending_tasks` - Tareas pendientes
- `overdue_tasks` - Tareas vencidas
- `xp_last_week` - XP ganado en los últimos 7 días

### 2. `v_tasks_enriched` - Tareas Enriquecidas
Tareas con información adicional calculada.

**Columnas adicionales:**
- `is_overdue` - Indica si está vencida (0/1)
- `is_today` - Indica si vence hoy (0/1)
- `days_until_due` - Días hasta vencimiento

### 3. `v_badge_progress` - Progreso de Badges
Progreso detallado de cada usuario hacia cada badge.

**Columnas:**
- Información completa del badge
- `progress_percentage` - Porcentaje de progreso (0-100)

### 4. `v_weekly_stats` - Estadísticas Semanales
Métricas agregadas de la semana actual.

**Columnas:**
- `tasks_this_week` - Tareas completadas esta semana
- `xp_this_week` - XP ganado esta semana
- `study_minutes_this_week` - Minutos de estudio
- `avg_tasks_per_day` - Promedio diario
- `days_active` - Días con actividad

---

## 🔍 Consultas Frecuentes (SQL Queries)

### Obtener resumen completo del usuario
```sql
SELECT * FROM v_user_summary WHERE id = ?;
```

### Tareas pendientes de hoy
```sql
SELECT * FROM v_tasks_enriched 
WHERE user_id = ? 
  AND status = 'PENDING' 
  AND is_today = 1
ORDER BY priority DESC, due_date ASC;
```

### Badges desbloqueados recientemente
```sql
SELECT b.*, ub.unlocked_at 
FROM user_badges ub
JOIN badges b ON ub.badge_id = b.id
WHERE ub.user_id = ? AND ub.is_unlocked = 1
ORDER BY ub.unlocked_at DESC
LIMIT 5;
```

### Actividad reciente
```sql
SELECT * FROM activity_log
WHERE user_id = ?
ORDER BY created_at DESC
LIMIT 20;
```

### Estadísticas de últimos 30 días
```sql
SELECT 
    stat_date,
    tasks_completed,
    xp_earned,
    study_minutes
FROM daily_stats
WHERE user_id = ? 
  AND stat_date >= DATE('now', '-30 days')
ORDER BY stat_date ASC;
```

---

## 🚀 Performance y Optimización

### Índices Estratégicos
- ✅ Índices en todas las foreign keys
- ✅ Índices en columnas de búsqueda frecuente (status, date, category)
- ✅ Índices compuestos para consultas complejas
- ✅ UNIQUE indexes para garantizar integridad

### Consideraciones de Performance
1. **Foreign Keys:** Habilitadas con `PRAGMA foreign_keys=ON`
2. **Triggers:** Eficientes con lógica mínima
3. **Views:** Pre-calculadas para consultas complejas
4. **Paginación:** Usar `LIMIT` y `OFFSET` para grandes datasets
5. **Transacciones:** Agrupar operaciones relacionadas

### Ejemplo de Transacción
```kotlin
db.beginTransaction()
try {
    // Múltiples operaciones
    db.setTransactionSuccessful()
} finally {
    db.endTransaction()
}
```

---

## 🔒 Seguridad

### Prevención de SQL Injection
- ✅ Usar parámetros preparados (`?`)
- ✅ Nunca concatenar SQL con strings de usuario
- ✅ Validar entradas antes de insertar

### Ejemplo Seguro
```kotlin
val query = "SELECT * FROM tasks WHERE user_id = ?"
db.rawQuery(query, arrayOf(userId.toString()))
```

---

## 📦 Backup y Migración

### Backup de Base de Datos
```kotlin
val currentDB = context.getDatabasePath("task_gamification.db")
val backupDB = File(context.getExternalFilesDir(null), "backup_${System.currentTimeMillis()}.db")
currentDB.copyTo(backupDB, overwrite = true)
```

### Restaurar desde Backup
```kotlin
val backupDB = File(backupPath)
val currentDB = context.getDatabasePath("task_gamification.db")
backupDB.copyTo(currentDB, overwrite = true)
```

### Migración de Versiones
Implementar en `onUpgrade()` de `DatabaseHelper`:
```kotlin
override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    when (oldVersion) {
        1 -> {
            // Migración de v1 a v2
            db.execSQL("ALTER TABLE users ADD COLUMN new_field TEXT")
        }
    }
}
```

---

## 🧪 Testing

### Limpieza de Base de Datos (Testing)
```kotlin
val dbHelper = DatabaseHelper.getInstance(context)
dbHelper.clearDatabase()
```

### Datos de Prueba
```kotlin
fun insertTestUser(): Long {
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
        put("uuid", UUID.randomUUID().toString())
        put("name", "Test User")
        put("email", "test@example.com")
    }
    return db.insert("users", null, values)
}
```

---

## 📈 Roadmap de Mejoras

### Versión 1.1 (Planeada)
- [ ] Tabla `categories_custom` para categorías personalizadas
- [ ] Tabla `task_attachments` para múltiples archivos
- [ ] Tabla `user_friends` para características sociales
- [ ] Tabla `challenges` para desafíos entre usuarios

### Versión 2.0 (Futuro)
- [ ] Soporte para SQLCipher (encriptación)
- [ ] Sincronización con Firebase/Supabase
- [ ] Room Database migration
- [ ] GraphQL API support

---

## 📞 Contacto y Soporte

**Desarrollador:** Ricardo Jiménez  
**Repositorio:** https://github.com/RMJ4G27020/GAMIFICACION  
**Versión:** 1.0  
**Fecha:** Noviembre 2025

---

## 📝 Changelog

### v1.0 (Noviembre 2025)
- ✅ Esquema inicial completo
- ✅ 9 tablas principales
- ✅ 4 vistas optimizadas
- ✅ Triggers automáticos para gamificación
- ✅ Badges predefinidos
- ✅ Sistema de sincronización
- ✅ Documentación completa
