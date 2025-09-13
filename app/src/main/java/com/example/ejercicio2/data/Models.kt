package com.example.ejercicio2.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ejercicio2.ui.theme.*

enum class TaskCategory(
    val displayName: String,
    val color: androidx.compose.ui.graphics.Color,
    val icon: ImageVector
) {
    STUDY("Estudios", StudyColor, Icons.AutoMirrored.Filled.MenuBook),
    MATHEMATICS("Matemáticas", StudyColor, Icons.Default.Calculate),
    HISTORY("Historia", StudyColor, Icons.Default.HistoryEdu),
    SCIENCE("Ciencias", StudyColor, Icons.Default.Science),
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

enum class TaskPriority(
    val displayName: String,
    val color: androidx.compose.ui.graphics.Color
) {
    LOW("Baja", androidx.compose.ui.graphics.Color.Green),
    MEDIUM("Media", androidx.compose.ui.graphics.Color.Yellow),
    HIGH("Alta", androidx.compose.ui.graphics.Color.Red)
}

data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val category: TaskCategory,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val dueDate: java.time.LocalDate = java.time.LocalDate.now(),
    val xpReward: Int = 10,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val imageProof: String? = null
)

data class User(
    val id: String,
    val name: String = "Estudiante",
    val email: String = "",
    val level: Int = 1,
    val currentXP: Int = 0,
    val currentStreak: Int = 0,
    val tasksCompleted: Int = 0,
    val badges: List<Badge> = emptyList()
)

// Para compatibilidad, también mantenemos UserProfile
typealias UserProfile = User

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val unlockedAt: Long? = null
)

// Nuevo modelo para información detallada de imágenes
data class ImageInfo(
    val id: Int,
    val title: String,
    val description: String,
    val details: String,
    val tips: String,
    val resourceName: String
)

// Badges predefinidas
object Badges {
    val FIRST_TASK = Badge(
        id = "first_task",
        name = "Primer Paso",
        description = "Completaste tu primera tarea",
        icon = Icons.Default.Star
    )
    
    val STREAK_3 = Badge(
        id = "streak_3", 
        name = "Constancia",
        description = "3 días consecutivos completando tareas",
        icon = Icons.Default.Whatshot
    )
    
    val STUDY_MASTER = Badge(
        id = "study_master",
        name = "Maestro del Estudio",
        description = "10 tareas de estudio completadas",
        icon = Icons.Default.School
    )
}
