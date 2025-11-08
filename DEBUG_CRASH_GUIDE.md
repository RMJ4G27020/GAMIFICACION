# 🔍 Guía de Debugging - App se Cierra

## 📱 Problema Actual
La app se cierra al ejecutar en el emulador de Android Studio.

## ✅ Soluciones Aplicadas

### 1. ✅ Core Library Desugaring Habilitado
```kotlin
// build.gradle.kts
compileOptions {
    isCoreLibraryDesugaringEnabled = true
}
dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}
```

### 2. ✅ Removidas Anotaciones @RequiresApi
Todos los `@RequiresApi(Build.VERSION_CODES.O)` fueron removidos de:
- ✅ MainActivity.kt
- ✅ Models.kt
- ✅ DashboardScreen.kt
- ✅ TaskManagerViewModel.kt
- ✅ Components.kt
- ✅ TaskRepository.kt

### 3. ✅ Try-Catch Robusto en onCreate()
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    try {
        initializeDatabase()
        setContent { ... }
    } catch (e: Exception) {
        // Intento de recuperación
        DatabaseInitializer.initialize(this, createSampleData = false)
        setContent { ... }
    }
}
```

## 🔍 Cómo Ver los Logs de Error (Logcat)

### Método 1: Android Studio Logcat (RECOMENDADO)
1. **Ejecuta la app** en el emulador
2. **Abre Logcat**: 
   - Menú: `View` → `Tool Windows` → `Logcat`
   - O tecla: `Alt + 6`
3. **Filtra por errores**:
   - En el dropdown de nivel, selecciona: **Error**
   - O escribe en el filtro: `tag:MainActivity` o `tag:AndroidRuntime`
4. **Busca el stacktrace**:
   - Busca líneas que digan: `FATAL EXCEPTION` o `java.lang.RuntimeException`
   - Copia el error completo

### Método 2: Terminal ADB
```powershell
# Conecta el dispositivo y ejecuta
adb logcat -s "AndroidRuntime:E MainActivity:E"

# O todo el log filtrado por error
adb logcat *:E
```

### Método 3: Logcat desde Run
1. Ejecuta la app con el botón **Run** (▶️)
2. La pestaña **Logcat** se abre automáticamente abajo
3. Si la app crashea, verás el error en rojo

## 📊 Qué Buscar en Logcat

### 🔴 Errores Comunes

#### Error 1: API Level Incompatibility
```
java.lang.NoClassDefFoundError: Failed resolution of: Ljava/time/LocalDate
```
**Solución:** ✅ Ya aplicado (desugaring)

#### Error 2: ViewModel Context Null
```
java.lang.NullPointerException: context must not be null
```
**Solución:** ✅ Ya aplicado (try-catch en onCreate)

#### Error 3: Database Initialization Fail
```
android.database.sqlite.SQLiteException: near "CREATE": syntax error
```
**Solución:** Verificar DatabaseHelper schema

#### Error 4: Permission Denied
```
java.lang.SecurityException: Permission denied: calendar access
```
**Solución:** Verificar permisos en AndroidManifest (✅ ya están)

#### Error 5: Memory Issues
```
OutOfMemoryError: Failed to allocate
```
**Solución:** Reducir datos de ejemplo o aumentar heap size

## 🛠️ Pasos de Debugging

### Paso 1: Limpiar y Recompilar
```powershell
.\gradlew clean assembleDebug -x lintDebug
```

### Paso 2: Verificar Emulador
- ✅ API Level del emulador: **24 o superior**
- ✅ Espacio disponible: **> 2 GB**
- ✅ RAM asignada: **> 2 GB**

### Paso 3: Instalar APK Manualmente
```powershell
# Desinstalar versión anterior
adb uninstall com.example.ejercicio2

# Instalar nueva versión
adb install app\build\outputs\apk\debug\app-debug.apk

# Ver logs en tiempo real
adb logcat -c  # Limpiar logs
adb logcat | Select-String "ejercicio2|MainActivity|FATAL"
```

### Paso 4: Ejecutar en Modo Debug
1. Click en el icono **Debug** (🐞) en lugar de Run
2. Agrega breakpoints en:
   - `MainActivity.onCreate()` línea 36
   - `initializeDatabase()` línea 213
   - `TaskManagerViewModel` constructor
3. Ejecuta paso a paso con F8

## 📝 Información para Reportar

Si sigues teniendo problemas, necesito esta información:

### 1. Logcat Error Completo
```
Copia aquí el stacktrace completo desde Logcat
Incluye las líneas desde "FATAL EXCEPTION" hasta el final
```

### 2. Especificaciones del Emulador
```
- Nombre del dispositivo: 
- API Level: 
- Tamaño de RAM: 
- Espacio disponible:
```

### 3. Versión de Build Tools
```powershell
.\gradlew --version
```

### 4. Estado del APK
```powershell
# Verificar que existe
Test-Path "app\build\outputs\apk\debug\app-debug.apk"

# Tamaño del APK
(Get-Item "app\build\outputs\apk\debug\app-debug.apk").Length / 1MB
```

## 🚀 Checklist de Verificación

Antes de reportar, verifica:

- [ ] ✅ Build exitoso (BUILD SUCCESSFUL)
- [ ] ✅ APK generado existe
- [ ] ✅ Emulador corriendo (adb devices muestra dispositivo)
- [ ] ✅ Permisos en AndroidManifest
- [ ] ✅ minSdk = 24 en build.gradle
- [ ] ✅ Desugaring habilitado
- [ ] ✅ Sin @RequiresApi en código
- [ ] ❓ Logcat capturado (NECESITO ESTO)
- [ ] ❓ Error específico identificado

## 💡 Soluciones Alternativas

### Si el error es de Base de Datos
Cambia en `MainActivity.kt` línea 215:
```kotlin
val success = DatabaseInitializer.initialize(this, createSampleData = false)
```

### Si el error es de Permisos
Comenta temporalmente en `AndroidManifest.xml`:
```xml
<!-- <uses-permission android:name="android.permission.READ_CALENDAR" /> -->
<!-- <uses-permission android:name="android.permission.WRITE_CALENDAR" /> -->
```

### Si el error es de ViewModel
Agrega logging en `TaskManagerViewModel.kt`:
```kotlin
init {
    Log.d("TaskManagerViewModel", "Inicializando ViewModel...")
    try {
        // código existente
        Log.d("TaskManagerViewModel", "✅ ViewModel inicializado")
    } catch (e: Exception) {
        Log.e("TaskManagerViewModel", "❌ Error en init", e)
        throw e
    }
}
```

## 📞 Siguiente Paso

**POR FAVOR EJECUTA ESTO Y PEGA EL RESULTADO:**

```powershell
# 1. Limpiar logs anteriores
adb logcat -c

# 2. Ejecutar app desde Android Studio (Run)

# 3. En otra terminal PowerShell, capturar error
adb logcat -d | Select-String "FATAL|Exception|Error" | Select-Object -First 50
```

O simplemente:
1. Abre **Logcat** en Android Studio
2. Ejecuta la app
3. Cuando crashee, copia el error completo (texto rojo)
4. Péga el error aquí

---

**Compilación:** ✅ BUILD SUCCESSFUL  
**APK:** ✅ Generado correctamente  
**Estado:** ⏳ Esperando logs de runtime para identificar crash exacto
