package com.example.ejercicio2.database

import android.content.ContentValues
import android.content.Context
import java.util.UUID

/**
 * DatabaseInitializer - Inicializa la base de datos con datos de prueba
 * 
 * Esta clase se encarga de:
 * 1. Crear la base de datos física (.db)
 * 2. Crear un usuario por defecto
 * 3. Insertar datos de ejemplo (opcional)
 */
object DatabaseInitializer {

    /**
     * Inicializa la base de datos y retorna true si se creó correctamente
     */
    fun initialize(context: Context, createSampleData: Boolean = false): Boolean {
        return try {
            val dbHelper = DatabaseHelper.getInstance(context)
            val db = dbHelper.writableDatabase
            
            // Verificar que la base de datos se creó
            if (!db.isOpen) {
                return false
            }
            
            // Crear usuario por defecto si no existe
            val userExists = checkUserExists(context)
            if (!userExists) {
                createDefaultUser(context)
            }
            
            // Crear datos de ejemplo si se solicita
            if (createSampleData && !userExists) {
                createSampleTasks(context)
            }
            
            true
        } catch (e: Exception) {
            android.util.Log.e("DatabaseInitializer", "Error al inicializar BD", e)
            false
        }
    }
    
    /**
     * Verifica si existe al menos un usuario en la BD
     */
    private fun checkUserExists(context: Context): Boolean {
        val dbHelper = DatabaseHelper.getInstance(context)
        val db = dbHelper.readableDatabase
        
        val cursor = db.rawQuery("SELECT COUNT(*) FROM users", null)
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0) > 0
            }
        }
        return false
    }
    
    /**
     * Crea un usuario por defecto
     */
    private fun createDefaultUser(context: Context): Long {
        val dbHelper = DatabaseHelper.getInstance(context)
        val db = dbHelper.writableDatabase
        
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_USER_UUID, UUID.randomUUID().toString())
            put(DatabaseHelper.COL_USER_NAME, "Usuario")
            put(DatabaseHelper.COL_USER_EMAIL, "usuario@example.com")
            put(DatabaseHelper.COL_USER_XP, 0)
            put(DatabaseHelper.COL_USER_LEVEL, 1)
            put(DatabaseHelper.COL_USER_STREAK, 0)
            put(DatabaseHelper.COL_USER_LONGEST_STREAK, 0)
            put(DatabaseHelper.COL_USER_TASKS_COMPLETED, 0)
            put(DatabaseHelper.COL_USER_TOTAL_XP, 0)
        }
        
        val userId = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        android.util.Log.d("DatabaseInitializer", "Usuario creado con ID: $userId")
        
        // Inicializar progreso de badges para el nuevo usuario
        initializeBadgeProgress(context, userId)
        
        return userId
    }
    
    /**
     * Inicializa el progreso de todos los badges para un usuario
     */
    private fun initializeBadgeProgress(context: Context, userId: Long) {
        val dbHelper = DatabaseHelper.getInstance(context)
        val db = dbHelper.writableDatabase
        
        // Obtener todos los badges
        val badgeCursor = db.rawQuery("SELECT id FROM badges WHERE is_active = 1", null)
        
        badgeCursor.use { cursor ->
            while (cursor.moveToNext()) {
                val badgeId = cursor.getLong(0)
                
                val values = ContentValues().apply {
                    put(DatabaseHelper.COL_UB_USER_ID, userId)
                    put(DatabaseHelper.COL_UB_BADGE_ID, badgeId)
                    put(DatabaseHelper.COL_UB_PROGRESS, 0)
                    put(DatabaseHelper.COL_UB_UNLOCKED, 0)
                }
                
                db.insertWithOnConflict(
                    DatabaseHelper.TABLE_USER_BADGES,
                    null,
                    values,
                    android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
                )
            }
        }
        
        android.util.Log.d("DatabaseInitializer", "Badges inicializados para usuario $userId")
    }
    
    /**
     * Crea tareas de ejemplo para pruebas
     */
    private fun createSampleTasks(context: Context) {
        val dbHelper = DatabaseHelper.getInstance(context)
        val db = dbHelper.writableDatabase
        
        // Obtener el ID del primer usuario
        val userCursor = db.rawQuery("SELECT id FROM users LIMIT 1", null)
        val userId = userCursor.use {
            if (it.moveToFirst()) it.getLong(0) else return
        }
        
        val sampleTasks = listOf(
            TaskData(
                "Estudiar matemáticas",
                "Repasar capítulo 5 de álgebra",
                "MATHEMATICS",
                "HIGH",
                "2025-11-05",
                20
            ),
            TaskData(
                "Leer historia",
                "Terminar lectura sobre la Segunda Guerra Mundial",
                "HISTORY",
                "MEDIUM",
                "2025-11-03",
                15
            ),
            TaskData(
                "Hacer ejercicio",
                "30 minutos de cardio",
                "EXERCISE",
                "MEDIUM",
                "2025-11-02",
                10
            ),
            TaskData(
                "Proyecto de ciencias",
                "Completar experimento de química",
                "SCIENCE",
                "HIGH",
                "2025-11-06",
                25
            ),
            TaskData(
                "Tarea de inglés",
                "Escribir ensayo sobre Shakespeare",
                "STUDY",
                "LOW",
                "2025-11-08",
                15
            )
        )
        
        sampleTasks.forEach { task ->
            val values = ContentValues().apply {
                put(DatabaseHelper.COL_TASK_UUID, UUID.randomUUID().toString())
                put(DatabaseHelper.COL_TASK_USER_ID, userId)
                put(DatabaseHelper.COL_TASK_TITLE, task.title)
                put(DatabaseHelper.COL_TASK_DESCRIPTION, task.description)
                put(DatabaseHelper.COL_TASK_CATEGORY, task.category)
                put(DatabaseHelper.COL_TASK_PRIORITY, task.priority)
                put(DatabaseHelper.COL_TASK_STATUS, "PENDING")
                put(DatabaseHelper.COL_TASK_DUE_DATE, task.dueDate)
                put(DatabaseHelper.COL_TASK_XP_REWARD, task.xpReward)
            }
            
            db.insert(DatabaseHelper.TABLE_TASKS, null, values)
        }
        
        android.util.Log.d("DatabaseInitializer", "Tareas de ejemplo creadas")
    }
    
    /**
     * Obtiene la ruta del archivo de base de datos
     */
    fun getDatabasePath(context: Context): String {
        return context.getDatabasePath(DatabaseHelper.DATABASE_NAME).absolutePath
    }
    
    /**
     * Verifica si la base de datos existe físicamente
     */
    fun databaseExists(context: Context): Boolean {
        return context.getDatabasePath(DatabaseHelper.DATABASE_NAME).exists()
    }
    
    /**
     * Obtiene información sobre la base de datos
     */
    fun getDatabaseInfo(context: Context): DatabaseInfo {
        val dbPath = context.getDatabasePath(DatabaseHelper.DATABASE_NAME)
        val exists = dbPath.exists()
        
        return DatabaseInfo(
            path = dbPath.absolutePath,
            exists = exists,
            size = if (exists) dbPath.length() else 0L,
            version = if (exists) {
                try {
                    val db = DatabaseHelper.getInstance(context).readableDatabase
                    db.version
                } catch (e: Exception) {
                    -1
                }
            } else -1
        )
    }
    
    /**
     * Clase de datos para tareas de ejemplo
     */
    private data class TaskData(
        val title: String,
        val description: String,
        val category: String,
        val priority: String,
        val dueDate: String,
        val xpReward: Int
    )
}

/**
 * Información sobre la base de datos
 */
data class DatabaseInfo(
    val path: String,
    val exists: Boolean,
    val size: Long,
    val version: Int
) {
    fun getSizeInKB(): String = "%.2f KB".format(size / 1024.0)
    fun getSizeInMB(): String = "%.2f MB".format(size / (1024.0 * 1024.0))
}
