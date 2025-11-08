package com.example.ejercicio2.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * PermissionManager - Gestor centralizado de permisos de Android
 * 
 * Maneja el verificación y solicitud de permisos de runtime
 * Compatible con API 23+ (Android 6.0+)
 */
class PermissionManager(private val context: Context) {

    /**
     * Estados posibles de un permiso
     */
    enum class PermissionStatus {
        GRANTED,        // ✅ Permiso otorgado
        DENIED,         // ❌ Permiso denegado
        SHOULD_REQUEST  // ⚠️ Debería solicitarse
    }

    companion object {
        // ============================================
        // GRUPOS DE PERMISOS POR CATEGORÍA
        // ============================================

        /**
         * Permisos para acceso a Calendario
         * Necesario para: Leer y escribir eventos en el calendario del dispositivo
         */
        val CALENDAR_PERMISSIONS = listOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )

        /**
         * Permisos para acceso a Almacenamiento
         * Necesario para: Guardar y leer archivos, fotos de prueba
         */
        val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        /**
         * Permisos para acceso a Cámara
         * Necesario para: Tomar fotos como prueba de completación de tareas
         */
        val CAMERA_PERMISSIONS = listOf(
            Manifest.permission.CAMERA
        )

        /**
         * Permisos para acceso a Contactos
         * Necesario para: Compartir tareas, colaborar con otros usuarios
         */
        val CONTACTS_PERMISSIONS = listOf(
            Manifest.permission.READ_CONTACTS
        )

