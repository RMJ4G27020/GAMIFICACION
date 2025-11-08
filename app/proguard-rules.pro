# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ============================================================================
# GAMIFICATION APP - ProGuard Rules
# ============================================================================

# SQLite Database - Mantener clases relacionadas con la base de datos
-keep class android.database.** { *; }
-keep class android.database.sqlite.** { *; }
-keep class com.example.ejercicio2.database.** { *; }

# DatabaseHelper - Mantener métodos públicos críticos
-keepclassmembers class com.example.ejercicio2.database.DatabaseHelper {
    public <methods>;
}

# DatabaseInitializer - Mantener métodos de inicialización
-keep class com.example.ejercicio2.database.DatabaseInitializer { *; }

# Jetpack Compose - Mantener anotaciones y composables
-dontwarn androidx.compose.animation.**
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
-keep @androidx.compose.runtime.Composable class * { *; }

# Material 3 - Mantener componentes de UI
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.material.icons.** { *; }

# Navigation - Mantener componentes de navegación
-keep class androidx.navigation.** { *; }
-keepclassmembers class androidx.navigation.** { *; }

# ViewModel - Mantener ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class com.example.ejercicio2.viewmodel.** { *; }

# Reflection - Mantener para SQLiteOpenHelper
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# Kotlin - Mantener metadata
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata { *; }

# Coroutines - Mantener para operaciones asíncronas
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Data classes - Mantener constructores y propiedades
-keepclassmembers class com.example.ejercicio2.models.** {
    <init>(...);
    <fields>;
}

# Enums - Mantener para categorías y prioridades
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ContentValues - Usado por SQLite
-keep class android.content.ContentValues { *; }
-keepclassmembers class android.content.ContentValues { *; }

# Cursor - Usado para queries SQLite
-keep class android.database.Cursor { *; }
-keepclassmembers class android.database.Cursor { *; }

# Calendar API - Mantener para integración de calendario
-keep class android.provider.CalendarContract** { *; }

# Activity & Fragment - Mantener lifecycle
-keep public class * extends androidx.activity.ComponentActivity
-keep public class * extends androidx.fragment.app.Fragment

# Serialization - Si se usa en el futuro
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

#-renamesourcefileattribute SourceFile