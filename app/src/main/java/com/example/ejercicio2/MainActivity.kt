package com.example.ejercicio2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ejercicio2.auth.AuthManager
import com.example.ejercicio2.database.DatabaseInitializer
import com.example.ejercicio2.models.User
import com.example.ejercicio2.screens.*
import com.example.ejercicio2.ui.theme.Ejercicio2Theme
import com.example.ejercicio2.viewmodel.TaskManagerViewModel

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("MainActivity", "🚀 ========== INICIANDO MAINACTIVITY ==========")
        
        try {
            // Inicializar base de datos SIN datos de ejemplo (más estable)
            Log.d("MainActivity", "🔄 Paso 1: Inicializando BD...")
            val dbInitialized = DatabaseInitializer.initialize(this, createSampleData = false)
            Log.d("MainActivity", "   Resultado BD: $dbInitialized")
            
            if (!dbInitialized) {
                Log.e("MainActivity", "⚠️ DatabaseInitializer retornó false, pero continuamos...")
            } else {
                Log.d("MainActivity", "✅ Paso 1: BD inicializada correctamente")
            }
            
            Log.d("MainActivity", "🔄 Paso 2: Creando UI con setContent...")
            setContent {
                Ejercicio2Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Log.d("MainActivity", "🔄 Paso 3: Entrando en MainApp composable...")
                        MainApp()
                    }
                }
            }
            Log.d("MainActivity", "✅ Paso 2: UI creada exitosamente")
            
        } catch (e: Exception) {
            Log.e("MainActivity", "❌ ERROR en onCreate", e)
            Log.e("MainActivity", "   Tipo: ${e.javaClass.simpleName}")
            Log.e("MainActivity", "   Mensaje: ${e.message}")
            e.printStackTrace()
            
            // Intentar continuar de todas formas
            try {
                Log.d("MainActivity", "🔧 Intentando continuar sin reinicializar BD...")
                
                setContent {
                    Ejercicio2Theme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            MainApp()
                        }
                    }
                }
                Log.d("MainActivity", "✅ UI creada en modo recuperación")
                return // Salir exitosamente
            } catch (e2: Exception) {
                Log.e("MainActivity", "❌ Fallo en modo recuperación", e2)
            }
            
            // Si todo falla, ir a diagnóstico
            try {
                Log.d("MainActivity", "🔧 REPARACIÓN: Abriendo diagnóstico...")
                val dbInitialized = DatabaseInitializer.initialize(this, createSampleData = false)
                Log.d("MainActivity", "   Resultado BD sin datos: $dbInitialized")
                
                if (!dbInitialized) {
                    throw Exception("DatabaseInitializer retornó false (sin datos)")
                }
                
                setContent {
                    Ejercicio2Theme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            MainApp()
                        }
                    }
                }
                Log.d("MainActivity", "✅ REPARACIÓN 1: Exitosa")
                
            } catch (e2: Exception) {
                Log.e("MainActivity", "❌ ERROR en reparación 1", e2)
                
                // Intentar reparación 2: Abrir CrashDiagnosticActivity
                try {
                    Log.d("MainActivity", "🔧 REPARACIÓN 2: Abriendo CrashDiagnosticActivity...")
                    val intent = Intent(this, CrashDiagnosticActivity::class.java)
                    intent.putExtra("error_message", e.message ?: "Error desconocido")
                    intent.putExtra("error_stacktrace", e.stackTraceToString())
                    startActivity(intent)
                    Log.d("MainActivity", "✅ CrashDiagnosticActivity iniciado")
                    finish()
                } catch (e3: Exception) {
                    Log.e("MainActivity", "❌ ERROR CRÍTICO IRRECUPERABLE", e3)
                    Log.e("MainActivity", "   No se pudo abrir CrashDiagnosticActivity")
                    
                    // Último intento: Mostrar error en pantalla
                    setContent {
                        ErrorScreen(
                            errorMessage = "Error crítico: ${e.message}\n\nRevisa Logcat para más detalles."
                        )
                    }
                }
            }
        }
    }
    
    @Composable
    private fun ErrorScreen(errorMessage: String) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFF5252))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "⚠️ Error Crítico",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {
                        // Reintentar
                        recreate()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFFF5252)
                    )
                ) {
                    Text("🔄 Reintentar")
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainApp() {
        val context = LocalContext.current
        val authManager = remember { AuthManager.getInstance(context) }
        
        // Estado de autenticación
        var currentUser by remember { mutableStateOf<User?>(null) }
        var isCheckingAuth by remember { mutableStateOf(true) }
        
        // Verificar si hay sesión activa al iniciar
        LaunchedEffect(Unit) {
            if (authManager.isLoggedIn()) {
                currentUser = authManager.getCurrentUser()
            }
            isCheckingAuth = false
        }
        
        // Si está verificando autenticación, mostrar splash
        if (isCheckingAuth) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }
        
        // Si no hay usuario, mostrar pantalla de autenticación
        if (currentUser == null) {
            AuthenticationFlow(
                onLoginSuccess = { user ->
                    currentUser = user
                }
            )
        } else {
            // Usuario autenticado, mostrar app principal
            AuthenticatedApp(
                currentUser = currentUser!!,
                onLogout = {
                    authManager.logout()
                    currentUser = null
                }
            )
        }
    }
    
    /**
     * Flujo de autenticación (Login y Registro)
     */
    @Composable
    private fun AuthenticationFlow(
        onLoginSuccess: (User) -> Unit
    ) {
        val navController = rememberNavController()
        
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = onLoginSuccess,
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
            
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = onLoginSuccess,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
    
    /**
     * App principal para usuarios autenticados
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AuthenticatedApp(
        currentUser: User,
        onLogout: () -> Unit
    ) {
        val navController = rememberNavController()
        val context = LocalContext.current
        
        // ViewModel con Context usando Factory
        val viewModel: TaskManagerViewModel = viewModel(
            factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return TaskManagerViewModel(context) as T
                }
            }
        )
        
        // Inicializar servicio de calendario
        LaunchedEffect(Unit) {
            viewModel.initializeCalendarService(context)
        }
        
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { 
                        // Abrir galería de zoom
                        startActivity(Intent(this@MainActivity, ImageZoomActivity::class.java))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Ver Galería con Zoom"
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { fullWidth -> fullWidth }
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { fullWidth -> -fullWidth }
                    ) + fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(300),
                        initialOffsetX = { fullWidth -> -fullWidth }
                    ) + fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { fullWidth -> fullWidth }
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                composable("dashboard") {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToTasks = { navController.navigate("tasks") },
                        onNavigateToAddTask = { navController.navigate("add_task") },
                        onNavigateToProfile = { navController.navigate("profile") },
                        onNavigateToReports = { navController.navigate("reports") }
                    )
                }
                
                composable("tasks") {
                    TaskListScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToAddTask = { navController.navigate("add_task") }
                    )
                }
                
                composable("add_task") {
                    AddTaskScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                
                composable("profile") {
                    ProfileScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onLogout = onLogout,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                
                composable("reports") {
                    ReportsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                
                composable("permissions") {
                    PermissionsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
    
    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                label = { Text("Dashboard") },
                selected = false,
                onClick = { navController.navigate("dashboard") }
            )
            NavigationBarItem(
                icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null) },
                label = { Text("Tareas") },
                selected = false,
                onClick = { navController.navigate("tasks") }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                label = { Text("Agregar") },
                selected = false,
                onClick = { navController.navigate("add_task") }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                label = { Text("Perfil") },
                selected = false,
                onClick = { navController.navigate("profile") }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                label = { Text("Reportes") },
                selected = false,
                onClick = { navController.navigate("reports") }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.PermIdentity, contentDescription = null) },
                label = { Text("Permisos") },
                selected = false,
                onClick = { navController.navigate("permissions") }
            )
        }
    }
    
    /**
     * Inicializa la base de datos SQLite
     * Crea el archivo .db y datos iniciales
     */
    private fun initializeDatabase() {
        try {
            // Inicializar BD con datos de ejemplo (cambiar a false en producción)
            val success = DatabaseInitializer.initialize(this, createSampleData = true)
            
            if (success) {
                // Obtener información de la BD
                val dbInfo = DatabaseInitializer.getDatabaseInfo(this)
                
                Log.d("MainActivity", "✅ Base de datos inicializada correctamente")
                Log.d("MainActivity", "📍 Ruta: ${dbInfo.path}")
                Log.d("MainActivity", "📦 Tamaño: ${dbInfo.getSizeInKB()}")
                Log.d("MainActivity", "🔢 Versión: ${dbInfo.version}")
                Log.d("MainActivity", "✔️ Existe: ${dbInfo.exists}")
                
                // Mostrar ruta en consola para que el usuario pueda encontrarla
                println("\n" + "=".repeat(80))
                println("🗄️  BASE DE DATOS CREADA EXITOSAMENTE")
                println("=".repeat(80))
                println("📍 Ubicación del archivo .db:")
                println("   ${dbInfo.path}")
                println("\n💡 Para ver la base de datos:")
                println("   1. Usa Android Studio Database Inspector")
                println("   2. O descarga el archivo desde Device File Explorer")
                println("   3. Abre con DB Browser for SQLite")
                println("=".repeat(80) + "\n")
            } else {
                Log.e("MainActivity", "❌ Error al inicializar la base de datos")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "❌ Error crítico al crear BD", e)
            e.printStackTrace()
        }
    }
}