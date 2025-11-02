# 🗄️ Base de Datos SQLite - task_gamification.db

## ✅ ¡ARCHIVO .DB CREADO EXITOSAMENTE!

📁 **Ubicación:** `c:\Users\ricoj\AndroidStudioProjects\ejercicio2\database\task_gamification.db`

---

## 📊 Contenido de la Base de Datos

### Tablas (9):
1. **users** - 1 usuario (Ricardo Jiménez)
2. **tasks** - 8 tareas (5 pendientes, 3 completadas)
3. **badges** - 15 badges predefinidos
4. **user_badges** - Progreso de badges
5. **study_sessions** - 3 sesiones de estudio
6. **daily_stats** - 7 días de estadísticas
7. **activity_log** - 4 actividades registradas
8. **app_settings** - 9 configuraciones
9. **sync_queue** - Cola de sincronización

### Vistas (4):
- `v_user_summary` - Resumen completo del usuario
- `v_tasks_enriched` - Tareas con información adicional
- `v_badge_progress` - Progreso de badges
- `v_weekly_stats` - Estadísticas semanales

### Triggers (5):
- Actualización automática de timestamps
- Cálculo automático de XP y nivel
- Registro de actividades

---

## 🔧 Scripts Disponibles

### 1. `create_database.py`
Crea la base de datos desde `schema.sql`

```bash
python database/create_database.py
```

### 2. `insert_sample_data.py`
Inserta datos de ejemplo (usuario, tareas, etc.)

```bash
python database/insert_sample_data.py
```

### 3. `explore_database.py`
Explorador interactivo con menú (requiere tabulate)

```bash
# Instalar dependencia
pip install tabulate

# Ejecutar explorador
python database/explore_database.py
```

---

## 👀 Cómo Ver la Base de Datos

### Opción 1: DB Browser for SQLite (Recomendado) ⭐

1. **Descarga:** https://sqlitebrowser.org/dl/
2. **Instala** el programa
3. **Abre:** `File` → `Open Database`
4. **Selecciona:** `task_gamification.db`
5. **Explora:**
   - Pestaña "Browse Data" - Ver tablas y datos
   - Pestaña "Execute SQL" - Ejecutar queries
   - Pestaña "Database Structure" - Ver schema

### Opción 2: VS Code Extension

1. **Instala la extensión:** "SQLite Viewer" o "SQLite"
2. **Click derecho** en `task_gamification.db`
3. **Selecciona:** "Open Database"
4. Explora las tablas directamente en VS Code

### Opción 3: Línea de Comandos (si tienes SQLite)

```bash
# Abrir base de datos
sqlite3 database/task_gamification.db

# Comandos útiles:
.tables                     # Ver todas las tablas
.schema users               # Ver estructura de tabla
SELECT * FROM users;        # Ver datos
.exit                       # Salir
```

---

## 📝 Queries SQL Útiles

### Ver Usuario
```sql
SELECT id, name, email, current_xp, level, current_streak, tasks_completed 
FROM users;
```

### Ver Tareas Pendientes
```sql
SELECT id, title, category, priority, due_date, xp_reward 
FROM tasks 
WHERE status = 'PENDING'
ORDER BY due_date;
```

### Ver Tareas Completadas
```sql
SELECT title, category, completed_at, xp_reward 
FROM tasks 
WHERE status = 'COMPLETED'
ORDER BY completed_at DESC;
```

### Ver Todos los Badges
```sql
SELECT badge_key, name, description, requirement_value, xp_bonus 
FROM badges
ORDER BY requirement_type, requirement_value;
```

### Ver Progreso de Badges del Usuario
```sql
SELECT 
    b.name,
    ub.progress,
    b.requirement_value,
    CAST(ub.progress AS REAL) / b.requirement_value * 100 as porcentaje,
    CASE WHEN ub.is_unlocked = 1 THEN '✅' ELSE '⏳' END as estado
FROM user_badges ub
JOIN badges b ON ub.badge_id = b.id
WHERE ub.user_id = 1
ORDER BY b.requirement_type, b.requirement_value;
```

### Ver Sesiones de Estudio
```sql
SELECT subject, scheduled_date, duration_minutes, status, xp_earned
FROM study_sessions
ORDER BY scheduled_date DESC;
```

### Ver Estadísticas Semanales
```sql
SELECT 
    stat_date,
    tasks_completed,
    xp_earned,
    study_minutes,
    CASE WHEN streak_active = 1 THEN '🔥' ELSE '❌' END as racha
FROM daily_stats
ORDER BY stat_date DESC;
```

### Ver Actividad Reciente
```sql
SELECT 
    activity_type,
    description,
    xp_change,
    datetime(created_at) as fecha
FROM activity_log
ORDER BY created_at DESC
LIMIT 10;
```

### Resumen Completo del Usuario (Vista)
```sql
SELECT * FROM v_user_summary;
```

### Tareas Enriquecidas (Vista)
```sql
SELECT 
    title,
    category,
    status,
    due_date,
    CASE 
        WHEN is_overdue = 1 THEN '⚠️ VENCIDA'
        WHEN is_today = 1 THEN '📅 HOY'
        ELSE '✅ OK'
    END as urgencia,
    days_until_due as dias_restantes
FROM v_tasks_enriched
ORDER BY days_until_due;
```

