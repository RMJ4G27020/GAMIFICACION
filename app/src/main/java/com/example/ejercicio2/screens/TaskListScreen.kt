package com.example.ejercicio2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejercicio2.data.*
import com.example.ejercicio2.ui.theme.*
import com.example.ejercicio2.viewmodel.TaskManagerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskManagerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddTask: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(TaskFilter.ALL) }
    
    val filteredTasks = when (selectedFilter) {
        TaskFilter.ALL -> viewModel.tasks
        TaskFilter.PENDING -> viewModel.getPendingTasks()
        TaskFilter.COMPLETED -> viewModel.getCompletedTasks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextPrimary
                )
            }
            
            Text(
                text = "Mis Tareas",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onNavigateToAddTask) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar tarea",
                    tint = PrimaryBlue
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Filtros
        FilterChips(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Lista de tareas
        if (filteredTasks.isEmpty()) {
            EmptyTasksMessage(selectedFilter)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTasks) { task ->
                    TaskItem(
                        task = task,
                        onComplete = { viewModel.completeTask(task) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }
            }
        }
    }
}

enum class TaskFilter(val displayName: String) {
    ALL("Todas"),
    PENDING("Pendientes"), 
    COMPLETED("Completadas")
}

@Composable
private fun FilterChips(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskFilter.values().forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.status == TaskStatus.COMPLETED) {
                TaskComplete.copy(alpha = 0.1f)
            } else {
                SurfaceLight
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Icono de categoría
                Icon(
                    task.category.icon,
                    contentDescription = task.category.displayName,
                    tint = task.category.color,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Contenido de la tarea
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        textDecoration = if (task.status == TaskStatus.COMPLETED) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                    
                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Información adicional
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Categoría
                        AssistChip(
                            onClick = { },
                            label = { 
                                Text(
                                    text = task.category.displayName,
                                    fontSize = 12.sp
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = task.category.color.copy(alpha = 0.2f),
                                labelColor = task.category.color
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Experiencia
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "XP",
                                tint = ExperienceColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${task.xpReward} XP",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // Fecha
                        Text(
                            text = formatDate(
                                if (task.status == TaskStatus.COMPLETED) {
                                    task.completedAt ?: task.createdAt
                                } else {
                                    task.createdAt
                                }
                            ),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    
                    // Mostrar proof si existe
                    if (task.imageProof != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = "Foto de prueba",
                                tint = TaskComplete,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Completado con foto",
                                fontSize = 12.sp,
                                color = TaskComplete,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                // Acciones
                Column {
                    if (task.status == TaskStatus.PENDING) {
                        IconButton(
                            onClick = onComplete
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Completar",
                                tint = TaskComplete
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = TaskOverdue
                        )
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Estás seguro de que quieres eliminar esta tarea?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = TaskOverdue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun EmptyTasksMessage(filter: TaskFilter) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Assignment,
            contentDescription = "No hay tareas",
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = when (filter) {
                TaskFilter.ALL -> "No hay tareas"
                TaskFilter.PENDING -> "No hay tareas pendientes"
                TaskFilter.COMPLETED -> "No hay tareas completadas"
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = when (filter) {
                TaskFilter.ALL -> "¡Agrega tu primera tarea!"
                TaskFilter.PENDING -> "¡Todas las tareas están completadas!"
                TaskFilter.COMPLETED -> "¡Completa algunas tareas!"
            },
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
