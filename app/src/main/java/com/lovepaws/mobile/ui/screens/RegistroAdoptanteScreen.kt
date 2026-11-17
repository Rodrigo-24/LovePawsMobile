package com.lovepaws.mobile.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.lovepaws.mobile.data.dto.RegistroAdoptanteRequest
import com.lovepaws.mobile.viewmodel.RegistroAdoptanteViewModel

@Composable
fun RegistroAdoptanteScreen(
    registroViewModel: RegistroAdoptanteViewModel,
    onRegistroExitoso: () -> Unit,
    onVolverLogin: () -> Unit
) {
    val cargando by registroViewModel.cargando.collectAsState()
    val mensaje by registroViewModel.mensaje.collectAsState()
    val registroExitoso by registroViewModel.registroExitoso.collectAsState()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var mostrarPassword by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    var errorFormulario by remember { mutableStateOf<String?>(null) }

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

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            onRegistroExitoso()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Regístrate para comenzar a adoptar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                value = nombres,
                onValueChange = {
                    if (it.length <= 80) nombres = it
                },
                label = { Text("Nombres") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = coloresCampos
            )

            OutlinedTextField(
                value = apellidos,
                onValueChange = {
                    if (it.length <= 80) apellidos = it
                },
                label = { Text("Apellidos (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = coloresCampos
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    if (it.length <= 72) password = it
                },
                label = { Text("Contraseña") },
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = coloresCampos
            )

            OutlinedTextField(
                value = confirmarPassword,
                onValueChange = {
                    if (it.length <= 72) confirmarPassword = it
                },
                label = { Text("Confirmar contraseña") },
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
                modifier = Modifier.fillMaxWidth(),
                colors = coloresCampos
            )

            Text(
                text = "El usuario debe tener entre 4 y 40 caracteres. " +
                        "La contraseña debe tener entre 8 y 72 caracteres.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!errorFormulario.isNullOrBlank()) {
                Text(
                    text = errorFormulario!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!mensaje.isNullOrBlank()) {
                Text(
                    text = mensaje!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    errorFormulario = when {
                        username.isBlank() ->
                            "Ingresa un nombre de usuario."

                        username.trim().length < 4 ->
                            "El usuario debe tener al menos 4 caracteres."

                        email.isBlank() ->
                            "Ingresa tu correo electrónico."

                        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                            "Ingresa un correo electrónico válido."

                        nombres.isBlank() ->
                            "Ingresa tus nombres."

                        password.length < 8 ->
                            "La contraseña debe tener al menos 8 caracteres."

                        confirmarPassword.isBlank() ->
                            "Confirma tu contraseña."

                        password != confirmarPassword ->
                            "Las contraseñas no coinciden."

                        else -> null
                    }

                    if (errorFormulario == null) {
                        registroViewModel.registrar(
                            RegistroAdoptanteRequest(
                                username = username.trim(),
                                email = email.trim(),
                                password = password,
                                nombres = nombres.trim(),
                                apellidos = apellidos.trim()
                            )
                        )
                    }
                },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    if (cargando) "Creando cuenta..." else "Crear cuenta"
                )
            }

            TextButton(
                onClick = onVolverLogin,
                enabled = !cargando
            ) {
                Text("¿Ya tienes cuenta? Iniciar sesión")
            }
        }
    }
}
