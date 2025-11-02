package com.example.ejercicio2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejercicio2.database.DatabaseHelper
import com.example.ejercicio2.database.DatabaseInitializer
import com.example.ejercicio2.ui.theme.Ejercicio2Theme
import java.io.File

class DatabaseDebugActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            Ejercicio2Theme {
                DatabaseDebugScreen()
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatabaseDebugScreen() {
        var dbInfo by remember { mutableStateOf<String>("Cargando...") }
        var tableCount by remember { mutableStateOf(0) }
        var userCount by remember { mutableStateOf(0) }
        var taskCount by remember { mutableStateOf(0) }
        var badgeCount by remember { mutableStateOf(0) }
        var dbExists by remember { mutableStateOf(false) }
        var dbPath by remember { mutableStateOf("") }
        var dbSize by remember { mutableStateOf("") }
        
        LaunchedEffect(Unit) {
            try {
                // Obtener información de la BD
                val info = DatabaseInitializer.getDatabaseInfo(this@DatabaseDebugActivity)
                dbExists = info.exists
                dbPath = info.path
                dbSize = info.getSizeInKB()
                
                // Verificar tablas
                val dbHelper = DatabaseHelper.getInstance(this@DatabaseDebugActivity)
                val db = dbHelper.readableDatabase
                
                // Contar tablas
                val tablesCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'",
                    null
                )
                tablesCursor.use {
                    if (it.moveToFirst()) {
                        tableCount = it.getInt(0)
                    }
                }
                
                // Contar usuarios
                val usersCursor = db.rawQuery("SELECT COUNT(*) FROM users", null)
                usersCursor.use {
                    if (it.moveToFirst()) {
                        userCount = it.getInt(0)
                    }
                }
                
                // Contar tareas
                val tasksCursor = db.rawQuery("SELECT COUNT(*) FROM tasks", null)
                tasksCursor.use {
                    if (it.moveToFirst()) {
                        taskCount = it.getInt(0)
                    }
                }
                
                // Contar badges
                val badgesCursor = db.rawQuery("SELECT COUNT(*) FROM badges", null)
                badgesCursor.use {
                    if (it.moveToFirst()) {
                        badgeCount = it.getInt(0)
                    }
                }
                
                dbInfo = "✅ Base de datos operativa"
                
            } catch (e: Exception) {
                dbInfo = "❌ Error: ${e.message}"
                e.printStackTrace()
            }
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("🗄️ Database Debug") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Estado de la BD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (dbExists) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (dbExists) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = dbInfo,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Información de la BD
                InfoCard(
                    title = "📍 Ubicación",
                    value = dbPath,
                    monospace = true
                )
                
                InfoCard(
                    title = "📦 Tamaño",
                    value = dbSize
                )
                
                InfoCard(
                    title = "✔️ Existe Físicamente",
                    value = if (dbExists) "SÍ ✅" else "NO ❌"
                )
                
                Divider()
                
                // Estadísticas
                Text(
                    text = "📊 Estadísticas de Datos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Tablas",
                        value = tableCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Usuarios",
                        value = userCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Tareas",
                        value = taskCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Badges",
                        value = badgeCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Divider()
                
                // Instrucciones
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Cómo ver la Base de Datos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        
                        Text(
                            text = """
                                1️⃣ Método más fácil:
                                • View → Tool Windows → App Inspection
                                • Pestaña "Database Inspector"
                                • Selecciona la app
                                • ¡Explora las tablas!
                                
                                2️⃣ Extraer archivo:
                                • View → Tool Windows → Device File Explorer
                                • Navega a: $dbPath
                                • Click derecho → Save As...
                                • Abre con DB Browser for SQLite
                                
                                3️⃣ Desde terminal:
                                • adb pull $dbPath ./database.db
                            """.trimIndent(),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                // Botón de reinicializar
                Button(
                    onClick = {
                        try {
                            DatabaseHelper.getInstance(this@DatabaseDebugActivity).clearDatabase()
                            DatabaseInitializer.initialize(this@DatabaseDebugActivity, createSampleData = true)
                            // Recargar información
                            recreate()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    )
                ) {
                    Text("🔄 Reinicializar Base de Datos")
                }
            }
        }
    }
    
    @Composable
    fun InfoCard(
        title: String,
        value: String,
        monospace: Boolean = false
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    fontSize = 12.sp,
                    fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    @Composable
    fun StatCard(
        title: String,
        value: String,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
