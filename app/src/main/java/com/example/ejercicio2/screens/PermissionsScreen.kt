package com.example.ejercicio2.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ejercicio2.permissions.PermissionManager
import com.example.ejercicio2.permissions.PermissionStatusCard
import com.example.ejercicio2.ui.theme.BackgroundLight
import com.example.ejercicio2.ui.theme.TextPrimary

/**
 * PermissionsScreen - Pantalla de Gestión de Permisos
 * 
 * Muestra el estado de todos los permisos de la app
 * Permite al usuario solicitar/revocar permisos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val permissionManager = remember { PermissionManager(context) }
    
    // Estado de permisos
    var permissionsSummary by remember {
        mutableStateOf(permissionManager.getPermissionsSummary())
    }
    
    // Launcher para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // Actualizar estado después de la solicitud
        permissionsSummary = permissionManager.getPermissionsSummary()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permisos y Servicios") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Introducción
            item {
                Column {
                    Text(
                        text = "Permisos de la Aplicación",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Gestiona qué funcionalidades pueden acceder a tus datos personales",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                }
            }

            // Resumen general
            item {
                ResumenGeneralCard(permissionManager, permissionsSummary)
            }

            // Sección de Calendario
            item {
                PermissionSectionHeader(
                    icon = Icons.Default.Event,
                    title = "📅 Calendario",
                    description = "Crear recordatorios y eventos de tareas"
                )
            }

            item {
                permissionsSummary["Calendario"]?.let { summary ->
                    PermissionStatusCard(
                        summary = summary,
                        onRequestPermissions = {
                            permissionLauncher.launch(
                                PermissionManager.CALENDAR_PERMISSIONS.toTypedArray()
                            )
                        }
                    )
                }
            }

            // Sección de Almacenamiento
            item {
                PermissionSectionHeader(
                    icon = Icons.Default.Storage,
                    title = "💾 Almacenamiento",
                    description = "Guardar fotos como prueba de tareas"
                )
            }

            item {
                permissionsSummary["Almacenamiento"]?.let { summary ->
                    PermissionStatusCard(
                        summary = summary,
                        onRequestPermissions = {
                            permissionLauncher.launch(
                                PermissionManager.STORAGE_PERMISSIONS.toTypedArray()
                            )
                        }
                    )
                }
            }

            // Sección de Cámara
            item {
                PermissionSectionHeader(
                    icon = Icons.Default.PhotoCamera,
                    title = "📸 Cámara",
                    description = "Tomar fotos como evidencia de completación"
                )
            }

            item {
                permissionsSummary["Cámara"]?.let { summary ->
                    PermissionStatusCard(
                        summary = summary,
                        onRequestPermissions = {
                            permissionLauncher.launch(
                                PermissionManager.CAMERA_PERMISSIONS.toTypedArray()
                            )
                        }
                    )
                }
            }

            // Sección de Contactos
            item {
                PermissionSectionHeader(
                    icon = Icons.Default.Contacts,
                    title = "👥 Contactos",
                    description = "Compartir tareas con otros usuarios"
                )
            }

            item {
                permissionsSummary["Contactos"]?.let { summary ->
                    PermissionStatusCard(
                        summary = summary,
                        onRequestPermissions = {
                            permissionLauncher.launch(
                                PermissionManager.CONTACTS_PERMISSIONS.toTypedArray()
                            )
                        }
                    )
                }
            }

            // Sección de Notificaciones
            item {
                PermissionSectionHeader(
                    icon = Icons.Default.Notifications,
                    title = "🔔 Notificaciones",
                    description = "Recordatorios de tareas próximas"
                )
            }

            item {
                permissionsSummary["Notificaciones"]?.let { summary ->
                    if (summary.total > 0) {
                        PermissionStatusCard(
                            summary = summary,
                            onRequestPermissions = {
                                permissionLauncher.launch(
                                    PermissionManager.NOTIFICATION_PERMISSIONS.toTypedArray()
                                )
                            }
                        )
                    } else {
                        Text(
                            text = "No disponible en esta versión de Android",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Información adicional
            item {
                Spacer(modifier = Modifier.height(16.dp))
                InfoCard()
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Card con resumen general de permisos
 */
@Composable
private fun ResumenGeneralCard(
    permissionManager: PermissionManager,
    summary: Map<String, com.example.ejercicio2.permissions.PermissionSummary>
) {
    val totalGranted = summary.values.sumOf { it.granted }
    val totalPermissions = summary.values.sumOf { it.total }
    val percentage = if (totalPermissions > 0) (totalGranted * 100) / totalPermissions else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Estado General",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Porcentaje grande
            Text(
                text = "$percentage%",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$totalGranted de $totalPermissions permisos otorgados",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { totalGranted.toFloat() / totalPermissions.toFloat().coerceAtLeast(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = when {
                    percentage == 100 -> "✅ Todos los permisos otorgados"
                    percentage == 0 -> "❌ No hay permisos otorgados"
                    else -> "⚠️ Algunos permisos están pendientes"
                },
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    percentage == 100 -> MaterialTheme.colorScheme.primary
                    percentage == 0 -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.tertiary
                }
            )
        }
    }
}

/**
 * Header para cada sección de permisos
 */
@Composable
private fun PermissionSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 32.dp)
        )
    }
}

/**
 * Card informativo con recomendaciones
 */
@Composable
private fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ℹ️ Información",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Text(
                text = "• Los permisos son opcionales pero recomendados\n" +
                        "• Puedes cambiar los permisos en cualquier momento\n" +
                        "• La app funciona sin permisos, pero con funcionalidad limitada\n" +
                        "• Ve a Configuración > Aplicaciones > Permisos para más opciones",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
