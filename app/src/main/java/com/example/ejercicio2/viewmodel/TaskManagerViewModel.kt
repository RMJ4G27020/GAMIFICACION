package com.example.ejercicio2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import com.example.ejercicio2.data.*
import java.time.LocalDate

class TaskManagerViewModel : ViewModel() {
    
    // Lista de tareas
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks
    
    // Perfil del usuario  
    private val _userProfile = mutableStateOf(
        User(
            id = "user1",
            name = "Estudiante",
            email = "estudiante@ejemplo.com",
            currentXP = 150,
            level = 3,
            badges = listOf(
                Badges.FIRST_TASK,
                Badges.STREAK_3
            ),
            currentStreak = 5,
            tasksCompleted = 12
        )
    )
    val userProfile: User get() = _userProfile.value
    
    init {
        // Tareas de ejemplo
        _tasks.addAll(listOf(
            Task(
                id = "1",
                title = "Estudiar Matemáticas",
                description = "Revisar capítulo de álgebra lineal",
                category = TaskCategory.MATHEMATICS,
                priority = TaskPriority.HIGH,
                status = TaskStatus.PENDING,
                dueDate = LocalDate.now().plusDays(2),
                xpReward = 50
            ),
            Task(
                id = "2", 
                title = "Ensayo de Historia",
                description = "Escribir ensayo sobre la Segunda Guerra Mundial",
                category = TaskCategory.HISTORY,
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.IN_PROGRESS,
                dueDate = LocalDate.now().plusDays(5),
                xpReward = 75
            ),
            Task(
                id = "3",
                title = "Laboratorio de Química",
                description = "Completar reporte de experimento",
                category = TaskCategory.SCIENCE,
                priority = TaskPriority.HIGH,
                status = TaskStatus.COMPLETED,
                dueDate = LocalDate.now().minusDays(1),
                xpReward = 60
            )
        ))
    }
    
    fun addTask(task: Task) {
        _tasks.add(task.copy(id = (_tasks.size + 1).toString()))
    }
    
    fun completeTask(task: Task) {
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            _tasks[index] = task.copy(status = TaskStatus.COMPLETED)
            // Actualizar XP del usuario
            _userProfile.value = _userProfile.value.copy(
                currentXP = _userProfile.value.currentXP + task.xpReward,
                tasksCompleted = _userProfile.value.tasksCompleted + 1
            )
        }
    }
    
    fun updateTask(task: Task) {
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index >= 0) {
            _tasks[index] = task
            
            // Si se completó la tarea, agregar XP
            if (task.status == TaskStatus.COMPLETED) {
                val currentUser = _userProfile.value
                _userProfile.value = currentUser.copy(
                    currentXP = currentUser.currentXP + task.xpReward,
                    tasksCompleted = currentUser.tasksCompleted + 1
                )
            }
        }
    }
    
    fun deleteTask(taskId: String) {
        _tasks.removeIf { it.id == taskId }
    }
    
    fun getPendingTasks(): List<Task> = _tasks.filter { it.status == TaskStatus.PENDING }
    
    fun getCompletedTasks(): List<Task> = _tasks.filter { it.status == TaskStatus.COMPLETED }
    
    fun getTasksByStatus(status: TaskStatus): List<Task> {
        return _tasks.filter { it.status == status }
    }
    
    fun getTasksByCategory(category: TaskCategory): List<Task> {
        return _tasks.filter { it.category == category }
    }
}