package com.lovepaws.mobile.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.lovepaws.mobile.ui.viewmodel.DetalleSolicitudViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DetalleSolicitudScreen(
    idSolicitud: Long,
    viewModel: DetalleSolicitudViewModel = viewModel(),
    onBack: () -> Unit
) {
    val solicitud by viewModel.solicitud.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    LaunchedEffect(idSolicitud) {
        viewModel.cargarDetalle(idSolicitud)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Volver a mis adopciones")
            }

            Text(
                text = "Detalle de solicitud",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                cargando -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                mensaje != null -> {
                    Text(
                        text = mensaje!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                solicitud != null -> {
                    val datos = solicitud!!

                    AsyncImage(
                        model = "http://192.168.18.65:8080${datos.fotoMascotaUrl}",
                        contentDescription = datos.nombreMascota,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = datos.nombreMascota,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Tipo: ${datos.tipoMascota}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Solicitud: #${datos.idSolicitud}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Fecha: ${formatearFecha(datos.fechaSolicitud)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Estado: ${datos.estado}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Text(
                        text = "Tus respuestas",
                        style = MaterialTheme.typography.titleLarge
                    )

                    datos.respuestas
                        .sortedBy { it.ordenPregunta }
                        .forEach { respuesta ->
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = respuesta.pregunta,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = respuesta.respuesta,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                    if (datos.fechaResolucion != null ||
                        datos.nombreResponsable != null ||
                        datos.observacionResolucion != null
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Text(
                            text = "Resolución del gestor",
                            style = MaterialTheme.typography.titleLarge
                        )

                        datos.fechaResolucion?.let {
                            Text("Fecha: ${formatearFecha(it)}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        datos.nombreResponsable?.let {
                            Text("Responsable: $it",
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        datos.observacionResolucion?.let {
                            Text("Observación: $it",
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private fun formatearFecha(fecha: String): String {
    return try {
        val fechaLocal = LocalDateTime.parse(fecha)
        fechaLocal.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm")
        )
    } catch (e: Exception) {
        fecha
    }
}