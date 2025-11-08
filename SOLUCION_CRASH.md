# 🔧 Solución del Crash al Iniciar la App

## 📋 Problema Reportado
**Usuario:** "la app en android studio se instala y se inicia pero se cierra inmediatamente"

## 🔍 Diagnóstico del Problema

### 1. **Síntoma Inicial**
```
FATAL EXCEPTION: main
android.database.sqlite.SQLiteException: no such table: users
at TaskRepository.getMainUser(TaskRepository.kt:257)
at TaskManagerViewModel.loadUser(TaskManagerViewModel.kt:62)
at TaskManagerViewModel.<init>(TaskManagerViewModel.kt:31)
```

### 2. **Causa Raíz**
La base de datos no se estaba creando correctamente debido a un **parser defectuoso de SQL**:

#### Problema en `DatabaseHelper.kt`:
```kotlin
// ❌ CÓDIGO ANTIGUO (INCORRECTO)
private fun executeMultipleStatements(db: SQLiteDatabase, sql: String) {
    val statements = sql.split(";")  // Split simple
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("--") }

    statements.forEach { statement ->
        try {
            db.execSQL(statement)
        } catch (e: Exception) {
            // ❌ Tragaba errores sin lanzarlos
            android.util.Log.e("DatabaseHelper", "Error executing SQL: $statement", e)
        }
    }
}
```

**Problemas:**
1. ❌ **Split ingenuo por `;`** - Cortaba TRIGGERS en medio del `BEGIN...END` block
2. ❌ **Tragaba excepciones** - No lanzaba errores, permitía que onCreate "exitoso" con tablas faltantes
3. ❌ **Sin validación de estado** - No verificaba si las tablas realmente se crearon

### 3. **Error Específico Detectado**
```
E DatabaseHelper: Error executing SQL: CREATE TRIGGER IF NOT EXISTS update_users_timestamp
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    UP...

E DatabaseHelper: android.database.sqlite.SQLiteException: incomplete input (code 1 SQLITE_ERROR)
```

El TRIGGER se cortó así:
```sql
-- Statement 1 (incompleto):
CREATE TRIGGER IF NOT EXISTS update_users_timestamp
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE id = OLD.id

-- Statement 2 (basura):
END
```

## ✅ Solución Implementada

### **Cambio en `DatabaseHelper.kt`**

```kotlin
// ✅ CÓDIGO NUEVO (CORRECTO)
private fun executeMultipleStatements(db: SQLiteDatabase, sql: String) {
    // 1. Eliminar comentarios de línea
    val cleanSql = sql.lines()
        .filterNot { it.trim().startsWith("--") }
        .joinToString("\n")
    
    // 2. Parser que maneja BEGIN...END blocks
    val statements = mutableListOf<String>()
    var currentStatement = StringBuilder()
    var insideBlock = 0 // Contador para BEGIN...END anidados
    
    cleanSql.split(";").forEach { part ->
        currentStatement.append(part).append(";")
        
        // 3. Contar palabras BEGIN y END completas (no parte de otras palabras)
        val beginMatches = "\\bBEGIN\\b".toRegex(RegexOption.IGNORE_CASE).findAll(part).count()
        val endMatches = "\\bEND\\b".toRegex(RegexOption.IGNORE_CASE).findAll(part).count()
        
        insideBlock += beginMatches - endMatches
        
        // 4. Si no estamos dentro de un bloque BEGIN...END, este statement está completo
        if (insideBlock <= 0) {
            val stmt = currentStatement.toString().trim().removeSuffix(";")
            if (stmt.isNotBlank()) {
                statements.add(stmt)
            }
            currentStatement.clear()
            insideBlock = 0
        }
    }
    
    android.util.Log.d("DatabaseHelper", "Ejecutando ${statements.size} statements SQL")
    
    // 5. Ejecutar statements con logging detallado
    statements.forEachIndexed { index, statement ->
        if (statement.isNotBlank()) {
            try {
                val preview = statement.replace("\\s+".toRegex(), " ").take(80)
                android.util.Log.d("DatabaseHelper", "Ejecutando [${index + 1}/${statements.size}]: $preview...")
                db.execSQL(statement)
            } catch (e: Exception) {
                android.util.Log.e("DatabaseHelper", "❌ Error en statement ${index + 1}", e)
                // 6. ✅ LANZAR excepción para que onCreate falle correctamente
                throw e
            }
        }
    }
    
    android.util.Log.d("DatabaseHelper", "✅ Todos los statements ejecutados correctamente")
}
```