---

## 🔄 Modificar Datos

### Agregar Nueva Tarea
```sql
INSERT INTO tasks (uuid, user_id, title, description, category, priority, status, due_date, xp_reward)
VALUES (
    '12345678-1234-1234-1234-123456789012',
    1,
    'Mi nueva tarea',
    'Descripción de la tarea',
    'STUDY',
    'HIGH',
    'PENDING',
    '2025-11-05',
    25
);
```

### Completar una Tarea
```sql
UPDATE tasks 
SET 
    status = 'COMPLETED',
    completed_at = datetime('now')
WHERE id = 1;

-- Esto automáticamente:
-- ✅ Actualiza XP del usuario
-- ✅ Incrementa contador de tareas
-- ✅ Registra en daily_stats
-- ✅ Crea entrada en activity_log
-- ✅ Calcula nivel automáticamente
```

### Actualizar XP del Usuario
```sql
UPDATE users 
SET current_xp = current_xp + 50
WHERE id = 1;

-- El trigger automáticamente calcula el nuevo nivel
```

### Desbloquear un Badge
```sql
UPDATE user_badges 
SET 
    is_unlocked = 1,
    unlocked_at = datetime('now')
WHERE user_id = 1 AND badge_id = 1;
```

---

## 📊 Estadísticas de la Base de Datos

```sql
-- Tamaño de cada tabla
SELECT 
    name,
    (SELECT COUNT(*) FROM sqlite_master WHERE type='index' AND tbl_name=m.name) as indices,
    (SELECT COUNT(*) FROM pragma_table_info(m.name)) as columnas
FROM sqlite_master m
WHERE type='table' AND name NOT LIKE 'sqlite_%'
ORDER BY name;

-- Integridad referencial
PRAGMA foreign_key_check;

-- Lista de índices
SELECT name, tbl_name FROM sqlite_master WHERE type='index' AND name NOT LIKE 'sqlite_%';

-- Lista de triggers
SELECT name, tbl_name FROM sqlite_master WHERE type='trigger';
```

---

## 🧪 Testing de Triggers

### Test 1: Completar Tarea (debe actualizar XP automáticamente)
```sql
-- Ver XP antes
SELECT current_xp, tasks_completed FROM users WHERE id = 1;

-- Completar tarea
UPDATE tasks SET status = 'COMPLETED', completed_at = datetime('now') WHERE id = 1;

-- Ver XP después (debe haber aumentado)
SELECT current_xp, tasks_completed FROM users WHERE id = 1;

-- Ver registro en activity_log
SELECT * FROM activity_log ORDER BY created_at DESC LIMIT 1;
```

### Test 2: Level Up (debe calcular nivel automáticamente)
```sql
-- Dar mucho XP
UPDATE users SET current_xp = 250 WHERE id = 1;

-- Ver nivel (debe ser 3 porque 250/100 + 1 = 3)
SELECT name, current_xp, level FROM users WHERE id = 1;

-- Ver registro de level up
SELECT * FROM activity_log WHERE activity_type = 'LEVEL_UP' ORDER BY created_at DESC LIMIT 1;
```

---

## 🔒 Backup y Restauración

### Crear Backup
```bash
# Copiar archivo
copy database\task_gamification.db database\backup_task_gamification.db

# O con Python
python -c "import shutil; shutil.copy('database/task_gamification.db', 'database/backup.db')"
```

### Restaurar Backup
```bash
copy database\backup_task_gamification.db database\task_gamification.db
```

### Exportar a SQL
```bash
sqlite3 database/task_gamification.db .dump > database/backup.sql
```

### Importar desde SQL
```bash
sqlite3 database/task_gamification_new.db < database/backup.sql
```

---

## 📦 Integración con la App Android

Esta base de datos es **compatible** con la app Android. Para usarla:

1. **Copia** el archivo `task_gamification.db`
2. **Móntala** en el emulador:
   ```bash
   adb push task_gamification.db /data/data/com.example.ejercicio2/databases/
   ```
3. **Reinicia** la app

O mejor aún, usa `DatabaseHelper.kt` que crea la misma estructura automáticamente.

---

## 🎨 Herramientas Adicionales

### SQLite Studio
- Website: https://sqlitestudio.pl/
- Gratis, multiplataforma
- Interfaz más avanzada

### Navicat for SQLite
- Website: https://www.navicat.com/
- Profesional (pago)
- Muchas características

---

## 📚 Documentación Adicional

- `DATABASE_DOCUMENTATION.md` - Documentación técnica completa
- `schema.sql` - Schema SQL fuente
- `COMO_VER_LA_BD.md` - Guía de visualización

---

## 🎉 ¡La base de datos está lista!

✅ 9 tablas creadas
✅ 15 badges predefinidos
✅ 1 usuario de ejemplo con 8 tareas
✅ Triggers funcionando
✅ Vistas optimizadas
✅ Datos de ejemplo insertados

**Tamaño actual:** ~204 KB

---

**Creado:** Noviembre 1, 2025  
**Versión:** 1.0  
**Desarrollador:** Ricardo Jiménez
