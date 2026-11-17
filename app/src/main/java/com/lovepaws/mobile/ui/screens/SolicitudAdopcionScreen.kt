package com.lovepaws.mobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lovepaws.mobile.data.dto.PreguntaSolicitudResponse
import com.lovepaws.mobile.viewmodel.SolicitudViewModel
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Composable
fun SolicitudAdopcionScreen(
    idMascota: Long,
    solicitudViewModel: SolicitudViewModel,
    onBack: () -> Unit
) {

    val preguntas by solicitudViewModel.preguntas.collectAsState()
    val respuestas by solicitudViewModel.respuestas.collectAsState()
    val cargando by solicitudViewModel.cargandoPreguntas.collectAsState()
    val enviando by solicitudViewModel.enviandoSolicitud.collectAsState()
    val mensaje by solicitudViewModel.mensaje.collectAsState()
    val solicitudCreada by solicitudViewModel.solicitudCreada.collectAsState()

    // Preparar un formulario limpio para la mascota seleccionada.
    LaunchedEffect(idMascota) {
        solicitudViewModel.reiniciarFormulario()
        solicitudViewModel.cargarPreguntas()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // Barra superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(onClick = onBack) {
                    Text("‹ Volver")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Solicitud de adopción",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Cuestionario de adopción",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Cuéntanos un poco sobre ti y las condiciones que puedes ofrecerle a tu futuro compañero.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Cargando preguntas
                if (cargando && preguntas.isEmpty()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Sin preguntas disponibles
                else if (preguntas.isEmpty()) {

                    Text(
                        text = mensaje.ifBlank {
                            "No hay preguntas disponibles."
                        },
                        color = MaterialTheme.colorScheme.error
                    )

                    OutlinedButton(
                        onClick = {
                            solicitudViewModel.cargarPreguntas()
                        }
                    ) {
                        Text("Reintentar")
                    }
                }

                // Solicitud registrada
                else if (solicitudCreada != null) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Text(
                                text = "✓",
                                fontSize = 40.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "¡Solicitud registrada!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Tu solicitud se ha enviado correctamente. Ahora podrás consultar su estado desde Mis adopciones.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "Código de solicitud: ${solicitudCreada!!.idSolicitud}",
                                fontWeight = FontWeight.Medium
                            )

                            Button(
                                onClick = onBack,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Volver")
                            }
                        }
                    }
                }

                // Preguntas dinámicas
                else {

                    preguntas.forEach { pregunta ->

                        PreguntaSolicitudItem(
                            pregunta = pregunta,
                            respuesta = respuestas[pregunta.idPregunta].orEmpty(),
                            onRespuestaChange = { respuesta ->
                                solicitudViewModel.actualizarRespuesta(
                                    pregunta.idPregunta,
                                    respuesta
                                )
                            }
                        )
                    }

                    if (
                        mensaje.isNotBlank() &&
                        mensaje != "Preguntas cargadas correctamente."
                    ) {

                        Text(
                            text = mensaje,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            solicitudViewModel.crearSolicitud(idMascota)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !enviando && !cargando
                    ) {

                        if (enviando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Enviar solicitud")
                        }
                    }

                    Text(
                        text = "* Las preguntas marcadas son obligatorias.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PreguntaSolicitudItem(
    pregunta: PreguntaSolicitudResponse,
    respuesta: String,
    onRespuestaChange: (String) -> Unit
) {

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

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = pregunta.texto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = if (pregunta.obligatoria) {
                    "* Obligatoria"
                } else {
                    "Opcional"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            when (pregunta.tipoRespuesta.uppercase()) {

                // Respuesta de texto
                "TEXTO" -> {

                    OutlinedTextField(
                        value = respuesta,
                        onValueChange = onRespuestaChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = coloresCampos,
                        placeholder = {
                            Text("Escribe tu respuesta...")
                        },
                        minLines = 3,
                        maxLines = 5
                    )
                }

                // Respuesta sí / no
                "SI_NO" -> {

                    listOf(
                        "SI" to "Sí",
                        "NO" to "No"
                    ).forEach { (valor, etiqueta) ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onRespuestaChange(valor)
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            RadioButton(
                                selected = respuesta == valor,
                                onClick = {
                                    onRespuestaChange(valor)
                                }
                            )

                            Text(
                                text = etiqueta,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                // Respuesta numérica
                "NUMERO" -> {

                    OutlinedTextField(
                        value = respuesta,
                        onValueChange = onRespuestaChange,
                        colors = coloresCampos,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Ingresa un número")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                }

                // Respuesta de selección
                "SELECCION" -> {

                    val opciones = (pregunta.opciones as? JsonArray)
                        ?.mapNotNull { opcion ->
                            (opcion as? JsonPrimitive)?.contentOrNull
                        }
                        .orEmpty()

                    opciones.forEach { opcion ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onRespuestaChange(opcion)
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            RadioButton(
                                selected = respuesta == opcion,
                                onClick = {
                                    onRespuestaChange(opcion)
                                }
                            )

                            Text(
                                text = opcion,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    if (opciones.isEmpty()) {
                        Text(
                            text = "No hay opciones configuradas.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {

                    Text(
                        text = "Tipo de respuesta no compatible: ${pregunta.tipoRespuesta}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
