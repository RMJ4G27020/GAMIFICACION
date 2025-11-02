# 🚨 INSTRUCCIONES: Cómo Ver tu Base de Datos SQLite

## ⚠️ IMPORTANTE: La BD se crea cuando EJECUTAS la app

La base de datos **NO existe en tu proyecto**. Se crea **dentro del dispositivo/emulador** cuando ejecutas la app.

---

## 🎯 SOLUCIÓN RÁPIDA: Usa la Pantalla de Debug

### ✅ **Acabo de agregar una pantalla especial para ti:**

He creado `DatabaseDebugActivity` que te muestra:
- ✅ Si la base de datos existe
- ✅ Ubicación exacta del archivo .db
- ✅ Tamaño del archivo
- ✅ Cantidad de tablas, usuarios, tareas y badges
- ✅ Instrucciones de cómo acceder

---

## 📱 PASOS PARA VER LA BASE DE DATOS:

### 1️⃣ **Ejecuta la App en un Emulador o Dispositivo**

```bash
# Opción A: Desde Android Studio
Click en el botón verde ▶️ (Run) con un emulador/dispositivo conectado

# Opción B: Desde terminal (si tienes dispositivo conectado)
./gradlew installDebug
```

### 2️⃣ **Abre la Pantalla de Debug**

Una vez que la app esté corriendo:
- Ve al **Dashboard** (pantalla principal)
- Desplázate hacia abajo
- Verás un botón naranja: **"🗄️ Ver Estado de Base de Datos"**
- ¡Tócalo!

### 3️⃣ **Verás esta Información:**

```
✅ Base de datos operativa

📍 Ubicación:
/data/data/com.example.ejercicio2/databases/task_gamification.db

📦 Tamaño: XX.XX KB

✔️ Existe Físicamente: SÍ ✅

📊 Estadísticas:
- Tablas: 9
- Usuarios: 1
- Tareas: 5
- Badges: 16
```

---

## 🔍 MÉTODOS PARA EXPLORAR LA BASE DE DATOS:

### **Método 1: Database Inspector** (EL MÁS FÁCIL) ⭐⭐⭐

**Mientras la app está corriendo:**

1. En Android Studio: **View** → **Tool Windows** → **App Inspection**
2. Selecciona la pestaña **"Database Inspector"**
3. En el dropdown, selecciona tu app: `com.example.ejercicio2`
4. ¡Verás TODAS las tablas listadas! 🎉

**Puedes:**
- ✅ Ver datos en tiempo real
- ✅ Ejecutar queries SQL
- ✅ Modificar registros
- ✅ Ver la estructura de las tablas

---

### **Método 2: Device File Explorer** (Para Extraer el Archivo)

1. En Android Studio: **View** → **Tool Windows** → **Device File Explorer**
2. Navega a: `/data/data/com.example.ejercicio2/databases/`
3. Verás 3 archivos:
   - `task_gamification.db` ← **Este es el archivo principal**
   - `task_gamification.db-shm` (memoria compartida)
   - `task_gamification.db-wal` (write-ahead log)
4. **Click derecho** en `task_gamification.db`
5. Selecciona **"Save As..."**
6. Guarda en tu PC
7. Abre con **DB Browser for SQLite**

**Descargar DB Browser:**
- Windows: https://sqlitebrowser.org/dl/
- Instala y abre el archivo `.db` extraído

---

### **Método 3: ADB (Línea de Comandos)**

**Solo si tienes un dispositivo/emulador conectado:**

```bash
# 1. Extraer la base de datos a tu PC
adb pull /data/data/com.example.ejercicio2/databases/task_gamification.db ./mi_database.db

# 2. Abrir con SQLite (si tienes sqlite3 instalado)
sqlite3 mi_database.db

# 3. Dentro de SQLite:
.tables                    # Ver todas las tablas
SELECT * FROM users;       # Ver usuarios
SELECT * FROM tasks;       # Ver tareas
SELECT * FROM badges;      # Ver badges
.exit                      # Salir
```

---

## 🚨 TROUBLESHOOTING

### ❌ "No veo el Database Inspector"
**Solución:**
- Asegúrate de que la app **ESTÁ CORRIENDO** (no solo instalada)
- Usa Android Studio Arctic Fox o superior
- Ve a: **File** → **Settings** → **Plugins** → Verifica que "Android" esté habilitado

