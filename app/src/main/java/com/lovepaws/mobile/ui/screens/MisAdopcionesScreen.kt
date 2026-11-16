package com.lovepaws.mobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lovepaws.mobile.data.dto.SolicitudEstadoResponse
import com.lovepaws.mobile.viewmodel.MisAdopcionesViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisAdopcionesScreen(
    viewModel: MisAdopcionesViewModel,
    onSolicitudClick: (Long) -> Unit,
    onBack: () -> Unit
) {

    val solicitudes by viewModel.solicitudes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarSolicitudes()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(onClick = onBack) {
                    Text("‹ Volver")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Mis adopciones",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Consulta tus solicitudes de adopción y revisa su estado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Listado con actualización mediante gesto
            PullToRefreshBox (
                isRefreshing = cargando,
                onRefresh = {
                    viewModel.cargarSolicitudes(forzar = true)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                if (solicitudes.isEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        if (cargando) {

                            CircularProgressIndicator()

                        } else {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                Text(
                                    text = "🐾",
                                    fontSize = 42.sp
                                )

                                Text(
                                    text = mensaje.ifBlank {
                                        "Aún no tienes solicitudes registradas."
                                    },
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                OutlinedButton(
                                    onClick = {
                                        viewModel.cargarSolicitudes(forzar = true)
                                    }
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            vertical = 4.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        item {

                            Text(
                                text = "${solicitudes.size} solicitudes registradas",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        items(
                            items = solicitudes,
                            key = { solicitud -> solicitud.idSolicitud }
                        ) { solicitud ->

                            SolicitudAdopcionCard(
                                solicitud = solicitud,
                                onClick = {
                                    onSolicitudClick(solicitud.idSolicitud)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SolicitudAdopcionCard(
    solicitud: SolicitudEstadoResponse,
    onClick: () -> Unit
) {

    val estado = when (solicitud.estado.uppercase()) {

        "PENDIENTE" -> Triple(
            "Pendiente",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )

        "APROBADA" -> Triple(
            "Aprobada",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )

        "RECHAZADA" -> Triple(
            "Rechazada",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )

        else -> Triple(
            solicitud.estado,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = solicitud.nombreMascota,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = solicitud.tipoMascota,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Surface(
                    color = estado.second,
                    shape = MaterialTheme.shapes.small
                ) {

                    Text(
                        text = estado.first,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        color = estado.third,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Fecha de solicitud",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = formatearFecha(solicitud.fechaSolicitud),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "Ver detalles ›",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun formatearFecha(fecha: String): String {

    return try {

        val fechaConvertida = LocalDateTime.parse(fecha)

        fechaConvertida.format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm")
        )

    } catch (e: Exception) {

        fecha
    }
}
