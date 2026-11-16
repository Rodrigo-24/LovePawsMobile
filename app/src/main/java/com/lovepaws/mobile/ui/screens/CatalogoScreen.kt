package com.lovepaws.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lovepaws.mobile.data.remote.RetrofitInstance
import com.lovepaws.mobile.viewmodel.MascotaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    mascotaViewModel: MascotaViewModel,
    onMascotaClick: (Long) -> Unit,
    onMisAdopcionesClick: () -> Unit
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Mascotas disponibles",
                style = MaterialTheme.typography.headlineMedium
            )

            TextButton(
                onClick = onMisAdopcionesClick
            ) {
                Text("Mis adopciones")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    if (cargando) {
                        CircularProgressIndicator()
                    } else {
                        Text(mensaje)
                    }
                }

            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                .height(300.dp)
                                .clickable {
                                    onMascotaClick(mascota.idMascota)
                                },
                        ) {

                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {

                                // Fotografía: 80%
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.8f)
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
                                            Text("Sin fotografía")
                                        }
                                    }

                                    // Etiqueta de solicitud pendiente
                                    if (tieneSolicitudPendiente) {

                                        Surface(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp),

                                            shape = MaterialTheme.shapes.small,

                                            color = MaterialTheme.colorScheme.tertiaryContainer
                                        ) {

                                            Text(
                                                text = "PENDIENTE",
                                                modifier = Modifier.padding(
                                                    horizontal = 9.dp,
                                                    vertical = 5.dp
                                                ),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                    }
                                }

                                // Información: 20%
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.2f)
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {

                                    Text(
                                        text = mascota.nombre,
                                        style = MaterialTheme.typography.titleSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = "${mascota.raza ?: "Sin raza"} · $sexo · $edad",
                                        fontSize = 10.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 12.sp
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