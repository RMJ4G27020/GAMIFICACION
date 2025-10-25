package com.example.ejercicio2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import com.example.ejercicio2.screens.*
import com.example.ejercicio2.ui.theme.Ejercicio2Theme
import com.example.ejercicio2.viewmodel.TaskManagerViewModel
import androidx.compose.material.icons.automirrored.filled.Assignment

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
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
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainApp() {
        val navController = rememberNavController()
        val viewModel: TaskManagerViewModel = viewModel()
        
        // Inicializar servicio de calendario
        LaunchedEffect(Unit) {
            viewModel.initializeCalendarService(this@MainActivity)
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
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                
                composable("reports") {
                    ReportsScreen(
                        viewModel = viewModel,
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
        }
    }
}