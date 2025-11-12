package com.example.ejercicio2.models

/**
 * Modelo de usuario para el sistema de autenticación
 */
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val level: Int = 1,
    val xp: Int = 0,
    val emailVerified: Boolean = false,
    val rememberToken: String? = null
)