### ❌ "No puedo acceder a /data/data/"
**Causa:** En dispositivos físicos sin root, no puedes acceder directamente.
**Solución:**
- Usa un **emulador** (tienen acceso completo)
- O usa **Database Inspector** (no necesita acceso al filesystem)

### ❌ "La app crashea al iniciar"
**Solución:**
1. Mira el **Logcat** para ver el error
2. Filtra por "Database" o "SQLite"
3. Si ves errores, comparte el log conmigo

### ❌ "No hay dispositivo conectado"
**Solución A - Usar Emulador:**
1. En Android Studio: **Tools** → **Device Manager**
2. Crea un nuevo dispositivo virtual si no tienes
3. Inicia el emulador
4. Ejecuta la app

**Solución B - Conectar Dispositivo Físico:**
1. Habilita **Depuración USB** en tu teléfono:
   - **Ajustes** → **Acerca del teléfono** → Toca 7 veces en "Número de compilación"
   - Vuelve a **Ajustes** → **Opciones de desarrollador**
   - Activa **Depuración USB**
2. Conecta el teléfono a tu PC con cable USB
3. Autoriza la depuración en el teléfono

---

## 📊 VERIFICAR QUE TODO FUNCIONA:

Una vez que ejecutes la app, deberías ver en **Logcat**:

```
==============================================================================
🗄️  BASE DE DATOS CREADA EXITOSAMENTE
==============================================================================
📍 Ubicación del archivo .db:
   /data/data/com.example.ejercicio2/databases/task_gamification.db

💡 Para ver la base de datos:
   1. Usa Android Studio Database Inspector
   2. O descarga el archivo desde Device File Explorer
   3. Abre con DB Browser for SQLite
==============================================================================
```

**Filtrar Logcat:**
- En la barra de búsqueda de Logcat escribe: `MainActivity`
- O: `Database`

---

## 🎯 PRÓXIMOS PASOS:

### Una vez que VEAS la base de datos funcionando:

1. **Explora las Tablas:**
   ```sql
   SELECT * FROM users;        -- Ver usuario creado
   SELECT * FROM tasks;        -- Ver 5 tareas de ejemplo
   SELECT * FROM badges;       -- Ver 16 badges predefinidos
   SELECT * FROM daily_stats;  -- Ver estadísticas
   ```

2. **Prueba los Triggers:**
   - Completa una tarea en la app
   - Ve a `users` y verifica que el XP aumentó
   - Ve a `daily_stats` y verifica que se registró

3. **Experimenta:**
   - Agrega más tareas
   - Completa tareas
   - Ve cómo se actualizan los badges automáticamente

---

## 📁 ARCHIVOS QUE YA ESTÁN LISTOS:

✅ **`database/schema.sql`** - Schema completo (474 líneas)
✅ **`database/DATABASE_DOCUMENTATION.md`** - Documentación detallada
✅ **`database/COMO_VER_LA_BD.md`** - Esta guía
✅ **`DatabaseHelper.kt`** - Gestor de la BD
✅ **`DatabaseInitializer.kt`** - Inicializador con datos de ejemplo
✅ **`DatabaseDebugActivity.kt`** - Pantalla de debug (NUEVO)
✅ **`MainActivity.kt`** - Llama a `initializeDatabase()`
✅ **`DashboardScreen.kt`** - Botón para abrir debug (NUEVO)

---

## 💡 TIP FINAL:

Si quieres ver la BD **SIN ejecutar la app en un dispositivo**, puedes:

1. Crear un test unitario que genere la BD en tu PC
2. Usar Android Studio Emulator (recomendado)
3. Esperar a que agregue soporte para exportar la BD a un archivo en tu proyecto

---

## 🆘 ¿NECESITAS AYUDA?

Si después de estos pasos **TODAVÍA** no ves la base de datos:

1. **Ejecuta la app** en un emulador/dispositivo
2. **Toma una captura** de la pantalla de debug
3. **Copia el mensaje** del Logcat
4. **Comparte** esa información conmigo

---

## ✅ CHECKLIST:

- [ ] Ejecuté la app en un emulador/dispositivo
- [ ] Vi el botón "🗄️ Ver Estado de Base de Datos" en el Dashboard
- [ ] Toqué el botón y vi la información de la BD
- [ ] Verifiqué que dice "✅ Base de datos operativa"
- [ ] Abrí Database Inspector y vi las 9 tablas
- [ ] Exploré las tablas: users, tasks, badges

---

🎉 **¡Una vez que completes esto, la base de datos está 100% funcional!** 🎉
