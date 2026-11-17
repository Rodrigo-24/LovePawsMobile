package com.lovepaws.mobile.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lovepaws.mobile.data.dto.ActualizarPerfilRequest
import com.lovepaws.mobile.viewmodel.PerfilViewModel

@Composable
fun EditarCredencialesScreen(
    perfilViewModel: PerfilViewModel,
    onBack: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val perfil by perfilViewModel.perfil.collectAsState()
    val actualizando by perfilViewModel.actualizando.collectAsState()
    val mensaje by perfilViewModel.mensaje.collectAsState()
    val perfilActualizado by perfilViewModel.perfilActualizado.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var passwordActual by remember { mutableStateOf("") }

    var mostrarPassword by remember { mutableStateOf(false) }
    var errorFormulario by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoReingreso by remember { mutableStateOf(false) }

    val coloresCampos = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    LaunchedEffect(Unit) {
        perfilViewModel.limpiarEstado()
        perfilViewModel.cargarPerfil()
    }

    LaunchedEffect(perfil) {
        perfil?.let {
            username = it.username
            email = it.email
        }
    }

    LaunchedEffect(perfilActualizado) {
        if (perfilActualizado) {
            passwordActual = ""
            mostrarDialogoReingreso = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← Volver")
        }

        Text(
            text = "Editar credenciales",
            style = MaterialTheme.typography.headlineMedium
        )

        if (perfil == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (mensaje.isNullOrBlank()) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = mensaje!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        } else {

            OutlinedTextField(
                value = username,
                onValueChange = {
                    if (it.length <= 40) username = it
                },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = coloresCampos
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    if (it.length <= 150) email = it
                },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = coloresCampos
            )

            OutlinedTextField(
                value = passwordActual,
                onValueChange = { passwordActual = it },
                label = { Text("Contraseña actual") },
                visualTransformation = if (mostrarPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    TextButton(
                        onClick = {
                            mostrarPassword = !mostrarPassword
                        }
                    ) {
                        Text(if (mostrarPassword) "Ocultar" else "Mostrar")
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = coloresCampos
            )

            Button(
                onClick = {
                    errorFormulario = when {
                        username.trim().length < 4 ->
                            "El usuario debe tener al menos 4 caracteres."

                        email.isBlank() ->
                            "Ingresa tu correo electrónico."

                        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                            "Ingresa un correo electrónico válido."

                        passwordActual.isBlank() ->
                            "Ingresa tu contraseña actual."

                        else -> null
                    }

                    if (errorFormulario == null) {
                        perfilViewModel.limpiarEstado()

                        perfilViewModel.actualizarPerfil(
                            ActualizarPerfilRequest(
                                username = username.trim(),
                                email = email.trim(),
                                passwordActual = passwordActual
                            )
                        )
                    }
                },
                enabled = !actualizando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    if (actualizando) "Actualizando..."
                    else "Guardar cambios"
                )
            }
        }

        if (!errorFormulario.isNullOrBlank()) {
            Text(
                text = errorFormulario!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!mensaje.isNullOrBlank() && !perfilActualizado) {
            Text(
                text = mensaje!!,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (mostrarDialogoReingreso) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text("Credenciales actualizadas")
            },
            text = {
                Text(
                    "Tus credenciales se actualizaron correctamente. " +
                            "Por seguridad, debes iniciar sesión nuevamente."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoReingreso = false
                        onCerrarSesion()
                    }
                ) {
                    Text("Ir al inicio de sesión")
                }
            }
        )
    }
}