        /**
         * Permisos para Notificaciones (Android 13+)
         * Necesario para: Enviar recordatorios de tareas
         */
        val NOTIFICATION_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }

        /**
         * Todos los permisos del grupo de peligro (dangerous permissions)
         */
        val ALL_PERMISSIONS = (
            CALENDAR_PERMISSIONS + 
            STORAGE_PERMISSIONS + 
            CAMERA_PERMISSIONS + 
            CONTACTS_PERMISSIONS +
            NOTIFICATION_PERMISSIONS
        ).distinct()
    }

    // ============================================
    // VERIFICACIÓN DE PERMISOS INDIVIDUALES
    // ============================================

    /**
     * Verificar si un permiso específico está otorgado
     */
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Obtener el estado de un permiso
     */
    fun getPermissionStatus(permission: String): PermissionStatus {
        return when {
            hasPermission(permission) -> PermissionStatus.GRANTED
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> PermissionStatus.GRANTED
            else -> PermissionStatus.DENIED
        }
    }

    // ============================================
    // VERIFICACIÓN DE GRUPOS DE PERMISOS
    // ============================================

    /**
     * Verificar si TODOS los permisos de una lista están otorgados
     */
    fun hasAllPermissions(permissions: List<String>): Boolean {
        return permissions.all { hasPermission(it) }
    }

    /**
     * Verificar si AL MENOS UNO de los permisos está otorgado
     */
    fun hasAnyPermission(permissions: List<String>): Boolean {
        return permissions.any { hasPermission(it) }
    }

    /**
     * Obtener qué permisos de una lista faltan
     */
    fun getMissingPermissions(permissions: List<String>): List<String> {
        return permissions.filter { !hasPermission(it) }
    }

    // ============================================
    // VERIFICACIÓN POR CATEGORÍA
    // ============================================

    /**
     * ✅ Verificar permisos de Calendario
     */
    fun hasCalendarPermissions(): Boolean {
        return hasAllPermissions(CALENDAR_PERMISSIONS)
    }

    /**
     * ✅ Verificar permisos de Almacenamiento
     */
    fun hasStoragePermissions(): Boolean {
        return hasAllPermissions(STORAGE_PERMISSIONS)
    }

    /**
     * ✅ Verificar permisos de Cámara
     */
    fun hasCameraPermissions(): Boolean {
        return hasAllPermissions(CAMERA_PERMISSIONS)
    }

    /**
     * ✅ Verificar permisos de Contactos
     */
    fun hasContactsPermissions(): Boolean {
        return hasAllPermissions(CONTACTS_PERMISSIONS)
    }

    /**
     * ✅ Verificar permisos de Notificaciones
     */
    fun hasNotificationPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true // Automático en versiones anteriores
        }
    }

    // ============================================
    // INFORMACIÓN Y DIAGNOSTICS
    // ============================================

    /**
     * Obtener estado completo de todos los permisos
     * @return Map<Permiso, Otorgado>
     */
    fun getAllPermissionsStatus(): Map<String, Boolean> {
        return ALL_PERMISSIONS.associateWith { hasPermission(it) }
    }

    /**
     * Obtener resumen de permisos por categoría
     */
    fun getPermissionsSummary(): Map<String, PermissionSummary> {
        return mapOf(
            "Calendario" to getPermissionSummary("Calendario", CALENDAR_PERMISSIONS),
            "Almacenamiento" to getPermissionSummary("Almacenamiento", STORAGE_PERMISSIONS),
            "Cámara" to getPermissionSummary("Cámara", CAMERA_PERMISSIONS),
            "Contactos" to getPermissionSummary("Contactos", CONTACTS_PERMISSIONS),
            "Notificaciones" to getPermissionSummary("Notificaciones", NOTIFICATION_PERMISSIONS)
        )
    }

    private fun getPermissionSummary(
        category: String,
        permissions: List<String>
    ): PermissionSummary {
        val granted = permissions.count { hasPermission(it) }
        val total = permissions.size
        return PermissionSummary(
            category = category,
            granted = granted,
            total = total,
            isComplete = granted == total,
            permissions = permissions.associateWith { hasPermission(it) }
        )
    }

    /**
     * Verificar si la app está en API 23+ (soporte de permisos runtime)
     */
    fun supportsRuntimePermissions(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * Obtener descripción legible de un permiso
     */
    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.READ_CALENDAR -> "Leer eventos del calendario"
            Manifest.permission.WRITE_CALENDAR -> "Crear eventos en el calendario"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Leer archivos del dispositivo"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Escribir archivos en el dispositivo"
            Manifest.permission.MANAGE_EXTERNAL_STORAGE -> "Gestionar archivos del dispositivo"
            Manifest.permission.CAMERA -> "Usar la cámara"
            Manifest.permission.READ_CONTACTS -> "Leer contactos"
            Manifest.permission.POST_NOTIFICATIONS -> "Enviar notificaciones"
            else -> "Permiso: $permission"
        }
    }

    /**
     * Obtener razón por la cual se necesita un permiso
     */
    fun getPermissionReason(permission: String): String {
        return when (permission) {
            Manifest.permission.READ_CALENDAR -> 
                "Necesitamos acceso al calendario para leer tus eventos y no crear duplicados."
            Manifest.permission.WRITE_CALENDAR -> 
                "Necesitamos acceso al calendario para crear recordatorios de tus tareas."
            Manifest.permission.READ_EXTERNAL_STORAGE -> 
                "Necesitamos acceso a tus archivos para guardar pruebas fotográficas."
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> 
                "Necesitamos acceso al almacenamiento para guardar fotos de prueba."
            Manifest.permission.MANAGE_EXTERNAL_STORAGE -> 
                "Necesitamos acceso completo al almacenamiento para gestionar archivos."
            Manifest.permission.CAMERA -> 
                "Necesitamos acceso a la cámara para que puedas tomar fotos como prueba."
            Manifest.permission.READ_CONTACTS -> 
                "Necesitamos acceso a tus contactos para compartir tareas con otros."
            Manifest.permission.POST_NOTIFICATIONS -> 
                "Necesitamos enviar notificaciones para recordarte tus tareas."
            else -> "Este permiso es necesario para el funcionamiento correcto de la app."
        }
    }
}

/**
 * Datos de un resumen de permisos por categoría
 */
data class PermissionSummary(
    val category: String,
    val granted: Int,
    val total: Int,
    val isComplete: Boolean,
    val permissions: Map<String, Boolean>
) {
    fun getStatus(): String {
        return when {
            total == 0 -> "No aplica"
            isComplete -> "✅ Todos otorgados ($granted/$total)"
            granted == 0 -> "❌ Ninguno otorgado (0/$total)"
            else -> "⚠️ Parcial ($granted/$total)"
        }
    }
}
