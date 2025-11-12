package com.example.ejercicio2.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.ejercicio2.database.DatabaseHelper
import com.example.ejercicio2.models.User
import java.security.MessageDigest
import java.util.UUID

/**
 * AuthManager - Gestor de Autenticación
 * 
 * Maneja el login, registro, sesión y persistencia de usuarios
 */
class AuthManager(private val context: Context) {
    
    private val dbHelper = DatabaseHelper.getInstance(context)
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_REMEMBER_TOKEN = "remember_token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        
        // Singleton
        @Volatile
        private var INSTANCE: AuthManager? = null
        
        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
    
    /**
     * Resultado de operaciones de autenticación
     */
    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
    
    // ============================================
    // REGISTRO DE USUARIO
    // ============================================
    
    /**
     * Registrar nuevo usuario
     */
    fun register(name: String, email: String, password: String): AuthResult {
        try {
            // Validaciones
            if (name.isBlank()) {
                return AuthResult.Error("El nombre es requerido")
            }
            
            if (email.isBlank() || !isValidEmail(email)) {
                return AuthResult.Error("El email no es válido")
            }
            
            if (password.length < 6) {
                return AuthResult.Error("La contraseña debe tener al menos 6 caracteres")
            }
            
            // Verificar si el email ya existe
            if (emailExists(email)) {
                return AuthResult.Error("Este email ya está registrado")
            }
            
            // Hashear contraseña
            val passwordHash = hashPassword(password)
            
            // Crear usuario en BD
            val db = dbHelper.writableDatabase
            val uuid = UUID.randomUUID().toString()
            
            val sql = """
                INSERT INTO users (
                    uuid, name, email, password_hash,
                    current_xp, level, current_streak, longest_streak,
                    tasks_completed, total_xp_earned, is_active, email_verified
                ) VALUES (?, ?, ?, ?, 0, 1, 0, 0, 0, 0, 1, 0)
            """.trimIndent()
            
            db.execSQL(sql, arrayOf(uuid, name, email, passwordHash))
            
            // Obtener el usuario creado
            val userId = db.rawQuery("SELECT last_insert_rowid()", null).use { cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) else -1L
            }
            
            if (userId > 0) {
                val user = getUserById(userId)
                if (user != null) {
                    Log.d("AuthManager", "✅ Usuario registrado: ${user.name} (${user.email})")
                    return AuthResult.Success(user)
                }
            }
            
            return AuthResult.Error("Error al crear el usuario")
            
        } catch (e: Exception) {
            Log.e("AuthManager", "Error en registro", e)
            return AuthResult.Error("Error: ${e.message}")
        }
    }
    
    // ============================================
    // LOGIN
    // ============================================
    
    /**
     * Iniciar sesión
     */
    fun login(email: String, password: String, rememberMe: Boolean = false): AuthResult {
        try {
            if (email.isBlank() || password.isBlank()) {
                return AuthResult.Error("Email y contraseña son requeridos")
            }
            
            val db = dbHelper.readableDatabase
            val passwordHash = hashPassword(password)
            
            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE email = ? AND password_hash = ? AND is_active = 1",
                arrayOf(email, passwordHash)
            )
            
            cursor.use {
                if (it.moveToFirst()) {
                    val user = cursorToUser(it)
                    
                    // Actualizar last_login
                    db.execSQL(
                        "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?",
                        arrayOf(user.id)
                    )
                    
                    // Guardar sesión
                    saveSession(user, rememberMe)
                    
                    Log.d("AuthManager", "✅ Login exitoso: ${user.name}")
                    return AuthResult.Success(user)
                } else {
                    return AuthResult.Error("Email o contraseña incorrectos")
                }
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Error en login", e)
            return AuthResult.Error("Error: ${e.message}")
        }
    }
    
    // ============================================
    // GESTIÓN DE SESIÓN
    // ============================================
    
    /**
     * Guardar sesión en SharedPreferences
     */
    private fun saveSession(user: User, rememberMe: Boolean) {
        val editor = prefs.edit()
        editor.putLong(KEY_USER_ID, user.id)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putString(KEY_USER_NAME, user.name)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        
        if (rememberMe) {
            val rememberToken = generateRememberToken()
            editor.putString(KEY_REMEMBER_TOKEN, rememberToken)
            
            // Guardar token en BD
            val db = dbHelper.writableDatabase
            db.execSQL(
                "UPDATE users SET remember_token = ? WHERE id = ?",
                arrayOf(rememberToken, user.id)
            )
        }
        
        editor.apply()
    }
    
    /**
     * Verificar si hay sesión activa
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Obtener usuario actual de la sesión
     */
    fun getCurrentUser(): User? {
        if (!isLoggedIn()) return null
        
        val userId = prefs.getLong(KEY_USER_ID, -1)
        if (userId > 0) {
            return getUserById(userId)
        }
        
        return null
    }
    
    /**
     * Cerrar sesión
     */
    fun logout() {
        val userId = prefs.getLong(KEY_USER_ID, -1)
        
        // Limpiar remember_token de la BD
        if (userId > 0) {
            val db = dbHelper.writableDatabase
            db.execSQL(
                "UPDATE users SET remember_token = NULL WHERE id = ?",
                arrayOf(userId)
            )
        }
        
        // Limpiar SharedPreferences
        prefs.edit().clear().apply()
        
        Log.d("AuthManager", "✅ Sesión cerrada")
    }
    
    /**
     * Intentar login automático con remember_token
     */
    fun autoLogin(): AuthResult? {
        val rememberToken = prefs.getString(KEY_REMEMBER_TOKEN, null) ?: return null
        val userId = prefs.getLong(KEY_USER_ID, -1)
        
        if (userId <= 0) return null
        
        try {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE id = ? AND remember_token = ? AND is_active = 1",
                arrayOf(userId.toString(), rememberToken)
            )
            
            cursor.use {
                if (it.moveToFirst()) {
                    val user = cursorToUser(it)
                    saveSession(user, true)
                    Log.d("AuthManager", "✅ Auto-login exitoso: ${user.name}")
                    return AuthResult.Success(user)
                }
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Error en auto-login", e)
        }
        
        return null
    }
    
    // ============================================
    // UTILIDADES
    // ============================================
    
    /**
     * Verificar si un email existe
     */
    private fun emailExists(email: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM users WHERE email = ?",
            arrayOf(email)
        )
        
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0) > 0
            }
        }
        
        return false
    }
    
    /**
     * Hashear contraseña con SHA-256
     */
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Generar token aleatorio para "recordar sesión"
     */
    private fun generateRememberToken(): String {
        return UUID.randomUUID().toString()
    }
    
    /**
     * Validar formato de email
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Obtener usuario por ID
     */
    private fun getUserById(userId: Long): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE id = ?",
            arrayOf(userId.toString())
        )
        
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToUser(it)
            }
        }
        
        return null
    }
    
    /**
     * Convertir cursor a objeto User
     */
    private fun cursorToUser(cursor: android.database.Cursor): User {
        return User(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
            level = cursor.getInt(cursor.getColumnIndexOrThrow("level")),
            xp = cursor.getInt(cursor.getColumnIndexOrThrow("current_xp")),
            emailVerified = cursor.getInt(cursor.getColumnIndexOrThrow("email_verified")) == 1,
            rememberToken = cursor.getString(cursor.getColumnIndexOrThrow("remember_token"))
        )
    }
}
