package com.example.ejercicio2.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.util.UUID

class TaskManagerViewModel : ViewModel() {
    
    var userProfile by mutableStateOf(UserProfile())
        private set
    
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> = _tasks
    
    // Tareas de ejemplo
    init {
        addSampleTasks()
    }
    
    fun addTask(
        title: String,
        description: String,
        category: TaskCategory
    ) {
        val newTask = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            category = category
        )
        _tasks.add(0, newTask)
    }
    
    fun completeTask(taskId: String, imageProof: String? = null) {
        val taskIndex = _tasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val task = _tasks[taskIndex]
            val completedTask = task.copy(
                status = TaskStatus.COMPLETED,
                completedAt = System.currentTimeMillis(),
                imageProof = imageProof
            )
            _tasks[taskIndex] = completedTask
            
            // Actualizar perfil del usuario
            updateUserProfile(task.experiencePoints)
        }
    }
    
    fun deleteTask(taskId: String) {
        _tasks.removeIf { it.id == taskId }
    }
    
    private fun updateUserProfile(expGained: Int) {
        val newTotalExp = userProfile.totalExp + expGained
        val newCurrentExp = userProfile.currentExp + expGained
        val newLevel = calculateLevel(newTotalExp)
        val newCompletedTasks = userProfile.completedTasks + 1
        
        userProfile = userProfile.copy(
            currentExp = if (newLevel > userProfile.level) 0 else newCurrentExp,
            totalExp = newTotalExp,
            level = newLevel,
            completedTasks = newCompletedTasks,
            streak = calculateStreak()
        )
    }
    
    private fun calculateLevel(totalExp: Int): Int {
        return (totalExp / 100) + 1
    }
    
    private fun calculateStreak(): Int {
        // Lógica simple de streak - se puede mejorar
        val today = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000
        
        val completedToday = _tasks.any { task ->
            task.status == TaskStatus.COMPLETED &&
            task.completedAt != null &&
            (today - task.completedAt!!) < oneDayInMillis
        }
        
        return if (completedToday) userProfile.streak + 1 else userProfile.streak
    }
    
    fun getTasksByCategory(category: TaskCategory): List<Task> {
        return _tasks.filter { it.category == category }
    }
    
    fun getCompletedTasks(): List<Task> {
        return _tasks.filter { it.status == TaskStatus.COMPLETED }
    }
    
    fun getPendingTasks(): List<Task> {
        return _tasks.filter { it.status == TaskStatus.PENDING }
    }
    
    private fun addSampleTasks() {
        _tasks.addAll(
            listOf(
                Task(
                    id = "1",
                    title = "Estudiar Matemáticas",
                    description = "Resolver ejercicios del capítulo 5",
                    category = TaskCategory.STUDY,
                    experiencePoints = 15
                ),
                Task(
                    id = "2", 
                    title = "Ejercicio matutino",
                    description = "30 minutos de cardio",
                    category = TaskCategory.EXERCISE,
                    experiencePoints = 20
                ),
                Task(
                    id = "3",
                    title = "Proyecto de programación",
                    description = "Implementar nueva funcionalidad",
                    category = TaskCategory.WORK,
                    experiencePoints = 25
                )
            )
        )
    }
}
