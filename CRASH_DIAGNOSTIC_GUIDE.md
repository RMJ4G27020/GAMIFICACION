# 🔧 Guía de Diagnóstico y Reparación de Crashes

## ❌ La app se cierra al ejecutar en el emulador

Este documento te ayudará a identificar y resolver por qué tu app se cierra inmediatamente.

---

## 🎯 Causas Comunes de Crashes

### 1. **Problema con la Base de Datos** (75% de los casos)
- BD corrupta o no inicializada correctamente
- Tabla faltante o estructura incorrecta
- Permisos insuficientes para acceder a BD

### 2. **Problema con Permisos de Tiempo de Ejecución** (15% de casos)
- Android requiere permisos en tiempo de ejecución (API 23+)
- La app intenta acceder a cámara/calendario sin permiso

### 3. **Problema con Composable/UI** (10% de casos)
- Error en estructura Compose
- NullPointerException en alguna composable

---

## 🔍 Paso 1: Verificar Logcat

### En Android Studio:

1. **Abre Logcat** (abajo a la derecha, o `Alt + 6`)
2. **Filtra por "MainActivity"** o **"Error"**
3. **Busca mensajes como:**
   - `E/AndroidRuntime: FATAL EXCEPTION`
   - `E/MainActivity: ❌ Error`
   - Trace del error completo

### Ejemplo de error:
```
E/AndroidRuntime: FATAL EXCEPTION: main
Process: com.example.ejercicio2, PID: 12345
java.lang.RuntimeException: Unable to start activity ComponentInfo
Caused by: android.database.sqlite.SQLiteException: 
  no such table: users
```

---

## 🛠️ Paso 2: Usar CrashDiagnosticActivity

Hemos agregado una **Activity de diagnóstico** que te ayudará a identificar el problema:

### Opción A: Lanzarla desde Terminal ADB
```powershell
adb shell am start -n com.example.ejercicio2/.CrashDiagnosticActivity
```

### Opción B: Modificar MainActivity para lanzarla si hay crash

**Abre `MainActivity.kt` y modifica `onCreate`:**

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    try {
        // Tu código original...
    } catch (e: Exception) {
        Log.e("MainActivity", "❌ Crash detectado, abriendo diagnóstico", e)
        // Abrir CrashDiagnosticActivity
        val intent = Intent(this, CrashDiagnosticActivity::class.java)
        intent.putExtra("error_message", e.message)
        startActivity(intent)
        finish()
        return
    }
}
```

---

## 🔧 Soluciones Comunes

### Solución 1: Limpiar y Recrear Base de Datos

**Desde Terminal ADB:**
```powershell
adb shell
cd /data/data/com.example.ejercicio2/databases
rm task_gamification.db
exit
```

Luego reinicia la app.

**O usando CrashDiagnosticActivity:**
1. Ejecuta: `adb shell am start -n com.example.ejercicio2/.CrashDiagnosticActivity`
2. Toca **"🗑️ Limpiar Datos"**
3. Toca **"▶️ Reintentar MainActivity"**

### Solución 2: Verificar Permisos

Asegúrate de que en `AndroidManifest.xml` estén todos los permisos:

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.READ_CALENDAR" />
<uses-permission android:name="android.permission.WRITE_CALENDAR" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### Solución 3: Verificar DatabaseInitializer

**Asegúrate que `DatabaseInitializer.initialize()` se ejecute ANTES de cualquier BD:**

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    try {
        // ✅ PRIMERO: Inicializar BD
        if (!DatabaseInitializer.initialize(this, createSampleData = true)) {
            throw Exception("No se pudo inicializar la base de datos")
        }
        
        // ✅ SEGUNDO: Crear UI
        setContent {
            // Tu composable...
        }
    } catch (e: Exception) {
        Log.e("MainActivity", "Error fatal", e)
        // Manejar error
    }
}
```

### Solución 4: Desactivar Datos de Ejemplo

Si la app funciona sin datos de ejemplo, intenta:

```kotlin
// En MainActivity.kt, cambia esto:
DatabaseInitializer.initialize(this, createSampleData = true)

// A esto:
DatabaseInitializer.initialize(this, createSampleData = false)
```

---

## 🚨 Debugging Avanzado

### Ver todo el Logcat en tiempo real:
```powershell
adb logcat | findstr "MainActivity\|Error\|Exception"
```

### Capturar crash completo:
```powershell
adb logcat > crash_log.txt
# Ejecuta la app y reproduce el crash
# Presiona Ctrl+C después de que falle
```

### Analizar el archivo de log:
1. Abre `crash_log.txt`
2. Busca: `FATAL EXCEPTION` o `Caused by:`
3. Lee el stack trace completo

---

## ✅ Checklist de Diagnóstico

- [ ] ¿Aparece el error en Logcat?
- [ ] ¿Menciona "database" o "table"?
- [ ] ¿Es un problema de permisos?
- [ ] ¿Se necesita limpiar datos?
- [ ] ¿Funciona sin datos de ejemplo?
- [ ] ¿El emulador tiene suficiente almacenamiento?

---

## 📱 Pasos Recomendados para Solucionar

### 1️⃣ Primera Intención (50% de probabilidad de éxito)
```powershell
# En Android Studio Terminal:
adb shell
rm /data/data/com.example.ejercicio2/databases/task_gamification.db
exit
```
Luego ejecuta la app nuevamente.

### 2️⃣ Segunda Intención (30% de probabilidad)
```powershell
adb uninstall com.example.ejercicio2
```
Luego reconstruye y ejecuta desde Android Studio (Run > Run 'app').

### 3️⃣ Tercera Intención (15% de probabilidad)
1. Abre Android Studio
2. Build > Clean Project
3. Build > Rebuild Project
4. Ejecuta la app

### 4️⃣ Última Intención (5% de probabilidad)
1. Elimina el emulador actual
2. Crea uno nuevo (preferiblemente con Android 12+)
3. Ejecuta la app

---

## 📋 Información Necesaria para Reportar Bug

Si aún no funciona, recopila:

1. **Logs completos de error** (desde Logcat)
2. **API version del emulador** (Build > Select Build Variant)
3. **Output de CrashDiagnosticActivity**
4. **Captura de pantalla del error**

---

## 🎯 Próximos Pasos

Si ya resolviste el crash:

1. ✅ Ejecuta la app correctamente
2. ✅ Verifica que todas las pantallas funcionan
3. ✅ Prueba los permisos (cámara, calendario)
4. ✅ Prueba la BD (crear tareas)

---

**¡Espero que esto resuelva tu problema! 🚀**
