# 🗄️ Cómo Encontrar y Ver la Base de Datos SQLite

## 📍 Ubicación del Archivo .db

La base de datos se crea automáticamente cuando ejecutas la app por primera vez.

### Ubicación en el Dispositivo/Emulador:
```
/data/data/com.example.ejercicio2/databases/task_gamification.db
```

### Ver la Ruta en el Logcat:
Cuando ejecutes la app, verás en el Logcat de Android Studio:
```
✅ Base de datos inicializada correctamente
📍 Ruta: /data/data/com.example.ejercicio2/databases/task_gamification.db
📦 Tamaño: XX.XX KB
🔢 Versión: 1
```

---

## 🔍 Métodos para Ver la Base de Datos

### Método 1: Database Inspector de Android Studio (RECOMENDADO) ⭐

1. **Ejecuta la app** en un emulador o dispositivo (con depuración habilitada)
2. **Abre Database Inspector:**
   - Ve a: `View` → `Tool Windows` → `App Inspection`
   - O usa el atajo: `Ctrl+Shift+A` y busca "Database Inspector"
3. **Selecciona tu app** en el dropdown
4. **Explora las tablas:**
   - `users` - Información del usuario
   - `tasks` - Tareas
   - `badges` - Insignias
   - `user_badges` - Progreso de logros
   - Y más...
5. **Ejecuta queries SQL** en tiempo real
6. **Modifica datos** para pruebas

**Ventajas:**
- ✅ No necesitas extraer el archivo
- ✅ Ver y editar en tiempo real
- ✅ Ejecutar queries SQL
- ✅ Ver cambios mientras usas la app

---

### Método 2: Device File Explorer (Extraer el archivo)

1. **Abre Device File Explorer:**
   - `View` → `Tool Windows` → `Device File Explorer`
   - O en la barra inferior de Android Studio
2. **Navega a la ruta:**
   ```
   /data/data/com.example.ejercicio2/databases/
   ```
3. **Localiza el archivo:**
   - `task_gamification.db` (archivo principal)
   - `task_gamification.db-shm` (memoria compartida)
   - `task_gamification.db-wal` (write-ahead log)
4. **Extrae el archivo:**
   - Click derecho en `task_gamification.db`
   - Selecciona `Save As...`
   - Guarda en tu computadora
