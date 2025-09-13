package com.example.ejercicio2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejercicio2.data.*
import com.example.ejercicio2.ui.theme.*
import com.example.ejercicio2.viewmodel.TaskManagerViewModel

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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        // Header con información del usuario
        UserProfileHeader(
            userProfile = userProfile,
            onProfileClick = onNavigateToProfile
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Estadísticas rápidas
        QuickStatsSection(
            pendingCount = pendingTasks.size,
            completedCount = completedTasks.size,
            streak = userProfile.currentStreak
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Categorías
        Text(
            text = "Categorías",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        CategoriesSection(
            viewModel = viewModel,
            onCategoryClick = { onNavigateToTasks() }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Tareas recientes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tareas Recientes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            TextButton(onClick = onNavigateToTasks) {
                Text("Ver todas")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Lista de tareas recientes
        LazyColumn {
            items(pendingTasks.take(3)) { task ->
                TaskItemCompact(
                    task = task,
                    onComplete = { viewModel.completeTask(task) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Botón flotante para agregar tarea
        FloatingActionButton(
            onClick = onNavigateToAddTask,
            containerColor = PrimaryBlue,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Agregar tarea",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun UserProfileHeader(
    userProfile: User,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() },
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "¡Hola, ${userProfile.name}!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = "Nivel ${userProfile.level} • ${userProfile.currentXP} XP",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                // Barra de progreso de XP
                Column(modifier = Modifier.fillMaxWidth()) {
                    // ... (código existente)
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    LinearProgressIndicator(
                        progress = { (userProfile.currentXP % 100) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = AccentYellow,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
            
            // Icono de estrella de streak
            if (userProfile.currentStreak > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Whatshot,
                        contentDescription = "Racha",
                        tint = AccentOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${userProfile.currentStreak}",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsSection(
    pendingCount: Int,
    completedCount: Int,
    streak: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            title = "Pendientes",
            value = pendingCount.toString(),
            color = TaskPending,
            icon = Icons.Default.PendingActions,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = "Completadas",
            value = completedCount.toString(), 
            color = TaskComplete,
            icon = Icons.Default.CheckCircle,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            title = "Racha",
            value = "${streak} días",
            color = AccentOrange,
            icon = Icons.Default.Whatshot,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun CategoriesSection(
    viewModel: TaskManagerViewModel,
    onCategoryClick: (TaskCategory) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(TaskCategory.values()) { category ->
            val taskCount = viewModel.getTasksByCategory(category).size
            CategoryCard(
                category = category,
                taskCount = taskCount,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: TaskCategory,
    taskCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = category.color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                category.icon,
                contentDescription = category.displayName,
                tint = category.color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = category.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            
            Text(
                text = "$taskCount tareas",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TaskItemCompact(
    task: Task,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                task.category.icon,
                contentDescription = task.category.displayName,
                tint = task.category.color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
            
            IconButton(
                onClick = { onComplete() }
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Completar",
                    tint = TaskComplete
                )
            }
        }
    }
}
