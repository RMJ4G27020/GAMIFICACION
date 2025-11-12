package com.example.ejercicio2.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper - Gestor de base de datos SQLite para la app
 * 
 * Esta clase maneja la creación, actualización y acceso a la base de datos
 * local de la aplicación de Gestor de Tareas Gamificado.
 * 
 * Patrón Singleton para evitar múltiples instancias de la base de datos
 */
class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "task_gamification.db"
        const val DATABASE_VERSION = 2  // Incrementado para migración de autenticación

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        /**
         * Obtener instancia única de DatabaseHelper (Singleton)
         */
        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context).also { INSTANCE = it }
            }
        }

        // Tabla: users
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_UUID = "uuid"
        const val COL_USER_NAME = "name"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_AVATAR = "avatar_url"
        const val COL_USER_XP = "current_xp"
        const val COL_USER_LEVEL = "level"
        const val COL_USER_STREAK = "current_streak"
        const val COL_USER_LONGEST_STREAK = "longest_streak"
        const val COL_USER_TASKS_COMPLETED = "tasks_completed"
        const val COL_USER_TOTAL_XP = "total_xp_earned"

        // Tabla: tasks
        const val TABLE_TASKS = "tasks"
        const val COL_TASK_ID = "id"
        const val COL_TASK_UUID = "uuid"
        const val COL_TASK_USER_ID = "user_id"
        const val COL_TASK_TITLE = "title"
        const val COL_TASK_DESCRIPTION = "description"
        const val COL_TASK_CATEGORY = "category"
        const val COL_TASK_PRIORITY = "priority"
        const val COL_TASK_STATUS = "status"
        const val COL_TASK_DUE_DATE = "due_date"
        const val COL_TASK_XP_REWARD = "xp_reward"
        const val COL_TASK_IMAGE_PATH = "image_proof_path"
        const val COL_TASK_CALENDAR_ID = "calendar_event_id"
        const val COL_TASK_COMPLETED_AT = "completed_at"

        // Tabla: badges
        const val TABLE_BADGES = "badges"
        const val COL_BADGE_ID = "id"
        const val COL_BADGE_KEY = "badge_key"
        const val COL_BADGE_NAME = "name"
        const val COL_BADGE_DESCRIPTION = "description"
        const val COL_BADGE_ICON = "icon_name"
        const val COL_BADGE_REQ_TYPE = "requirement_type"
        const val COL_BADGE_REQ_VALUE = "requirement_value"
        const val COL_BADGE_XP_BONUS = "xp_bonus"

        // Tabla: user_badges
        const val TABLE_USER_BADGES = "user_badges"
        const val COL_UB_ID = "id"
        const val COL_UB_USER_ID = "user_id"
        const val COL_UB_BADGE_ID = "badge_id"
        const val COL_UB_PROGRESS = "progress"
        const val COL_UB_UNLOCKED = "is_unlocked"
        const val COL_UB_UNLOCKED_AT = "unlocked_at"

        // Tabla: study_sessions
        const val TABLE_STUDY_SESSIONS = "study_sessions"
        const val COL_SESSION_ID = "id"
        const val COL_SESSION_UUID = "uuid"
        const val COL_SESSION_USER_ID = "user_id"
        const val COL_SESSION_SUBJECT = "subject"
        const val COL_SESSION_DATE = "scheduled_date"
        const val COL_SESSION_DURATION = "duration_minutes"
        const val COL_SESSION_STATUS = "status"
        const val COL_SESSION_XP = "xp_earned"

        // Tabla: daily_stats
        const val TABLE_DAILY_STATS = "daily_stats"
        const val COL_STATS_ID = "id"
        const val COL_STATS_USER_ID = "user_id"
        const val COL_STATS_DATE = "stat_date"
        const val COL_STATS_TASKS = "tasks_completed"
        const val COL_STATS_XP = "xp_earned"
        const val COL_STATS_MINUTES = "study_minutes"
        const val COL_STATS_STREAK = "streak_active"

        // Tabla: activity_log
        const val TABLE_ACTIVITY_LOG = "activity_log"
        const val COL_ACTIVITY_ID = "id"
        const val COL_ACTIVITY_USER_ID = "user_id"
        const val COL_ACTIVITY_TYPE = "activity_type"
        const val COL_ACTIVITY_ENTITY_TYPE = "entity_type"
        const val COL_ACTIVITY_ENTITY_ID = "entity_id"
        const val COL_ACTIVITY_DESCRIPTION = "description"
        const val COL_ACTIVITY_XP_CHANGE = "xp_change"

        // Tabla: app_settings
        const val TABLE_APP_SETTINGS = "app_settings"
        const val COL_SETTING_KEY = "setting_key"
        const val COL_SETTING_VALUE = "setting_value"

        // Tabla: sync_queue
        const val TABLE_SYNC_QUEUE = "sync_queue"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Leer y ejecutar el archivo schema.sql
        val schema = readSchemaFromAssets()
        executeMultipleStatements(db, schema)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        android.util.Log.d("DatabaseHelper", "Migrando BD de versión $oldVersion a $newVersion")
        
        when (oldVersion) {
            1 -> {
                if (newVersion >= 2) {
                    // Migración v1 -> v2: Agregar campos de autenticación
                    android.util.Log.d("DatabaseHelper", "Aplicando migración v1 -> v2: Autenticación")
                    
                    // Agregar columnas nuevas a la tabla users
                    db.execSQL("ALTER TABLE users ADD COLUMN password_hash TEXT DEFAULT '' NOT NULL")
                    db.execSQL("ALTER TABLE users ADD COLUMN remember_token TEXT")
                    db.execSQL("ALTER TABLE users ADD COLUMN email_verified INTEGER DEFAULT 0 CHECK(email_verified IN (0, 1))")
                    
                    // Hacer email NOT NULL (actualizar primero los existentes)
                    db.execSQL("UPDATE users SET email = 'usuario@ejemplo.com' WHERE email IS NULL OR email = ''")
                    
                    // Crear índice para remember_token
                    db.execSQL("CREATE INDEX IF NOT EXISTS idx_users_remember_token ON users(remember_token)")
                    
                    android.util.Log.d("DatabaseHelper", "✅ Migración v1 -> v2 completada")
                }
            }
        }
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        // Habilitar foreign keys
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    /**
     * Lee el archivo schema.sql del directorio de assets o resources
     */
    private fun readSchemaFromAssets(): String {
        // Si tienes el schema.sql en assets, descomenta esto:
        // return context.assets.open("database/schema.sql").bufferedReader().use { it.readText() }
        
        // Por ahora, retornamos el schema directamente
        return getSchemaSQL()
    }

    /**
     * Ejecuta múltiples statements SQL separados por punto y coma
     * Maneja correctamente TRIGGERS con BEGIN...END blocks
     */
    private fun executeMultipleStatements(db: SQLiteDatabase, sql: String) {
        // Eliminar comentarios de línea
        val cleanSql = sql.lines()
            .filterNot { it.trim().startsWith("--") }
            .joinToString("\n")
        
        // Parser que maneja BEGIN...END blocks
        val statements = mutableListOf<String>()
        var currentStatement = StringBuilder()
        var insideBlock = 0 // Contador para BEGIN...END anidados
        
        cleanSql.split(";").forEach { part ->
            currentStatement.append(part).append(";")
            
            // Contar palabras BEGIN y END completas (no parte de otras palabras)
            val beginMatches = "\\bBEGIN\\b".toRegex(RegexOption.IGNORE_CASE).findAll(part).count()
            val endMatches = "\\bEND\\b".toRegex(RegexOption.IGNORE_CASE).findAll(part).count()
            
            insideBlock += beginMatches - endMatches
            
            // Si no estamos dentro de un bloque BEGIN...END, este statement está completo
            if (insideBlock <= 0) {
                val stmt = currentStatement.toString().trim().removeSuffix(";")
                if (stmt.isNotBlank()) {
                    statements.add(stmt)
                }
                currentStatement.clear()
                insideBlock = 0
            }
        }
        
        android.util.Log.d("DatabaseHelper", "Ejecutando ${statements.size} statements SQL")
        
        statements.forEachIndexed { index, statement ->
            if (statement.isNotBlank()) {
                try {
                    val preview = statement.replace("\\s+".toRegex(), " ").take(80)
                    android.util.Log.d("DatabaseHelper", "Ejecutando [${index + 1}/${statements.size}]: $preview...")
                    db.execSQL(statement)
                } catch (e: Exception) {
                    android.util.Log.e("DatabaseHelper", "❌ Error en statement ${index + 1}", e)
                    throw e
                }
            }
        }
        
        android.util.Log.d("DatabaseHelper", "✅ Todos los statements ejecutados correctamente")
    }

    /**
     * Retorna el SQL completo del schema
     * En producción, considera leer esto desde un archivo externo
     */
    private fun getSchemaSQL(): String {
        return """
            -- Tabla: users
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid TEXT NOT NULL UNIQUE,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                avatar_url TEXT,
                current_xp INTEGER DEFAULT 0 CHECK(current_xp >= 0),
                level INTEGER DEFAULT 1 CHECK(level >= 1),
                current_streak INTEGER DEFAULT 0 CHECK(current_streak >= 0),
                longest_streak INTEGER DEFAULT 0 CHECK(longest_streak >= 0),
                tasks_completed INTEGER DEFAULT 0 CHECK(tasks_completed >= 0),
                total_xp_earned INTEGER DEFAULT 0 CHECK(total_xp_earned >= 0),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                preferences_json TEXT,
                is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1)),
                remember_token TEXT,
                email_verified INTEGER DEFAULT 0 CHECK(email_verified IN (0, 1))
            );

            CREATE INDEX IF NOT EXISTS idx_users_uuid ON users(uuid);
            CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
            CREATE INDEX IF NOT EXISTS idx_users_level ON users(level);
            CREATE INDEX IF NOT EXISTS idx_users_remember_token ON users(remember_token);

            -- Tabla: tasks
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid TEXT NOT NULL UNIQUE,
                user_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                category TEXT NOT NULL CHECK(category IN (
                    'STUDY', 'MATHEMATICS', 'HISTORY', 'SCIENCE', 
                    'EXERCISE', 'SOCIAL', 'WORK', 'PERSONAL'
                )),
                priority TEXT NOT NULL DEFAULT 'MEDIUM' CHECK(priority IN ('LOW', 'MEDIUM', 'HIGH')),
                status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN (
                    'PENDING', 'IN_PROGRESS', 'COMPLETED', 'OVERDUE'
                )),
                due_date DATE NOT NULL,
                xp_reward INTEGER DEFAULT 10 CHECK(xp_reward >= 0),
                image_proof_path TEXT,
                calendar_event_id INTEGER,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                completed_at TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            );

            CREATE INDEX IF NOT EXISTS idx_tasks_uuid ON tasks(uuid);
            CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id);
            CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
            CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
            CREATE INDEX IF NOT EXISTS idx_tasks_category ON tasks(category);
            CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks(priority);

            -- Tabla: badges
            CREATE TABLE IF NOT EXISTS badges (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                badge_key TEXT NOT NULL UNIQUE,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                icon_name TEXT NOT NULL,
                requirement_type TEXT NOT NULL CHECK(requirement_type IN (
                    'TASK_COUNT', 'STREAK', 'XP_MILESTONE', 'CATEGORY_MASTER', 'SPECIAL'
                )),
                requirement_value INTEGER NOT NULL,
                xp_bonus INTEGER DEFAULT 0 CHECK(xp_bonus >= 0),
                is_active INTEGER DEFAULT 1 CHECK(is_active IN (0, 1)),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );

            CREATE INDEX IF NOT EXISTS idx_badges_key ON badges(badge_key);

            -- Tabla: user_badges
            CREATE TABLE IF NOT EXISTS user_badges (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                badge_id INTEGER NOT NULL,
                unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                progress INTEGER DEFAULT 0,
                is_unlocked INTEGER DEFAULT 0 CHECK(is_unlocked IN (0, 1)),
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (badge_id) REFERENCES badges(id) ON DELETE CASCADE,
                UNIQUE(user_id, badge_id)
            );

            CREATE INDEX IF NOT EXISTS idx_user_badges_user ON user_badges(user_id);

            -- Tabla: study_sessions
            CREATE TABLE IF NOT EXISTS study_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                uuid TEXT NOT NULL UNIQUE,
                user_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                description TEXT,
                scheduled_date TIMESTAMP NOT NULL,
                duration_minutes INTEGER NOT NULL CHECK(duration_minutes > 0),
                calendar_event_id INTEGER,
                status TEXT NOT NULL DEFAULT 'SCHEDULED' CHECK(status IN (
                    'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'MISSED'
                )),
                xp_earned INTEGER DEFAULT 0 CHECK(xp_earned >= 0),
                actual_duration_minutes INTEGER,
                notes TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                completed_at TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            );

            CREATE INDEX IF NOT EXISTS idx_sessions_uuid ON study_sessions(uuid);
            CREATE INDEX IF NOT EXISTS idx_sessions_user ON study_sessions(user_id);

            -- Tabla: daily_stats
            CREATE TABLE IF NOT EXISTS daily_stats (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                stat_date DATE NOT NULL,
                tasks_completed INTEGER DEFAULT 0 CHECK(tasks_completed >= 0),
                xp_earned INTEGER DEFAULT 0 CHECK(xp_earned >= 0),
                study_minutes INTEGER DEFAULT 0 CHECK(study_minutes >= 0),
                streak_active INTEGER DEFAULT 0 CHECK(streak_active IN (0, 1)),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                UNIQUE(user_id, stat_date)
            );

            CREATE INDEX IF NOT EXISTS idx_daily_stats_user ON daily_stats(user_id);

            -- Tabla: activity_log
            CREATE TABLE IF NOT EXISTS activity_log (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                activity_type TEXT NOT NULL CHECK(activity_type IN (
                    'TASK_CREATED', 'TASK_COMPLETED', 'TASK_DELETED',
                    'BADGE_UNLOCKED', 'LEVEL_UP', 'SESSION_COMPLETED',
                    'STREAK_MILESTONE', 'XP_EARNED'
                )),
                entity_type TEXT,
                entity_id INTEGER,
                description TEXT NOT NULL,
                xp_change INTEGER DEFAULT 0,
                metadata_json TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            );

            CREATE INDEX IF NOT EXISTS idx_activity_user ON activity_log(user_id);

            -- Tabla: app_settings
            CREATE TABLE IF NOT EXISTS app_settings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                setting_key TEXT NOT NULL UNIQUE,
                setting_value TEXT NOT NULL,
                setting_type TEXT NOT NULL CHECK(setting_type IN ('STRING', 'INTEGER', 'BOOLEAN', 'JSON')),
                description TEXT,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );

            CREATE INDEX IF NOT EXISTS idx_settings_key ON app_settings(setting_key);

            -- Tabla: sync_queue
            CREATE TABLE IF NOT EXISTS sync_queue (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                entity_type TEXT NOT NULL,
                entity_id INTEGER NOT NULL,
                operation TEXT NOT NULL CHECK(operation IN ('CREATE', 'UPDATE', 'DELETE')),
                data_json TEXT NOT NULL,
                sync_status TEXT NOT NULL DEFAULT 'PENDING' CHECK(sync_status IN (
                    'PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED'
                )),
                retry_count INTEGER DEFAULT 0,
                error_message TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                synced_at TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            );

            -- Triggers
            CREATE TRIGGER IF NOT EXISTS update_users_timestamp 
            AFTER UPDATE ON users
            FOR EACH ROW
            BEGIN
                UPDATE users SET updated_at = CURRENT_TIMESTAMP WHERE id = OLD.id;
            END;

            CREATE TRIGGER IF NOT EXISTS update_tasks_timestamp 
            AFTER UPDATE ON tasks
            FOR EACH ROW
            BEGIN
                UPDATE tasks SET updated_at = CURRENT_TIMESTAMP WHERE id = OLD.id;
            END;

            CREATE TRIGGER IF NOT EXISTS task_completed_stats
            AFTER UPDATE ON tasks
            FOR EACH ROW
            WHEN NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED'
            BEGIN
                UPDATE users 
                SET 
                    tasks_completed = tasks_completed + 1,
                    current_xp = current_xp + NEW.xp_reward,
                    total_xp_earned = total_xp_earned + NEW.xp_reward
                WHERE id = NEW.user_id;
                
                INSERT INTO daily_stats (user_id, stat_date, tasks_completed, xp_earned)
                VALUES (NEW.user_id, DATE('now'), 1, NEW.xp_reward)
                ON CONFLICT(user_id, stat_date) DO UPDATE SET
                    tasks_completed = tasks_completed + 1,
                    xp_earned = xp_earned + NEW.xp_reward;
                
                INSERT INTO activity_log (user_id, activity_type, entity_type, entity_id, description, xp_change)
                VALUES (
                    NEW.user_id, 
                    'TASK_COMPLETED', 
                    'task', 
                    NEW.id,
                    'Completaste: ' || NEW.title,
                    NEW.xp_reward
                );
            END;

            -- Datos iniciales: Badges
            INSERT OR IGNORE INTO badges (badge_key, name, description, icon_name, requirement_type, requirement_value, xp_bonus) VALUES
            ('FIRST_TASK', 'Primer Paso', 'Completaste tu primera tarea', 'star', 'TASK_COUNT', 1, 50),
            ('TASK_10', 'Novato Productivo', 'Completaste 10 tareas', 'trophy', 'TASK_COUNT', 10, 100),
            ('TASK_50', 'Estudiante Dedicado', 'Completaste 50 tareas', 'medal', 'TASK_COUNT', 50, 250),
            ('STREAK_3', 'Constancia', '3 días consecutivos completando tareas', 'fire', 'STREAK', 3, 75),
            ('STREAK_7', 'Semana Perfecta', '7 días consecutivos de productividad', 'fire', 'STREAK', 7, 150),
            ('XP_1000', 'Aprendiz', 'Alcanzaste 1,000 XP', 'star', 'XP_MILESTONE', 1000, 100);

            -- Configuración inicial
            INSERT OR IGNORE INTO app_settings (setting_key, setting_value, setting_type, description) VALUES
            ('db_version', '1.0', 'STRING', 'Versión del esquema de base de datos'),
            ('xp_per_level', '100', 'INTEGER', 'XP necesarios para subir de nivel'),
            ('default_task_xp', '10', 'INTEGER', 'XP por defecto para tareas nuevas'),
            ('theme_mode', 'auto', 'STRING', 'Modo de tema: light, dark, auto');
        """.trimIndent()
    }

    /**
     * Limpia toda la base de datos (útil para testing)
     */
    fun clearDatabase() {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS tasks")
        db.execSQL("DROP TABLE IF EXISTS badges")
        db.execSQL("DROP TABLE IF EXISTS user_badges")
        db.execSQL("DROP TABLE IF EXISTS study_sessions")
        db.execSQL("DROP TABLE IF EXISTS daily_stats")
        db.execSQL("DROP TABLE IF EXISTS activity_log")
        db.execSQL("DROP TABLE IF EXISTS app_settings")
        db.execSQL("DROP TABLE IF EXISTS sync_queue")
        onCreate(db)
    }

    /**
     * Cierra la base de datos y libera recursos
     */
    override fun close() {
        try {
            writableDatabase?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.close()
    }

    /**
     * Verifica si la base de datos está corrupta
     * @return true si la DB está corrupta, false si está OK
     */
    fun isDatabaseCorrupted(): Boolean {
        return try {
            val db = readableDatabase
            db.rawQuery("PRAGMA integrity_check", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    val result = cursor.getString(0)
                    result != "ok"
                } else {
                    true
                }
            }
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Obtiene el tamaño de la base de datos en bytes
     */
    fun getDatabaseSize(): Long {
        return try {
            val db = readableDatabase
            db.path?.let { path ->
                java.io.File(path).length()
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}

