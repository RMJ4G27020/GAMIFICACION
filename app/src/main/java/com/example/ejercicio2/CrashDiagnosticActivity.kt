package com.example.ejercicio2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontMonospace
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejercicio2.database.DatabaseHelper
import com.example.ejercicio2.database.DatabaseInitializer
import com.example.ejercicio2.ui.theme.Ejercicio2Theme
import java.io.File

/**
 * CrashDiagnosticActivity - Diagnóstico para problemas de inicialización
 * 
 * Esta activity se abre si MainActivity falla, permitiendo ver logs y diagnósticos
 */
class CrashDiagnosticActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            Ejercicio2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiagnosticScreen()
                }
            }
        }
    }

    @Composable
    fun DiagnosticScreen() {
        var diagnosticInfo by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            diagnosticInfo = performDiagnostics()
            isLoading = false
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    "🔧 Diagnóstico de Crash",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x1F2196F3)
                    )
                ) {
                    Text(
                        diagnosticInfo,
                        modifier = Modifier.padding(12.dp),
                        fontFamily = FontMonospace,
                        fontSize = 10.sp,
                        lineHeight = 12.sp
                    )
                }

                Button(
                    onClick = { 
                        try {
                            attemptRepairDatabase()
                        } catch (e: Exception) {
                            Log.e("CrashDiagnostic", "Error al reparar BD", e)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("🔧 Reparar Base de Datos")
                }

                Button(
                    onClick = { 
                        clearAppData()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Text("🗑️ Limpiar Datos (Reinicia app después)")
                }

                Button(
                    onClick = { 
                        try {
                            startActivity(intent.apply {
                                setClass(this@CrashDiagnosticActivity, MainActivity::class.java)
                            })
                            finish()
                        } catch (e: Exception) {
                            Log.e("CrashDiagnostic", "Error al reiniciar", e)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("▶️ Reintentar MainActivity")
                }
            }
        }
    }

    private fun performDiagnostics(): String {
        val sb = StringBuilder()
        
        sb.append("=== DIAGNÓSTICO DE CRASH ===\n")
        sb.append("Fecha: ${java.time.LocalDateTime.now()}\n")
        sb.append("Dispositivo: ${android.os.Build.DEVICE}\n")
        sb.append("Android: ${android.os.Build.VERSION.SDK_INT}\n\n")

        // 1. Verificar BD
        sb.append("📦 BASE DE DATOS\n")
        try {
            val dbHelper = DatabaseHelper.getInstance(this)
            val db = dbHelper.readableDatabase
            sb.append("✅ BD se abrió correctamente\n")
            
            // Verificar tablas
            val cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'",
                null
            )
            val tableCount = cursor.count
            sb.append("   Tablas encontradas: $tableCount\n")
            cursor.close()
            db.close()
        } catch (e: Exception) {
            sb.append("❌ Error al abrir BD: ${e.message}\n")
            sb.append("   ${e.cause}\n")
        }

        // 2. Verificar archivo de BD
        sb.append("\n📁 ARCHIVOS\n")
        try {
            val dbPath = getDatabasePath("task_gamification.db")
            val exists = dbPath.exists()
            val size = if (exists) "${dbPath.length() / 1024}KB" else "N/A"
            sb.append("   BD existe: $exists (${size})\n")
            
            if (exists) {
                sb.append("   Ruta: ${dbPath.absolutePath}\n")
            }
        } catch (e: Exception) {
            sb.append("❌ Error al verificar archivos: ${e.message}\n")
        }

        // 3. Verificar permisos
        sb.append("\n🔐 PERMISOS\n")
        val permisos = arrayOf(
            "android.permission.READ_CALENDAR",
            "android.permission.WRITE_CALENDAR",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMERA"
        )
        
        for (permiso in permisos) {
            val granted = checkCallingOrSelfPermission(permiso) == 
                android.content.pm.PackageManager.PERMISSION_GRANTED
            val status = if (granted) "✅" else "❌"
            sb.append("   $status ${permiso.split(".").last()}\n")
        }

        // 4. Verificar espacio disponible
        sb.append("\n💾 ALMACENAMIENTO\n")
        try {
            val stat = android.os.StatFs(filesDir.absolutePath)
            val availableMB = stat.availableBytes / 1024 / 1024
            val totalMB = stat.totalBytes / 1024 / 1024
            sb.append("   Disponible: ${availableMB}MB / ${totalMB}MB\n")
        } catch (e: Exception) {
            sb.append("❌ Error al obtener almacenamiento: ${e.message}\n")
        }

        // 5. Verificar datos de ejemplo
        sb.append("\n📊 DATOS DE EJEMPLO\n")
        try {
            val initialized = DatabaseInitializer.initialize(this, createSampleData = false)
            sb.append("   Inicialización: ${if (initialized) "✅ OK" else "❌ FALLIDA"}\n")
        } catch (e: Exception) {
            sb.append("❌ Error: ${e.message}\n")
        }

        return sb.toString()
    }

    private fun attemptRepairDatabase() {
        try {
            Log.d("CrashDiagnostic", "Intentando reparar BD...")
            
            // 1. Eliminar BD corrupta
            deleteDatabase("task_gamification.db")
            
            // 2. Recrear BD
            val initialized = DatabaseInitializer.initialize(this, createSampleData = true)
            
            if (initialized) {
                Log.d("CrashDiagnostic", "✅ BD reparada correctamente")
            } else {
                Log.e("CrashDiagnostic", "❌ Error al recrear BD")
            }
        } catch (e: Exception) {
            Log.e("CrashDiagnostic", "❌ Error durante reparación", e)
        }
    }

    private fun clearAppData() {
        try {
            deleteDatabase("task_gamification.db")
            val cache = cacheDir
            deleteRecursive(cache)
            Log.d("CrashDiagnostic", "✅ Datos limpiados")
        } catch (e: Exception) {
            Log.e("CrashDiagnostic", "❌ Error al limpiar datos", e)
        }
    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach { file ->
                deleteRecursive(file)
            }
        }
        fileOrDirectory.delete()
    }
}
