package com.lovepaws.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.lovepaws.mobile.data.dto.MascotaResponse
import com.lovepaws.mobile.data.remote.RetrofitInstance
import com.lovepaws.mobile.viewmodel.DetalleMascotaViewModel

@Composable
fun DetalleMascotaScreen(
    idMascota: Long,
    detalleMascotaViewModel: DetalleMascotaViewModel,
    onBack: () -> Unit,
    onSolicitar: () -> Unit,
    solicitudPendiente: Boolean,
    verificandoSolicitud: Boolean
) {

    val mascota by detalleMascotaViewModel.mascota.collectAsState()
    val mensaje by detalleMascotaViewModel.mensaje.collectAsState()
    val cargando by detalleMascotaViewModel.cargando.collectAsState()
    val mascotaActual = mascota
    val scrollState = rememberScrollState()
    var dragOffset by remember {
        mutableFloatStateOf(0f)
    }

    val scope = rememberCoroutineScope()
    val dismissThreshold = with(LocalDensity.current) {
        120.dp.toPx()
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val dismissalTarget = with(density) {
        (configuration.screenHeightDp.dp + 80.dp).toPx()
    }

    LaunchedEffect(idMascota) {
        detalleMascotaViewModel.cargarDetalle(idMascota)
    }

    when {

        cargando || (mascotaActual != null &&
                mascotaActual.idMascota != idMascota) -> {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        mascota == null -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Button(onClick = onBack) {
                    Text("Volver al catálogo")
                }
            }
        }

        else -> {

            val detalle = mascotaActual ?: return

            val dragProgress = (
                    dragOffset / dismissThreshold
                    ).coerceIn(0f, 1f)

            val backgroundScrimAlpha = 0.55f * (1f - dragProgress)

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                // 2. Capa oscura que irá desapareciendo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = backgroundScrimAlpha
                            )
                        )
                )

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = dragOffset
                        }
                        .pointerInput(scrollState, onBack, dismissThreshold) {

                            awaitEachGesture {

                                val down = awaitFirstDown(
                                    requireUnconsumed = false,
                                    pass = PointerEventPass.Initial
                                )

                                var totalDrag = 0f

                                do {

                                    val event = awaitPointerEvent(
                                        PointerEventPass.Initial
                                    )

                                    val change = event.changes
                                        .firstOrNull { it.id == down.id }

                                    if (change != null && scrollState.value == 0) {

                                        val deltaY = change.position.y -
                                                change.previousPosition.y

                                        totalDrag = (totalDrag + deltaY)
                                            .coerceAtLeast(0f)

                                        if (totalDrag > 0f || deltaY > 0f) {

                                            dragOffset = totalDrag

                                            change.consume()
                                        }
                                    }

                                } while (event.changes.any { it.pressed })

                                // Decidir si regresamos al catálogo
                                val shouldDismiss = totalDrag >= dismissThreshold

                                // Ejecutar la animación dentro de una corrutina
                                scope.launch {

                                    val animation = Animatable(dragOffset)

                                    animation.animateTo(

                                        targetValue = if (shouldDismiss) {
                                            dismissalTarget
                                        } else {
                                            0f
                                        },

                                        animationSpec = if (shouldDismiss) {

                                            tween(
                                                durationMillis = 420,
                                                easing = LinearOutSlowInEasing
                                            )

                                        } else {

                                            spring(
                                                dampingRatio = 0.78f,
                                                stiffness = 350f
                                            )
                                        }

                                    ) {
                                        dragOffset = value
                                    }

                                    if (shouldDismiss) {
                                        onBack()
                                    }
                                }
                            }
                        }
                ) {

                    val alturaDisponible = this.maxHeight
                    val fotoHeight = alturaDisponible * 0.55f
                    val superposicion = 32.dp
                    val cardTop = fotoHeight - superposicion
                    val cardHeight = alturaDisponible - cardTop

                    // FOTO
                    if (!detalle.fotoUrl.isNullOrBlank()) {

                        AsyncImage(
                            model = RetrofitInstance.BASE_URL.trimEnd('/') +
                                    "/" + detalle.fotoUrl.trimStart('/'),

                            contentDescription = "Fotografía de ${detalle.nombre}",

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(fotoHeight)
                                .align(Alignment.TopCenter),

                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(fotoHeight)
                                .align(Alignment.TopCenter)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin fotografía",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // BOTÓN VOLVER
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .statusBarsPadding()
                            .padding(12.dp)
                            .size(44.dp),

                        shape = CircleShape,

                        color = Color.Black.copy(alpha = 0.45f)
                    ) {

                        IconButton(onClick = onBack) {
                            Text(
                                text = "‹",
                                color = Color.White,
                                fontSize = 32.sp
                            )
                        }
                    }

                    // TARJETA DE INFORMACIÓN
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = cardTop)
                            .fillMaxWidth()
                            .height(cardHeight),

                        shape = RoundedCornerShape(
                            topStart = 28.dp,
                            topEnd = 28.dp
                        ),

                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),

                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 8.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(
                                    horizontal = 22.dp,
                                    vertical = 24.dp
                                )
                        ) {

                            // Nombre
                            Text(
                                text = detalle.nombre,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Tipo y raza
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = "Tipo de mascota",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    Text(
                                        text = detalle.tipoMascota,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = "Raza",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    Text(
                                        text = detalle.raza ?: "No especificada",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Sexo y edad
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = "Sexo",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    Text(
                                        text = formatearSexo(detalle.sexo),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = "Edad",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    Text(
                                        text = formatearEdad(detalle),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            HorizontalDivider()

                            Spacer(modifier = Modifier.height(20.dp))

                            // Descripción
                            Text(
                                text = "Sobre ${detalle.nombre}",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = detalle.descripcion
                                    ?: "No hay descripción disponible.",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = onSolicitar,
                                enabled = !solicitudPendiente && !verificandoSolicitud,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = when {
                                        verificandoSolicitud -> "Verificando solicitud..."
                                        solicitudPendiente -> "Solicitud pendiente"
                                        else -> "Solicitar adopción"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatearSexo(sexo: String?): String {

    return when (sexo?.uppercase()) {
        "MACHO" -> "Macho"
        "HEMBRA" -> "Hembra"
        else -> "No especificado"
    }
}

private fun formatearEdad(mascota: MascotaResponse): String {

    return when {

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
}