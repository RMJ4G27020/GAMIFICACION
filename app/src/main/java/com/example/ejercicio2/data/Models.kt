package com.example.ejercicio2.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ejercicio2.ui.theme.*

enum class TaskCategory(
    val displayName: String,
    val color: androidx.compose.ui.graphics.Color,
    val icon: ImageVector
) {
    STUDY("Estudios", StudyColor, Icons.Default.MenuBook),
    EXERCISE("Ejercicio", ExerciseColor, Icons.Default.FitnessCenter),
    SOCIAL("Social", SocialColor, Icons.Default.People),
    WORK("Trabajo", WorkColor, Icons.Default.Work),
    PERSONAL("Personal", PersonalColor, Icons.Default.Person)
}

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE
}

data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val category: TaskCategory,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val imageProof: String? = null,
    val experiencePoints: Int = 10
)

data class UserProfile(
    val name: String = "Estudiante",
    val level: Int = 1,
    val currentExp: Int = 0,
    val totalExp: Int = 0,
    val completedTasks: Int = 0,
    val streak: Int = 0,
    val badges: List<Badge> = emptyList()
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val unlockedAt: Long? = null
)

// Badges predefinidas
object BadgeSystem {
    val FIRST_TASK = Badge(
        id = "first_task",
        name = "Primer Paso",
        description = "Completaste tu primera tarea",
        icon = Icons.Default.Star
    )
    
    val WEEK_STREAK = Badge(
        id = "week_streak", 
        name = "Constancia",
        description = "7 días consecutivos completando tareas",
        icon = Icons.Default.Whatshot
    )
    
    val STUDY_MASTER = Badge(
        id = "study_master",
        name = "Maestro del Estudio",
        description = "10 tareas de estudio completadas",
        icon = Icons.Default.School
    )
}
