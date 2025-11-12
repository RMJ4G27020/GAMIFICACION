package com.example.ejercicio2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import com.example.ejercicio2.models.User as AuthUser
import com.example.ejercicio2.ui.theme.*
import com.example.ejercicio2.viewmodel.TaskManagerViewModel
import androidx.compose.material.icons.filled.ExitToApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: TaskManagerViewModel,
    currentUser: AuthUser,
    onLogout: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    val userProfile = viewModel.userProfile
    
    // Si el usuario no está cargado, mostrar indicador
    if (userProfile == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onNavigateBack != null) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                }
                
                Text(
                    text = "Mi Perfil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            // Información del usuario autenticado
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = PrimaryBlueLight,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Nombre
                    Text(
                        text = currentUser.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Email
                    Text(
                        text = currentUser.email,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Botón de Logout
                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cerrar Sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        item {
            // Tarjeta de perfil principal
            ProfileCard(userProfile)
        }
        
        item {
            // Estadísticas detalladas
            StatsSection(viewModel)
        }
        
        item {
            // Logros y badges
            AchievementsSection(userProfile)
        }
        
        item {
            // Progreso por categoría
            CategoryProgressSection(viewModel)
        }
    }
}

@Composable
private fun ProfileCard(userProfile: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Nombre
            Text(
                text = userProfile.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Nivel y XP
            Text(
                text = "Nivel ${userProfile.level}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Barra de progreso XP
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${userProfile.currentXP} XP",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${userProfile.level * 100} XP",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                LinearProgressIndicator(
                    progress = { (userProfile.currentXP.toFloat() / (userProfile.level * 100).toFloat()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = AccentYellow,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Estadísticas rápidas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(
                    icon = Icons.Default.EmojiEvents,
                    value = "${userProfile.currentXP}",
                    label = "XP Total"
                )
                
                ProfileStat(
                    icon = Icons.Default.CheckCircle,
                    value = "${userProfile.tasksCompleted}",
                    label = "Completadas"
                )
                
                ProfileStat(
                    icon = Icons.Default.Whatshot,
                    value = "${userProfile.currentStreak}",
                    label = "Racha"
                )
            }
        }
    }
}

@Composable
private fun ProfileStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun StatsSection(viewModel: TaskManagerViewModel) {
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
                text = "Estadísticas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val pendingTasks = viewModel.getPendingTasks().size
            val completedTasks = viewModel.getCompletedTasks().size
            val totalTasks = pendingTasks + completedTasks
            
            StatRow(
                label = "Tareas totales",
                value = totalTasks.toString(),
                icon = Icons.AutoMirrored.Filled.Assignment,
                color = PrimaryBlue
            )
            
            StatRow(
                label = "Pendientes",
                value = pendingTasks.toString(),
                icon = Icons.Default.PendingActions,
                color = TaskPending
            )
            
            StatRow(
                label = "Completadas",
                value = completedTasks.toString(),
                icon = Icons.Default.CheckCircle,
                color = TaskComplete
            )
            
            if (totalTasks > 0) {
                StatRow(
                    label = "Tasa de finalización",
                    value = "${(completedTasks * 100) / totalTasks}%",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = SecondaryGreen
                )
            }
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun AchievementsSection(userProfile: User) {
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
                text = "Logros",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (userProfile.badges.isEmpty()) {
                Text(
                    text = "Aún no has desbloqueado ningún logro. ¡Sigue completando tareas!",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(userProfile.badges) { badge ->
                        BadgeItem(badge)
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(badge: Badge) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GoldColor.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                badge.icon,
                contentDescription = badge.name,
                tint = GoldColor,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = badge.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun CategoryProgressSection(viewModel: TaskManagerViewModel) {
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
                text = "Progreso por Categoría",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TaskCategory.values().forEach { category ->
                val categoryTasks = viewModel.getTasksByCategory(category)
                val completedTasks = categoryTasks.filter { it.status == TaskStatus.COMPLETED }
                
                CategoryProgressItem(
                    category = category,
                    completed = completedTasks.size,
                    total = categoryTasks.size
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun CategoryProgressItem(
    category: TaskCategory,
    completed: Int,
    total: Int
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
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = category.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "$completed/$total",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { if (total > 0) completed.toFloat() / total else 0f },
            modifier = Modifier.fillMaxWidth(),
            color = category.color,
            trackColor = category.color.copy(alpha = 0.2f)
        )
    }
}
