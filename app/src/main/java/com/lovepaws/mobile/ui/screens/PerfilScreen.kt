package com.lovepaws.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lovepaws.mobile.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    perfilViewModel: PerfilViewModel,
    onBack: () -> Unit,
    onEditarCredenciales: () -> Unit,
    onCambiarPassword: () -> Unit,
    onCerrarSesion: () -> Unit
) {

    val perfil by perfilViewModel.perfil.collectAsState()
    val cargando by perfilViewModel.cargando.collectAsState()
    val mensaje by perfilViewModel.mensaje.collectAsState()

    var mostrarDialogoCerrarSesion by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        perfilViewModel.cargarPerfil()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Volver")
        }

        Text(
            text = "Mi perfil",
            style = MaterialTheme.typography.headlineMedium
        )

        if (cargando && perfil == null) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (perfil != null) {

            // INFORMACIÓN DEL ADOPTANTE
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "${perfil!!.nombres} ${perfil!!.apellidos.orEmpty()}",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "@${perfil!!.username}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = perfil!!.email,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    HorizontalDivider()

                    Text(
                        text = "Estado: ${perfil!!.estado}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // OPCIONES DEL PERFIL
            Text(
                text = "Configuración de cuenta",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedButton(
                onClick = onEditarCredenciales,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Editar credenciales")
            }

            OutlinedButton(
                onClick = onCambiarPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Cambiar contraseña")
            }

        } else if (!mensaje.isNullOrBlank()) {

            Text(
                text = mensaje!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // CERRAR SESIÓN
        OutlinedButton(
            onClick = {
                mostrarDialogoCerrarSesion = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }

    // CONFIRMACIÓN DE CIERRE DE SESIÓN
    if (mostrarDialogoCerrarSesion) {

        AlertDialog(
            onDismissRequest = {
                mostrarDialogoCerrarSesion = false
            },
            title = {
                Text("Cerrar sesión")
            },
            text = {
                Text("¿Estás seguro de que deseas cerrar tu sesión?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        onCerrarSesion()
                    }
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
