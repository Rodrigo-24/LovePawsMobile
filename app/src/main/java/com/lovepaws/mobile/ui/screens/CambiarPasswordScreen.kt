package com.lovepaws.mobile.ui.screens

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
import com.lovepaws.mobile.data.dto.CambiarPasswordRequest
import com.lovepaws.mobile.viewmodel.PerfilViewModel

@Composable
fun CambiarPasswordScreen(
    perfilViewModel: PerfilViewModel,
    onBack: () -> Unit,
    onCerrarSesion: () -> Unit
) {

    val cambiandoPassword by perfilViewModel.cambiandoPassword.collectAsState()
    val mensaje by perfilViewModel.mensaje.collectAsState()
    val passwordCambiado by perfilViewModel.passwordCambiado.collectAsState()

    var passwordActual by remember { mutableStateOf("") }
    var passwordNueva by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var mostrarPasswordActual by remember { mutableStateOf(false) }
    var mostrarPasswordNueva by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    var errorFormulario by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoReingreso by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        perfilViewModel.limpiarEstado()
    }

    LaunchedEffect(passwordCambiado) {
        if (passwordCambiado) {
            passwordActual = ""
            passwordNueva = ""
            confirmarPassword = ""
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
            text = "Cambiar contraseña",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Por seguridad, ingresa tu contraseña actual para establecer una nueva.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = passwordActual,
            onValueChange = { passwordActual = it },
            label = { Text("Contraseña actual") },
            visualTransformation = if (mostrarPasswordActual) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(
                    onClick = {
                        mostrarPasswordActual = !mostrarPasswordActual
                    }
                ) {
                    Text(if (mostrarPasswordActual) "Ocultar" else "Mostrar")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = passwordNueva,
            onValueChange = {
                if (it.length <= 72) passwordNueva = it
            },
            label = { Text("Nueva contraseña") },
            visualTransformation = if (mostrarPasswordNueva) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(
                    onClick = {
                        mostrarPasswordNueva = !mostrarPasswordNueva
                    }
                ) {
                    Text(if (mostrarPasswordNueva) "Ocultar" else "Mostrar")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = {
                if (it.length <= 72) confirmarPassword = it
            },
            label = { Text("Confirmar nueva contraseña") },
            visualTransformation = if (mostrarConfirmacion) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = !mostrarConfirmacion
                    }
                ) {
                    Text(if (mostrarConfirmacion) "Ocultar" else "Mostrar")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "La nueva contraseña debe tener entre 8 y 72 caracteres.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (!errorFormulario.isNullOrBlank()) {
            Text(
                text = errorFormulario!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (!mensaje.isNullOrBlank() && !passwordCambiado) {
            Text(
                text = mensaje!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {

                errorFormulario = when {
                    passwordActual.isBlank() ->
                        "Ingresa tu contraseña actual."

                    passwordNueva.length < 8 ->
                        "La nueva contraseña debe tener al menos 8 caracteres."

                    passwordNueva == passwordActual ->
                        "La nueva contraseña debe ser diferente a la actual."

                    confirmarPassword.isBlank() ->
                        "Confirma tu nueva contraseña."

                    passwordNueva != confirmarPassword ->
                        "Las contraseñas no coinciden."

                    else -> null
                }

                if (errorFormulario == null) {

                    perfilViewModel.limpiarEstado()

                    perfilViewModel.cambiarPassword(
                        CambiarPasswordRequest(
                            passwordActual = passwordActual,
                            passwordNueva = passwordNueva,
                            confirmarPassword = confirmarPassword
                        )
                    )
                }
            },
            enabled = !cambiandoPassword,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                if (cambiandoPassword) "Cambiando..."
                else "Guardar nueva contraseña"
            )
        }
    }

    if (mostrarDialogoReingreso) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text("Contraseña actualizada")
            },
            text = {
                Text(
                    "Tu contraseña se cambió correctamente. " +
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
