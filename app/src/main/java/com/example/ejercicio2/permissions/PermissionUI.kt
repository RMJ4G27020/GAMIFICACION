package com.example.ejercicio2.permissions

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * PermissionHandler - Componente Compose para solicitar permisos
 * 
 * Proporciona UI y lógica para solicitar permisos de forma intuitiva
 */
@Composable
fun rememberPermissionHandler(): PermissionHandler {
    var permissions by remember { mutableStateOf(emptyList<String>()) }
    var onPermissionsResult by remember { mutableStateOf<((Map<String, Boolean>) -> Unit)?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        onPermissionsResult?.invoke(results)
    }

    return remember {
        PermissionHandler(
            permissionLauncher = { perms, callback ->
                permissions = perms
                onPermissionsResult = callback
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permissionLauncher.launch(perms.toTypedArray())
                }
            }
        )
    }
}

/**
 * Holder para el launcher de permisos
 */
class PermissionHandler(
    val permissionLauncher: (List<String>, (Map<String, Boolean>) -> Unit) -> Unit
) {
    fun requestPermissions(
        permissions: List<String>,
        onResult: (Map<String, Boolean>) -> Unit
    ) {
        permissionLauncher(permissions, onResult)
    }
}

/**
 * Dialog de solicitud de permisos con explicación
 */
@Composable
fun PermissionRequestDialog(
    title: String = "Se requieren permisos",
    description: String = "",
    permissions: List<String> = emptyList(),
    onGranted: () -> Unit = {},
    onDenied: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        AlertDialog(
            onDismissRequest = {
                isVisible = false
                onDismiss()
            },
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (permissions.isNotEmpty()) {
                        Text(
                            text = "Permisos necesarios:",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        permissions.forEach { permission ->
                            Text(
                                text = "• ${permission}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isVisible = false
                        onGranted()
                    }
                ) {
                    Text("Permitir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isVisible = false
                        onDenied()
                    }
                ) {
                    Text("Denegar")
                }
            }
        )
    }
}

/**
 * Card mostrando el estado de un grupo de permisos
 */
@Composable
fun PermissionStatusCard(
    summary: PermissionSummary,
    onRequestPermissions: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = summary.category,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = summary.getStatus(),
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        summary.isComplete -> MaterialTheme.colorScheme.primary
                        summary.granted == 0 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                )
            }

            // Progress bar
            if (summary.total > 0) {
                LinearProgressIndicator(
                    progress = { summary.granted.toFloat() / summary.total.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = when {
                        summary.isComplete -> MaterialTheme.colorScheme.primary
                        summary.granted == 0 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Detalles de permisos
                summary.permissions.forEach { (permission, isGranted) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = permission.substringAfterLast("."),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Botón de solicitud si no están todos
            if (!summary.isComplete && summary.total > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.align(Alignment.End),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ) {
                    Text("Solicitar")
                }
            }
        }
    }
}