5. **Abre con DB Browser:**
   - Descarga [DB Browser for SQLite](https://sqlitebrowser.org/)
   - Abre el archivo `.db` extraído

**Ventajas:**
- ✅ Puedes hacer backup del archivo
- ✅ Abrir con herramientas externas
- ✅ Compartir con otros desarrolladores

---

### Método 3: ADB Shell (Línea de Comandos)

```bash
# 1. Conectar al dispositivo
adb shell

# 2. Navegar a la base de datos
cd /data/data/com.example.ejercicio2/databases/

# 3. Listar archivos
ls -la

# 4. Abrir SQLite
sqlite3 task_gamification.db

# 5. Comandos útiles dentro de SQLite:
.tables                    # Ver todas las tablas
.schema users              # Ver estructura de tabla
SELECT * FROM users;       # Consultar datos
.exit                      # Salir
```

**Extraer archivo con ADB:**
```bash
# Extraer del dispositivo a tu computadora
adb pull /data/data/com.example.ejercicio2/databases/task_gamification.db ./task_gamification.db

# Subir archivo modificado al dispositivo
adb push ./task_gamification.db /data/data/com.example.ejercicio2/databases/task_gamification.db
```

---

### Método 4: Stetho (Facebook Debug Bridge)

Para apps en desarrollo, puedes usar Stetho para inspeccionar la BD desde Chrome.

**1. Agregar dependencia (si quieres):**
```gradle
debugImplementation 'com.facebook.stetho:stetho:1.6.0'
```

**2. Inicializar en Application:**
```kotlin
Stetho.initializeWithDefaults(this)
```

**3. Ver en Chrome:**
- Ejecuta la app
- Abre Chrome: `chrome://inspect`
- Click en "Inspect" bajo tu app
- Ve a "Resources" → "Web SQL" → "task_gamification.db"

---

## 🧪 Verificar que la BD se Creó Correctamente

### Desde Logcat de Android Studio:
Filtra por `MainActivity` y busca:
```
✅ Base de datos inicializada correctamente
📍 Ruta: /data/data/com.example.ejercicio2/databases/task_gamification.db
```

### Verificar Tablas Creadas:
```sql
-- En Database Inspector o SQLite shell:
SELECT name FROM sqlite_master WHERE type='table';
```

**Deberías ver:**
- users
- tasks
- badges
- user_badges
- study_sessions
- daily_stats
- activity_log
- app_settings
- sync_queue

### Verificar Datos Iniciales:
```sql
-- Ver badges predefinidos
SELECT badge_key, name, description FROM badges;

-- Ver usuario creado
SELECT id, name, email, level, current_xp FROM users;

-- Ver tareas de ejemplo (si createSampleData = true)
SELECT title, category, due_date, status FROM tasks;
```

---

## 📊 Herramientas Recomendadas

### DB Browser for SQLite (GRATIS)
- 🌐 Website: https://sqlitebrowser.org/
- 💻 Multiplataforma (Windows, Mac, Linux)
- ✨ Interfaz gráfica amigable
- 🔍 Explorar tablas, ejecutar queries, modificar datos
- 📊 Ver estructura del esquema
- 📈 Exportar a CSV, SQL, JSON

### SQLite Studio (GRATIS)
- 🌐 Website: https://sqlitestudio.pl/
- 💻 Multiplataforma
- 🎨 Interfaz más avanzada
- 🔧 Herramientas de diseño de esquema
- 📝 Editor SQL con autocompletado

### Navicat for SQLite (PAGO)
- 🌐 Website: https://www.navicat.com/
- 💰 Versión de prueba disponible
- 🚀 Características profesionales
- 🔄 Sincronización y backup avanzado

---

## 🛠️ Comandos SQL Útiles

### Información de la Base de Datos:
```sql
-- Ver versión de SQLite
SELECT sqlite_version();

-- Ver todas las tablas
SELECT name FROM sqlite_master WHERE type='table';

-- Ver estructura de una tabla
PRAGMA table_info(users);

-- Ver índices
SELECT name FROM sqlite_master WHERE type='index';

-- Ver triggers
SELECT name FROM sqlite_master WHERE type='trigger';

-- Ver tamaño de la BD
SELECT page_count * page_size as size FROM pragma_page_count(), pragma_page_size();
```

### Consultas de Datos:
```sql
-- Ver resumen del usuario
SELECT * FROM v_user_summary;

-- Tareas pendientes
SELECT * FROM v_tasks_enriched WHERE status = 'PENDING';

-- Progreso de badges
SELECT * FROM v_badge_progress WHERE is_unlocked = 1;

-- Estadísticas de la semana
SELECT * FROM v_weekly_stats;

-- Actividad reciente
SELECT * FROM activity_log ORDER BY created_at DESC LIMIT 10;
```

---

## 🚨 Troubleshooting

### La BD no aparece en Database Inspector:
1. Asegúrate de que la app está corriendo (no solo instalada)
2. Usa un emulador o dispositivo con depuración habilitada
3. Reinicia Android Studio
4. Limpia y reconstruye: `Build` → `Clean Project` → `Rebuild Project`

### No puedo acceder a /data/data/ con Device File Explorer:
1. **En dispositivos físicos:** Necesitas root
2. **En emuladores:** Funciona directamente
3. **Alternativa:** Usa `adb pull` como se mostró arriba

### El archivo .db está vacío o corrupto:
1. Verifica el Logcat por errores
2. Desinstala y reinstala la app
3. Verifica que `createSampleData = true` en `MainActivity`
4. Limpia datos de la app: `Settings` → `Apps` → Tu App → `Clear Data`

### Foreign Keys no funcionan:
El DatabaseHelper ya tiene `PRAGMA foreign_keys=ON;` en el método `onOpen()`.

---

## 📝 Checklist de Verificación

- [ ] La app se ejecuta sin crashes
- [ ] Aparece el mensaje "Base de datos inicializada" en Logcat
- [ ] Database Inspector muestra las 9 tablas
- [ ] Existe al menos 1 usuario en la tabla `users`
- [ ] Existen 16 badges en la tabla `badges`
- [ ] Si `createSampleData = true`, hay 5 tareas de ejemplo
- [ ] Los triggers funcionan (completa una tarea y verifica que el XP aumenta)

---

## 🎯 Próximos Pasos

Una vez que verifiques que la BD funciona:

1. **Crear DAOs (Data Access Objects)** para operaciones CRUD
2. **Implementar Repository Pattern** para abstracción de datos
3. **Integrar con ViewModel** para acceso desde UI
4. **Agregar Room Database** (opcional, para mayor abstracción)
5. **Implementar sincronización cloud** usando `sync_queue`

---

## 💡 Tips Adicionales

### Backup Automático:
```kotlin
// En algún lugar de tu app
fun backupDatabase(context: Context) {
    val currentDB = context.getDatabasePath("task_gamification.db")
    val backupDir = File(context.getExternalFilesDir(null), "backups")
    backupDir.mkdirs()
    
    val backupDB = File(backupDir, "backup_${System.currentTimeMillis()}.db")
    currentDB.copyTo(backupDB, overwrite = true)
    
    Log.d("Backup", "BD respaldada en: ${backupDB.absolutePath}")
}
```

### Resetear BD para Testing:
```kotlin
// En MainActivity o donde necesites
DatabaseHelper.getInstance(this).clearDatabase()
DatabaseInitializer.initialize(this, createSampleData = true)
```

### Exportar a JSON:
```kotlin
// Útil para debugging o compartir datos
fun exportTasksToJson(context: Context): String {
    val db = DatabaseHelper.getInstance(context).readableDatabase
    val cursor = db.rawQuery("SELECT * FROM tasks", null)
    
    val tasks = mutableListOf<Map<String, Any?>>()
    cursor.use {
        while (it.moveToNext()) {
            val task = mutableMapOf<String, Any?>()
            for (i in 0 until it.columnCount) {
                task[it.getColumnName(i)] = when (it.getType(i)) {
                    android.database.Cursor.FIELD_TYPE_INTEGER -> it.getLong(i)
                    android.database.Cursor.FIELD_TYPE_FLOAT -> it.getDouble(i)
                    else -> it.getString(i)
                }
            }
            tasks.add(task)
        }
    }
    
    return org.json.JSONArray(tasks).toString(2)
}
```

---

**¿Necesitas ayuda?** Revisa el código en:
- `DatabaseHelper.kt` - Configuración de la BD
- `DatabaseInitializer.kt` - Inicialización y datos de ejemplo
- `schema.sql` - Estructura completa
- `DATABASE_DOCUMENTATION.md` - Documentación detallada

¡Éxito con tu base de datos! 🚀
