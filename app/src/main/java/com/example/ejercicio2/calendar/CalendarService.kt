package com.example.ejercicio2.calendar

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.util.*

class CalendarService(private val context: Context) {
    
    companion object {
        private const val DEFAULT_CALENDAR_ID = 1L
    }
    
    /**
     * Crea un evento en el calendario para una tarea
     */
    fun createTaskEvent(
        title: String,
        description: String,
        startTimeMillis: Long,
        durationMinutes: Int = 60
    ): Long? {
        return try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startTimeMillis)
                put(CalendarContract.Events.DTEND, startTimeMillis + (durationMinutes * 60 * 1000))
                put(CalendarContract.Events.TITLE, "📚 $title")
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.CALENDAR_ID, DEFAULT_CALENDAR_ID)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.HAS_ALARM, 1)
            }
            
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            ContentUris.parseId(uri!!)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Crea un recordatorio para la tarea
     */
    fun createTaskReminder(eventId: Long, minutesBefore: Int = 30): Boolean {
        return try {
            val values = ContentValues().apply {
                put(CalendarContract.Reminders.EVENT_ID, eventId)
                put(CalendarContract.Reminders.MINUTES, minutesBefore)
                put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            }
            
            context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values)
            true
        } catch (e: Exception) {
            false
        }
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
        val eventId = createTaskEvent(
            title = "Sesión de $subject",
            description = "📖 $description\n\n⏰ Duración: ${durationMinutes}min\n🎯 ¡Mantén tu racha de estudio!",
            startTimeMillis = dateTime.timeInMillis,
            durationMinutes = durationMinutes
        )
        
        return if (eventId != null) {
            // Crear recordatorio 15 minutos antes
            createTaskReminder(eventId, 15)
            true
        } else {
            false
        }
    }
    
    /**
     * Programa recordatorio para fecha límite de tarea
     */
    fun scheduleTaskDeadline(
        taskTitle: String,
        description: String,
        deadlineDate: Calendar
    ): Boolean {
        // Crear evento el día de la fecha límite
        val eventId = createTaskEvent(
            title = "⚠️ Fecha límite: $taskTitle",
            description = "🚨 $description\n\n📅 Esta tarea debe completarse hoy\n💪 ¡Tú puedes!",
            startTimeMillis = deadlineDate.timeInMillis,
            durationMinutes = 30
        )
        
        return if (eventId != null) {
            // Recordatorios: 1 día antes y 2 horas antes
            createTaskReminder(eventId, 24 * 60) // 1 día antes
            createTaskReminder(eventId, 2 * 60)  // 2 horas antes
            true
        } else {
            false
        }
    }
    
    /**
     * Verifica si la app tiene permisos de calendario
     */
    fun hasCalendarPermissions(): Boolean {
        return try {
            val projection = arrayOf(CalendarContract.Calendars._ID)
            val cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                null,
                null,
                null
            )
            val hasPermission = cursor != null
            cursor?.close()
            hasPermission
        } catch (e: SecurityException) {
            false
        }
    }
}