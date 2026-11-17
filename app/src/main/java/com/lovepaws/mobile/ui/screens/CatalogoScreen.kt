package com.lovepaws.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lovepaws.mobile.data.remote.RetrofitInstance
import com.lovepaws.mobile.viewmodel.MascotaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    mascotaViewModel: MascotaViewModel,
    onMascotaClick: (Long) -> Unit,
    onMisAdopcionesClick: () -> Unit,
    onMiPerfilClick: () -> Unit
) {
    val mascotas by mascotaViewModel.mascotas.collectAsState()
    val mensaje by mascotaViewModel.mensaje.collectAsState()
    val cargando by mascotaViewModel.cargando.collectAsState()
    val mascotasConSolicitudPendiente by mascotaViewModel.mascotasConSolicitudPendiente.collectAsState()
    val cargandoSolicitudes by mascotaViewModel.cargandoSolicitudes.collectAsState()

    LaunchedEffect(Unit) {
        mascotaViewModel.cargarMascotas()
        mascotaViewModel.cargarSolicitudesPendientes()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {

            // Encabezado
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Encuentra a tu compañero",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Conoce a las mascotas que buscan un hogar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onMisAdopcionesClick,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 10.dp
                        )
                    ) {
                        Text(
                            text = "Mis adopciones",
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }

                    OutlinedButton(
                        onClick = onMiPerfilClick,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 10.dp
                        )
                    ) {
                        Text(
                            text = "Mi perfil",
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Catálogo
            PullToRefreshBox(
                isRefreshing = cargando || cargandoSolicitudes,
                onRefresh = {
                    mascotaViewModel.cargarMascotas(forzar = true)
                    mascotaViewModel.cargarSolicitudesPendientes(forzar = true)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (mascotas.isEmpty()) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = mensaje.ifBlank {
                                    "Por ahora no hay mascotas disponibles."
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                } else {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 4.dp,
                            bottom = 20.dp,
                            start = 2.dp,
                            end = 2.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = mascotas,
                            key = { mascota -> mascota.idMascota }
                        ) { mascota ->

                            val edad = when {
                                (mascota.edadAnios ?: 0) >= 1 -> {
                                    val anios = mascota.edadAnios!!
                                    if (anios == 1) "1 año" else "$anios años"
                                }

                                (mascota.edadMeses ?: 0) > 0 -> {
                                    val meses = mascota.edadMeses!!
                                    if (meses == 1) "1 mes" else "$meses meses"
                                }

                                else -> "Edad no especificada"
                            }

                            val sexo = mascota.sexo
                                ?.lowercase()
                                ?.replaceFirstChar { it.uppercase() }
                                ?: "Sexo no especificado"

                            val tieneSolicitudPendiente =
                                mascota.idMascota in mascotasConSolicitudPendiente

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.72f)
                                    .clickable {
                                        onMascotaClick(mascota.idMascota)
                                    },
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                ) {

                                    // Fotografía
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(0.72f)
                                    ) {
                                        if (!mascota.fotoUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = RetrofitInstance.BASE_URL.trimEnd('/') +
                                                        "/" + mascota.fotoUrl.trimStart('/'),
                                                contentDescription = "Fotografía de ${mascota.nombre}",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "Sin fotografía",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        // Solicitud pendiente
                                        if (tieneSolicitudPendiente) {
                                            Surface(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(8.dp),
                                                shape = RoundedCornerShape(50),
                                                color = MaterialTheme.colorScheme.tertiaryContainer
                                            ) {
                                                Text(
                                                    text = "Pendiente",
                                                    modifier = Modifier.padding(
                                                        horizontal = 10.dp,
                                                        vertical = 5.dp
                                                    ),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            }
                                        }
                                    }

                                    // Información de la mascota
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(0.28f)
                                            .padding(
                                                horizontal = 10.dp,
                                                vertical = 8.dp
                                            ),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = mascota.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(
                                            text = mascota.raza ?: "Sin raza especificada",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = "$sexo · $edad",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

