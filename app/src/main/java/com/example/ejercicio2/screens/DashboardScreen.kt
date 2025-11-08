package com.example.ejercicio2.screens

import android.content.Intent
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ejercicio2.DatabaseDebugActivity
import com.example.ejercicio2.data.*
import com.example.ejercicio2.ui.components.*
import com.example.ejercicio2.ui.theme.*
import com.example.ejercicio2.viewmodel.TaskManagerViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TaskManagerViewModel,
    onNavigateToTasks: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAddTask: () -> Unit = {},
    onNavigateToReports: () -> Unit = {}
) {
    val userProfile = viewModel.userProfile
    val pendingTasks = viewModel.getPendingTasks()
    val completedTasks = viewModel.getCompletedTasks()
    val context = LocalContext.current
    
    // Si el usuario aún no se ha cargado, mostrar indicador
    if (userProfile == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddTask,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva Tarea") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header con información del usuario (mejorado)
            item {
                EnhancedUserProfileHeader(
                    userProfile = userProfile,
                    onProfileClick = onNavigateToProfile
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Estadísticas rápidas mejoradas
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "📊 Estadísticas de Hoy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EnhancedQuickStatsSection(
                        pendingCount = pendingTasks.size,
                        completedCount = completedTasks.size,
                        streak = userProfile.currentStreak,
                        totalXP = userProfile.currentXP
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Progreso Semanal
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    WeeklyProgressCard(viewModel = viewModel)
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Sesiones de Estudio
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    StudySessionsSection(viewModel = viewModel)
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Categorías
            item {
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = "📂 Categorías",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CategoriesSection(
                        viewModel = viewModel,
                        onCategoryClick = { onNavigateToTasks() }
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Tareas recientes con nuevo diseño
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📝 Tareas Pendientes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        TextButton(onClick = onNavigateToTasks) {
                            Text("Ver todas")
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            
            // Lista de tareas con el nuevo componente
            items(pendingTasks.take(5)) { task ->
                EnhancedTaskCard(
                    task = task,
                    onClick = { 
                        // Navegación al detalle de tarea (para futura implementación)
                        Log.d("DashboardScreen", "Tarea seleccionada: ${task.title}")
                    },
                    onComplete = { viewModel.completeTask(task) }
                )
            }
            
            if (pendingTasks.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "¡Todo listo! 🎉",
                        description = "No tienes tareas pendientes. ¡Buen trabajo!",
                        icon = Icons.Default.CheckCircle
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
            
            // Botón de Debug de Base de Datos
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    FilledTonalButton(
                        onClick = {
                            context.startActivity(Intent(context, DatabaseDebugActivity::class.java))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFFF9800)
                        )
                    ) {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🗄️ Ver Estado de Base de Datos")
                    }
                }
            }
        }
    }
}

/**
 * Header de perfil de usuario mejorado con gradiente y animaciones
 */
@Composable
private fun EnhancedUserProfileHeader(
    userProfile: User,
    onProfileClick: () -> Unit
) {
    val xpProgress = (userProfile.currentXP % 100) / 100f
    val animatedProgress by animateFloatAsState(
        targetValue = xpProgress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "xpProgress"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onProfileClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar con borde animado
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Hola, ${userProfile.name}!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Nivel
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = GoldColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Nivel ${userProfile.level}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // XP
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ExperienceColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${userProfile.currentXP} XP",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Barra de progreso de XP mejorada
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progreso al nivel ${userProfile.level + 1}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "${(animatedProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        AnimatedProgressBar(
                            progress = animatedProgress,
                            color = AccentYellow,
                            backgroundColor = Color.White.copy(alpha = 0.3f),
                            height = 10.dp
                        )
                    }
                }
                
                // Streak indicator
                if (userProfile.currentStreak > 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Whatshot,
                            contentDescription = "Racha",
                            tint = AccentOrange,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "${userProfile.currentStreak}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "días",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Sección de estadísticas rápidas mejorada con tarjetas animadas
 */
@Composable
private fun EnhancedQuickStatsSection(
    pendingCount: Int,
    completedCount: Int,
    streak: Int,
    totalXP: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Pendientes",
            value = pendingCount.toString(),
            icon = Icons.Default.PendingActions,
            color = TaskPending,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = "Completadas",
            value = completedCount.toString(),
            icon = Icons.Default.CheckCircle,
            color = TaskComplete,
            modifier = Modifier.weight(1f)
        )
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Racha Actual",
            value = "${streak} días",
            icon = Icons.Default.Whatshot,
            color = AccentOrange,
            modifier = Modifier.weight(1f),
            subtitle = "¡Sigue así!"
        )
        
        StatCard(
            title = "XP Total",
            value = totalXP.toString(),
            icon = Icons.Default.Star,
            color = ExperienceColor,
            modifier = Modifier.weight(1f),
            subtitle = "Ganados"
        )
    }
}