### **Mejoras Clave:**

1. ✅ **Parser inteligente de SQL**
   - Detecta bloques `BEGIN...END` con regex `\bBEGIN\b` y `\bEND\b`
   - Mantiene contador de profundidad para bloques anidados
   - Solo termina statement cuando `insideBlock <= 0`

2. ✅ **Manejo correcto de errores**
   - `throw e` - Lanza excepciones en lugar de tragarlas
   - `onCreate()` falla si hay error real
   - La app no continúa con BD corrupta

3. ✅ **Logging detallado**
   - Muestra progreso: `"Ejecutando [1/30]..."`
   - Preview de cada statement
   - Mensaje de éxito: `"✅ Todos los statements ejecutados correctamente"`

## 📊 Resultado Final

### **Antes (Fallaba)**
```
11-08 02:31:26.903 E AndroidRuntime: FATAL EXCEPTION: main
11-08 02:31:26.903 E AndroidRuntime: android.database.sqlite.SQLiteException: no such table: users
11-08 02:31:26.903 E AndroidRuntime:      at TaskRepository.getMainUser(TaskRepository.kt:257)
```

### **Después (Exitoso)**
```
11-08 02:51:22.770 D DatabaseHelper: Ejecutando 30 statements SQL
11-08 02:51:22.773 D DatabaseHelper: Ejecutando [1/30]: CREATE TABLE IF NOT EXISTS users...
11-08 02:51:22.775 D DatabaseHelper: Ejecutando [2/30]: CREATE INDEX IF NOT EXISTS idx_users_uuid...
...
11-08 02:51:22.798 D DatabaseHelper: Ejecutando [26/30]: CREATE TRIGGER IF NOT EXISTS update_users_timestamp...
11-08 02:51:22.799 D DatabaseHelper: Ejecutando [27/30]: CREATE TRIGGER IF NOT EXISTS update_tasks_timestamp...
11-08 02:51:22.802 D DatabaseHelper: Ejecutando [28/30]: CREATE TRIGGER IF NOT EXISTS task_completed_stats...
11-08 02:51:22.806 D DatabaseHelper: ✅ Todos los statements ejecutados correctamente
```

### **Estado de la App**
```bash
$ adb shell dumpsys activity activities | grep ejercicio2
  * Task #100 visible=true visibleRequested=true
  topResumedActivity=MainActivity
  Resumed: ActivityRecord MainActivity
```

## 🎯 Verificación

1. ✅ **Base de datos creada** - 30 statements ejecutados
2. ✅ **Tablas creadas** - users, tasks, badges, etc.
3. ✅ **Índices creados** - idx_users_uuid, idx_tasks_status, etc.
4. ✅ **Triggers creados** - update_users_timestamp, task_completed_stats, etc.
5. ✅ **App iniciada** - MainActivity visible y en ejecución
6. ✅ **Sin crashes** - No hay FATAL EXCEPTION en logcat

## 📝 Lecciones Aprendidas

1. **No usar `split(";")` simple para SQL** - Los TRIGGERS y procedimientos tienen `;` internos
2. **Siempre lanzar excepciones en inicialización crítica** - No tragar errores que dejan la app en estado inconsistente
3. **Logging detallado es esencial** - Permitió identificar exactamente qué statement fallaba
4. **Desinstalar completamente antes de probar** - BD corrupta puede persistir entre instalaciones

## 🔄 Commits Realizados

1. ✅ Mejorado parser de SQL en `DatabaseHelper.kt`
2. ✅ Añadido logging detallado de creación de BD
3. ✅ Implementado manejo correcto de excepciones en `onCreate()`

## 🚀 App Ahora Funcional

- La app se instala correctamente
- La base de datos se crea sin errores
- MainActivity se inicia y es visible
- No hay crashes al inicio

---
**Fecha:** 2025-11-08  
**Archivo modificado:** `app/src/main/java/com/example/ejercicio2/database/DatabaseHelper.kt`  
**Estado:** ✅ RESUELTO
