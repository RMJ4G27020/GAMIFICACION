package com.example.ejercicio2.database

import android.content.ContentValues
import android.database.Cursor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import com.example.ejercicio2.data.*
import java.time.LocalDate

/**
 * Repository Pattern - Capa de acceso a datos
 * Maneja todas las operaciones CRUD con la base de datos SQLite
 */
class TaskRepository(private val dbHelper: DatabaseHelper) {

    // ============================================
    // OPERACIONES DE TAREAS (TASKS)
    // ============================================

    /**
     * Insertar nueva tarea
     */

    fun insertTask(task: Task, userId: Int = 1): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_TASK_UUID, task.id)
            put(DatabaseHelper.COL_TASK_USER_ID, userId)
            put(DatabaseHelper.COL_TASK_TITLE, task.title)
            put(DatabaseHelper.COL_TASK_DESCRIPTION, task.description)
            put(DatabaseHelper.COL_TASK_CATEGORY, task.category.name)
            put(DatabaseHelper.COL_TASK_PRIORITY, task.priority.name)
            put(DatabaseHelper.COL_TASK_STATUS, task.status.name)
            put(DatabaseHelper.COL_TASK_DUE_DATE, task.dueDate.toString())
            put(DatabaseHelper.COL_TASK_XP_REWARD, task.xpReward)
            put(DatabaseHelper.COL_TASK_IMAGE_PATH, task.imageProof)
            task.completedAt?.let {
                put(DatabaseHelper.COL_TASK_COMPLETED_AT, it)
            }
        }
        
        return db.insert(DatabaseHelper.TABLE_TASKS, null, values)
    }

    /**
     * Actualizar tarea existente
     */

    fun updateTask(task: Task): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_TASK_TITLE, task.title)
            put(DatabaseHelper.COL_TASK_DESCRIPTION, task.description)
            put(DatabaseHelper.COL_TASK_CATEGORY, task.category.name)
            put(DatabaseHelper.COL_TASK_PRIORITY, task.priority.name)
            put(DatabaseHelper.COL_TASK_STATUS, task.status.name)
            put(DatabaseHelper.COL_TASK_DUE_DATE, task.dueDate.toString())
            put(DatabaseHelper.COL_TASK_XP_REWARD, task.xpReward)
            put(DatabaseHelper.COL_TASK_IMAGE_PATH, task.imageProof)
            task.completedAt?.let {
                put(DatabaseHelper.COL_TASK_COMPLETED_AT, it)
            }
        }
        
        return db.update(
            DatabaseHelper.TABLE_TASKS,
            values,
            "${DatabaseHelper.COL_TASK_UUID} = ?",
            arrayOf(task.id)
        )
    }

    /**
     * Eliminar tarea
     */
    fun deleteTask(taskId: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_TASKS,
            "${DatabaseHelper.COL_TASK_UUID} = ?",
            arrayOf(taskId)
        )
    }

    /**
     * Obtener todas las tareas de un usuario
     */

    fun getAllTasks(userId: Int = 1): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = dbHelper.readableDatabase
        
        val cursor = db.query(
            DatabaseHelper.TABLE_TASKS,
            null,
            "${DatabaseHelper.COL_TASK_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "${DatabaseHelper.COL_TASK_DUE_DATE} ASC"
        )
        
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    tasks.add(cursorToTask(it))
                } while (it.moveToNext())
            }
        }
        
        return tasks
    }

    /**
     * Obtener tareas por estado
     */

    fun getTasksByStatus(status: TaskStatus, userId: Int = 1): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = dbHelper.readableDatabase
        
        val cursor = db.query(
            DatabaseHelper.TABLE_TASKS,
            null,
            "${DatabaseHelper.COL_TASK_USER_ID} = ? AND ${DatabaseHelper.COL_TASK_STATUS} = ?",
            arrayOf(userId.toString(), status.name),
            null,
            null,
            "${DatabaseHelper.COL_TASK_DUE_DATE} ASC"
        )
        
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    tasks.add(cursorToTask(it))
                } while (it.moveToNext())
            }
        }
        
        return tasks
    }

    /**
     * Obtener tareas por categoría
     */

    fun getTasksByCategory(category: TaskCategory, userId: Int = 1): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = dbHelper.readableDatabase
        
        val cursor = db.query(
            DatabaseHelper.TABLE_TASKS,
            null,
            "${DatabaseHelper.COL_TASK_USER_ID} = ? AND ${DatabaseHelper.COL_TASK_CATEGORY} = ?",
            arrayOf(userId.toString(), category.name),
            null,
            null,
            "${DatabaseHelper.COL_TASK_DUE_DATE} ASC"
        )
        
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    tasks.add(cursorToTask(it))
                } while (it.moveToNext())
            }
        }
        
        return tasks
    }

    /**
     * Marcar tarea como completada
     */
    fun completeTask(taskId: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_TASK_STATUS, TaskStatus.COMPLETED.name)
            put(DatabaseHelper.COL_TASK_COMPLETED_AT, System.currentTimeMillis())
        }
        
        val rows = db.update(
            DatabaseHelper.TABLE_TASKS,
            values,
            "${DatabaseHelper.COL_TASK_UUID} = ?",
            arrayOf(taskId)
        )
        
        return rows > 0
    }

    // ============================================
    // OPERACIONES DE USUARIO (USER)
    // ============================================

    /**
     * Insertar o actualizar usuario
     */
    fun upsertUser(user: User): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_USER_UUID, user.id)
            put(DatabaseHelper.COL_USER_NAME, user.name)
            put(DatabaseHelper.COL_USER_EMAIL, user.email)
            put(DatabaseHelper.COL_USER_XP, user.currentXP)
            put(DatabaseHelper.COL_USER_LEVEL, user.level)
            put(DatabaseHelper.COL_USER_STREAK, user.currentStreak)
            put(DatabaseHelper.COL_USER_TASKS_COMPLETED, user.tasksCompleted)
        }
        
        // Intentar insertar, si falla actualizar
        return try {
            db.insertOrThrow(DatabaseHelper.TABLE_USERS, null, values)
        } catch (e: Exception) {
            db.update(
                DatabaseHelper.TABLE_USERS,
                values,
                "${DatabaseHelper.COL_USER_UUID} = ?",
                arrayOf(user.id)
            ).toLong()
        }
    }

    /**
     * Obtener usuario por ID
     */
    fun getUserById(userId: String): User? {
        val db = dbHelper.readableDatabase
        var user: User? = null
        
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_USER_UUID} = ?",
            arrayOf(userId),
            null,
            null,
            null
        )
        
        cursor.use {
            if (it.moveToFirst()) {
                user = cursorToUser(it)
            }
        }
        
        return user
    }

    /**
     * Obtener el primer usuario (usuario principal)
     */
    fun getMainUser(): User? {
        val db = dbHelper.readableDatabase
        var user: User? = null
        
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COL_USER_ID} ASC",
            "1"
        )
        
        cursor.use {
            if (it.moveToFirst()) {
                user = cursorToUser(it)
            }
        }
        
        return user
    }

    /**
     * Actualizar XP del usuario
     */
    fun updateUserXP(userId: String, xpToAdd: Int): Boolean {
        val db = dbHelper.writableDatabase
        
        // Obtener XP actual
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COL_USER_XP, DatabaseHelper.COL_USER_LEVEL),
            "${DatabaseHelper.COL_USER_UUID} = ?",
            arrayOf(userId),
            null,
            null,
            null
        )
        
        var success = false
        cursor.use {
            if (it.moveToFirst()) {
                val currentXP = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_XP))
                val currentLevel = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_LEVEL))
                val newXP = currentXP + xpToAdd
                
                // Calcular nuevo nivel (cada 100 XP = 1 nivel)
                val newLevel = 1 + (newXP / 100)
                
                val values = ContentValues().apply {
                    put(DatabaseHelper.COL_USER_XP, newXP)
                    put(DatabaseHelper.COL_USER_LEVEL, newLevel)
                }
                
                val rows = db.update(
                    DatabaseHelper.TABLE_USERS,
                    values,
                    "${DatabaseHelper.COL_USER_UUID} = ?",
                    arrayOf(userId)
                )
                
                success = rows > 0
            }
        }
        
        return success
    }

    /**
     * Incrementar tareas completadas
     */
    fun incrementTasksCompleted(userId: String): Boolean {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE ${DatabaseHelper.TABLE_USERS} " +
            "SET ${DatabaseHelper.COL_USER_TASKS_COMPLETED} = ${DatabaseHelper.COL_USER_TASKS_COMPLETED} + 1 " +
            "WHERE ${DatabaseHelper.COL_USER_UUID} = ?",
            arrayOf(userId)
        )
        return true
    }

    // ============================================
    // ESTADÍSTICAS Y REPORTES
    // ============================================

    /**
     * Obtener conteo de tareas por estado
     */
    fun getTaskCountByStatus(userId: Int = 1): Map<TaskStatus, Int> {
        val db = dbHelper.readableDatabase
        val counts = mutableMapOf<TaskStatus, Int>()
        
        TaskStatus.values().forEach { status ->
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_TASKS} " +
                "WHERE ${DatabaseHelper.COL_TASK_USER_ID} = ? " +
                "AND ${DatabaseHelper.COL_TASK_STATUS} = ?",
                arrayOf(userId.toString(), status.name)
            )
            
            cursor.use {
                if (it.moveToFirst()) {
                    counts[status] = it.getInt(0)
                }
            }
        }
        
        return counts
    }

    /**
     * Obtener conteo de tareas por categoría
     */
    fun getTaskCountByCategory(userId: Int = 1): Map<TaskCategory, Int> {
        val db = dbHelper.readableDatabase
        val counts = mutableMapOf<TaskCategory, Int>()
        
        TaskCategory.values().forEach { category ->
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_TASKS} " +
                "WHERE ${DatabaseHelper.COL_TASK_USER_ID} = ? " +
                "AND ${DatabaseHelper.COL_TASK_CATEGORY} = ?",
                arrayOf(userId.toString(), category.name)
            )
            
            cursor.use {
                if (it.moveToFirst()) {
                    counts[category] = it.getInt(0)
                }
            }
        }
        
        return counts
    }

    // ============================================
    // HELPERS - Conversión Cursor a Objetos
    // ============================================

    /**
     * Convertir Cursor a Task
     */

    private fun cursorToTask(cursor: Cursor): Task {
        return Task(
            id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_UUID)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_TITLE)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_DESCRIPTION)) ?: "",
            category = TaskCategory.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_CATEGORY))
            ),
            priority = TaskPriority.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_PRIORITY))
            ),
            status = TaskStatus.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_STATUS))
            ),
            dueDate = LocalDate.parse(
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_DUE_DATE))
            ),
            xpReward = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_XP_REWARD)),
            imageProof = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_IMAGE_PATH)),
            completedAt = cursor.getLongOrNull(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TASK_COMPLETED_AT))
        )
    }

    /**
     * Convertir Cursor a User
     */
    private fun cursorToUser(cursor: Cursor): User {
        return User(
            id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_UUID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL)) ?: "",
            level = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_LEVEL)),
            currentXP = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_XP)),
            currentStreak = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_STREAK)),
            tasksCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_TASKS_COMPLETED)),
            badges = getUserBadges(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_UUID)))
        )
    }
    
    /**
     * Obtener badges de un usuario
     */
    private fun getUserBadges(userId: String): List<Badge> {
        val badges = mutableListOf<Badge>()
        val db = dbHelper.readableDatabase
        
        val cursor = db.query(
            DatabaseHelper.TABLE_USER_BADGES,
            null,
            "${DatabaseHelper.COL_UB_USER_ID} = ?",
            arrayOf(userId),
            null, null, null
        )
        
        cursor.use {
            while (it.moveToNext()) {
                val badgeId = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_UB_BADGE_ID))
                // Por ahora retornamos badges simples con iconos por defecto
                badges.add(Badge(
                    id = badgeId,
                    name = "Badge $badgeId",
                    description = "Achievement badge",
                    icon = Icons.Default.Star  // ⭐ Star icon
                ))
            }
        }
        
        return badges
    }

    /**
     * Extension para obtener Long nullable desde Cursor
     */
    private fun Cursor.getLongOrNull(columnIndex: Int): Long? {
        return if (isNull(columnIndex)) null else getLong(columnIndex)
    }

    // ============================================
    // LIMPIEZA Y MANTENIMIENTO
    // ============================================

    /**
     * Limpiar todas las tareas (útil para testing)
     */
    fun clearAllTasks() {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_TASKS, null, null)
    }

    /**
     * Obtener estadísticas generales
     */
    fun getGeneralStats(userId: Int = 1): Map<String, Any> {
        val db = dbHelper.readableDatabase
        val stats = mutableMapOf<String, Any>()
        
        // Total de tareas
        val cursorTotal = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_TASKS} WHERE ${DatabaseHelper.COL_TASK_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        cursorTotal.use {
            if (it.moveToFirst()) stats["total_tasks"] = it.getInt(0)
        }
        
        // Tareas completadas
        val cursorCompleted = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_TASKS} " +
            "WHERE ${DatabaseHelper.COL_TASK_USER_ID} = ? AND ${DatabaseHelper.COL_TASK_STATUS} = ?",
            arrayOf(userId.toString(), TaskStatus.COMPLETED.name)
        )
        cursorCompleted.use {
            if (it.moveToFirst()) stats["completed_tasks"] = it.getInt(0)
        }
        
        // Tareas pendientes
        val cursorPending = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_TASKS} " +
            "WHERE ${DatabaseHelper.COL_TASK_USER_ID} = ? AND ${DatabaseHelper.COL_TASK_STATUS} = ?",
            arrayOf(userId.toString(), TaskStatus.PENDING.name)
        )
        cursorPending.use {
            if (it.moveToFirst()) stats["pending_tasks"] = it.getInt(0)
        }
        
        return stats
    }
}
