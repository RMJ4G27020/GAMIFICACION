package com.example.ejercicio2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejercicio2.data.*
import com.example.ejercicio2.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: TaskManagerViewModel
) {
    val userProfile = viewModel.userProfile
    val completedTasks = viewModel.getCompletedTasks()
    val pendingTasks = viewModel.getPendingTasks()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Text(
                text = "Reportes y Estadísticas",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        
        item {
            // Resumen general
            GeneralSummaryCard(
                completedTasks = completedTasks.size,
                pendingTasks = pendingTasks.size,
                totalExp = userProfile.totalExp,
                level = userProfile.level,
                streak = userProfile.streak
            )
        }
        
        item {
            // Progreso por categorías
            CategoryStatsCard(viewModel)
        }
        
        item {
            // Actividad reciente
            RecentActivityCard(completedTasks)
        }
        
        item {
            // Motivación y próximos objetivos
            MotivationCard(userProfile)
        }
    }
}

@Composable
private fun GeneralSummaryCard(
    completedTasks: Int,
    pendingTasks: Int,
    totalExp: Int,
    level: Int,
    streak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Resumen General",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    icon = Icons.Default.CheckCircle,
                    value = completedTasks.toString(),
                    label = "Completadas",
                    color = Color.White
                )
                
                SummaryItem(
                    icon = Icons.Default.PendingActions,
                    value = pendingTasks.toString(),
                    label = "Pendientes",
                    color = Color.White
                )
                
                SummaryItem(
                    icon = Icons.Default.EmojiEvents,
                    value = totalExp.toString(),
                    label = "XP Total",
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(color = Color.White.copy(alpha = 0.3f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    icon = Icons.Default.TrendingUp,
                    value = "Nivel $level",
                    label = "Actual",
                    color = Color.White
                )
                
                SummaryItem(
                    icon = Icons.Default.Whatshot,
                    value = "$streak días",
                    label = "Racha",
                    color = Color.White
                )
                
                val completionRate = if (completedTasks + pendingTasks > 0) {
                    (completedTasks * 100) / (completedTasks + pendingTasks)
                } else {
                    0
                }
                
                SummaryItem(
                    icon = Icons.Default.Percent,
                    value = "$completionRate%",
                    label = "Completado",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = color.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun CategoryStatsCard(viewModel: TaskManagerViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Progreso por Categorías",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TaskCategory.values().forEach { category ->
                val categoryTasks = viewModel.getTasksByCategory(category)
                val completedTasks = categoryTasks.filter { it.status == TaskStatus.COMPLETED }
                val totalXP = completedTasks.sumOf { it.experiencePoints }
                
                CategoryStatItem(
                    category = category,
                    completed = completedTasks.size,
                    total = categoryTasks.size,
                    totalXP = totalXP
                )
                
                if (category != TaskCategory.values().last()) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun CategoryStatItem(
    category: TaskCategory,
    completed: Int,
    total: Int,
    totalXP: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                category.icon,
                contentDescription = category.displayName,
                tint = category.color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                
                Text(
                    text = "$totalXP XP ganados",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$completed/$total",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = category.color
                )
                
                Text(
                    text = if (total > 0) "${(completed * 100) / total}%" else "0%",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = if (total > 0) completed.toFloat() / total else 0f,
            modifier = Modifier.fillMaxWidth(),
            color = category.color,
            trackColor = category.color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun RecentActivityCard(completedTasks: List<Task>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Actividad Reciente",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (completedTasks.isEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No hay tareas completadas aún",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            } else {
                completedTasks.sortedByDescending { it.completedAt }.take(5).forEach { task ->
                    RecentActivityItem(task)
                    if (task != completedTasks.last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentActivityItem(task: Task) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            task.category.icon,
            contentDescription = task.category.displayName,
            tint = task.category.color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            
            Text(
                text = "Completada ${formatDate(task.completedAt ?: task.createdAt)}",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Star,
                contentDescription = "XP",
                tint = ExperienceColor,
                modifier = Modifier.size(16.dp)
            )
            
            Text(
                text = "+${task.experiencePoints}",
                fontSize = 12.sp,
                color = ExperienceColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MotivationCard(userProfile: UserProfile) {
    val nextLevelXP = (userProfile.level * 100)
    val xpNeeded = nextLevelXP - userProfile.currentExp
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SecondaryGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = "Motivación",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "¡Sigue así!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = when {
                    userProfile.streak >= 7 -> "¡Increíble racha de ${userProfile.streak} días! Eres un verdadero campeón."
                    userProfile.completedTasks >= 10 -> "¡${userProfile.completedTasks} tareas completadas! Tu dedicación es admirable."
                    userProfile.level >= 3 -> "¡Nivel ${userProfile.level}! Tu progreso es impresionante."
                    else -> "¡Cada tarea completada te acerca más a tus objetivos!"
                },
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Próximo objetivo: Solo necesitas $xpNeeded XP más para alcanzar el Nivel ${userProfile.level + 1}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
