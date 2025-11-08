package com.example.ejercicio2.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import com.example.ejercicio2.data.*
import com.example.ejercicio2.database.TaskRepository
import com.example.ejercicio2.database.DatabaseHelper
import com.example.ejercicio2.calendar.CalendarService
import android.content.Context
import java.time.LocalDate
import java.util.*

class TaskManagerViewModel(context: Context) : ViewModel() {
    
    // Repository para acceso a base de datos
    private val repository: TaskRepository = TaskRepository(DatabaseHelper.getInstance(context))
    
    private var calendarService: CalendarService? = null
    
    // Estado observable de tareas (cargado desde DB)
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> get() = _tasks
    
    // Perfil del usuario (cargado desde DB)
    private val _userProfile = mutableStateOf<User?>(null)
    val userProfile: User? get() = _userProfile.value
    
    init {
        // Cargar usuario desde base de datos
        loadUser()
        
        // Si no existe usuario, crear uno por defecto
        if (_userProfile.value == null) {
            val defaultUser = User(
                id = UUID.randomUUID().toString(),
                name = "Estudiante",
                email = "estudiante@ejemplo.com",
                currentXP = 0,
                level = 1,
                badges = emptyList(),
                currentStreak = 0,
                tasksCompleted = 0
            )
            repository.upsertUser(defaultUser)
            _userProfile.value = defaultUser
        }
        
        // Cargar tareas desde base de datos
        loadTasks()
        
        // Si no hay tareas, crear algunas de ejemplo
        if (_tasks.isEmpty()) {
            createSampleTasks()
        }
    }
    
    /**
     * Cargar usuario desde base de datos
     */
    private fun loadUser() {
        _userProfile.value = repository.getMainUser()
    }
    
    /**
     * Cargar tareas desde base de datos
     */
    private fun loadTasks() {
        _tasks.clear()
        _tasks.addAll(repository.getAllTasks())
    }
    
    /**
     * Crear tareas de ejemplo (primera vez)
     */
    @SuppressLint("NewApi")
    private fun createSampleTasks() {
        val sampleTasks = listOf(
            Task(
                id = UUID.randomUUID().toString(),
                title = "Estudiar Matemáticas",
                description = "Revisar capítulo de álgebra lineal",
                category = TaskCategory.MATHEMATICS,
                priority = TaskPriority.HIGH,
                status = TaskStatus.PENDING,
                dueDate = LocalDate.now().plusDays(2),
                xpReward = 50
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Ensayo de Historia",
                description = "Escribir ensayo sobre la Segunda Guerra Mundial",
                category = TaskCategory.HISTORY,
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.IN_PROGRESS,
                dueDate = LocalDate.now().plusDays(5),
                xpReward = 75
            ),
            Task(
                id = UUID.randomUUID().toString(),
                title = "Laboratorio de Química",
                description = "Completar reporte de experimento",
                category = TaskCategory.SCIENCE,
                priority = TaskPriority.HIGH,
                status = TaskStatus.COMPLETED,
                dueDate = LocalDate.now().minusDays(1),
                xpReward = 60
            )
        )
        
        sampleTasks.forEach { task ->
            repository.insertTask(task)
        }
        
        loadTasks() // Recargar desde DB
    }
    
    
    /**
     * Inicializa el servicio de calendario
     */
    fun initializeCalendarService(context: Context) {
        calendarService = CalendarService(context)
    }
    
    /**
     * Agregar nueva tarea (guarda en DB)
     */
    fun addTask(task: Task) {
        val newTask = task.copy(id = UUID.randomUUID().toString())
        
        // Guardar en base de datos
        repository.insertTask(newTask)
        
        // Actualizar lista en memoria
        _tasks.add(newTask)
        
        // Programar en calendario si hay fecha límite y permisos
        calendarService?.let { calendar ->
            if (calendar.hasCalendarPermissions()) {
                val deadlineCalendar = Calendar.getInstance().apply {
                    val localDate = newTask.dueDate
                    set(localDate.year, localDate.monthValue - 1, localDate.dayOfMonth, 9, 0, 0)
                }
                calendar.scheduleTaskDeadline(
                    taskTitle = newTask.title,
                    description = newTask.description,
                    deadlineDate = deadlineCalendar
                )
            }
        }
    }
    
    /**
     * Completar tarea (actualiza en DB)
     */
    fun completeTask(task: Task) {
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            val completedTask = task.copy(
                status = TaskStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )
            
            // Actualizar en base de datos
            repository.updateTask(completedTask)
            
            // Actualizar XP del usuario en DB
            _userProfile.value?.let { user ->
                repository.updateUserXP(user.id, task.xpReward)
                repository.incrementTasksCompleted(user.id)
                
                // Recargar usuario para obtener nivel actualizado
                loadUser()
            }
            
            // Actualizar lista en memoria
            _tasks[index] = completedTask
        }
    }
    
    /**
     * Actualizar tarea (actualiza en DB)
     */
    fun updateTask(task: Task) {
        val index = _tasks.indexOfFirst { it.id == task.id }
        if (index >= 0) {
            // Actualizar en base de datos
            repository.updateTask(task)
            
            // Actualizar lista en memoria
            _tasks[index] = task
            
            // Si se completó la tarea, agregar XP
            if (task.status == TaskStatus.COMPLETED) {
                _userProfile.value?.let { user ->
                    repository.updateUserXP(user.id, task.xpReward)
                    repository.incrementTasksCompleted(user.id)
                    loadUser()
                }
            }
        }
    }
    
    /**
     * Eliminar tarea (elimina de DB)
     */
    fun deleteTask(taskId: String) {
        // Eliminar de base de datos
        repository.deleteTask(taskId)
        
        // Eliminar de lista en memoria
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
    
    /**
     * Programa una sesión de estudio en el calendario
     */
    fun scheduleStudySession(
        subject: String,
        description: String,
        dateTime: Calendar,
        durationMinutes: Int = 90
    ): Boolean {
        return calendarService?.scheduleStudySession(
            subject, description, dateTime, durationMinutes
        ) ?: false
    }
    
    /**
     * Verifica si hay permisos de calendario
     */
    fun hasCalendarPermissions(): Boolean {
        return calendarService?.hasCalendarPermissions() ?: false
    }
}