/**
 * Tarjeta de progreso semanal
 */
@Composable
private fun WeeklyProgressCard(viewModel: TaskManagerViewModel) {
    val completedThisWeek = viewModel.getCompletedTasks().filter { 
        // Simple filter - idealmente usar fechas reales
        true
    }.size
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📈 Progreso Semanal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "$completedThisWeek tareas completadas esta semana",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = SecondaryGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Simulación de progreso semanal
            AnimatedProgressBar(
                progress = (completedThisWeek / 20f).coerceIn(0f, 1f),
                color = SecondaryGreen,
                height = 12.dp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Meta semanal: 20 tareas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Tarjeta de estado vacío
 */
@Composable
private fun EmptyStateCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Sección de categorías con scroll horizontal
 */
@Composable
private fun CategoriesSection(
    viewModel: TaskManagerViewModel,
    onCategoryClick: (TaskCategory) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(TaskCategory.values()) { category ->
            val taskCount = viewModel.getTasksByCategory(category).size
            EnhancedCategoryCard(
                category = category,
                taskCount = taskCount,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

/**
 * Tarjeta de categoría mejorada
 */
@Composable
private fun EnhancedCategoryCard(
    category: TaskCategory,
    taskCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, category.color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono con fondo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(category.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category.icon,
                    contentDescription = category.displayName,
                    tint = category.color,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = category.color.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "$taskCount tarea${if (taskCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = category.color,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Sección de sesiones de estudio mejorada
 */
@Composable
fun StudySessionsSection(viewModel: TaskManagerViewModel) {
    var showScheduleDialog by remember { mutableStateOf(false) }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📅 Sesiones de Estudio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            FilledTonalButton(
                onClick = { showScheduleDialog = true },
                modifier = Modifier.height(40.dp)
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "Programar",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Programar", style = MaterialTheme.typography.labelLarge)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Indicador de estado del calendario
        if (viewModel.hasCalendarPermissions()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = StudyColor.copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, StudyColor.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Conectado",
                        tint = StudyColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "✅ Calendario conectado",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StudyColor
                        )
                        Text(
                            text = "Las sesiones se programarán automáticamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF9800).copy(alpha = 0.15f)
                ),
                border = BorderStroke(1.dp, Color(0xFFFF9800).copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Sin permisos",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚠️ Permisos necesarios",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )
                        Text(
                            text = "Permite el acceso al calendario para programar sesiones",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    
    // Dialog para programar sesión
    if (showScheduleDialog) {
        QuickScheduleDialog(
            onDismiss = { showScheduleDialog = false },
            onSchedule = { subject, minutes ->
                val calendar = Calendar.getInstance().apply {
                    add(Calendar.HOUR_OF_DAY, 1)
                }
                viewModel.scheduleStudySession(
                    subject = subject,
                    description = "Sesión de estudio programada desde la app",
                    dateTime = calendar,
                    durationMinutes = minutes
                )
                showScheduleDialog = false
            }
        )
    }
}

/**
 * Dialog mejorado para programar sesiones rápidamente
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickScheduleDialog(
    onDismiss: () -> Unit,
    onSchedule: (String, Int) -> Unit
) {
    var selectedSubject by remember { mutableStateOf("Matemáticas") }
    var selectedDuration by remember { mutableStateOf(60) }
    
    val subjects = listOf("Matemáticas", "Historia", "Ciencias", "Literatura", "Idiomas", "Programación")
    val durations = listOf(30, 60, 90, 120)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                "📚 Programar Sesión de Estudio",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Materia:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { subject ->
                        FilterChip(
                            onClick = { selectedSubject = subject },
                            label = { Text(subject) },
                            selected = selectedSubject == subject,
                            leadingIcon = if (selectedSubject == subject) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Duración (minutos):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(durations) { duration ->
                        FilterChip(
                            onClick = { selectedDuration = duration },
                            label = { Text("${duration}min") },
                            selected = selectedDuration == duration,
                            leadingIcon = if (selectedDuration == duration) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "La sesión se programará para dentro de 1 hora",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSchedule(selectedSubject, selectedDuration) }
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Programar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
