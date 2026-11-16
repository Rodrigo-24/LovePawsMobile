package com.lovepaws.mobile.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.lovepaws.mobile.ui.screen.DetalleSolicitudScreen

import com.lovepaws.mobile.ui.screens.LoginScreen
import com.lovepaws.mobile.ui.screens.CatalogoScreen
import com.lovepaws.mobile.ui.screens.DetalleMascotaScreen
import com.lovepaws.mobile.ui.screens.MisAdopcionesScreen
import com.lovepaws.mobile.ui.screens.RegistroAdoptanteScreen
import com.lovepaws.mobile.ui.screens.SolicitudAdopcionScreen
import com.lovepaws.mobile.viewmodel.AuthViewModel
import com.lovepaws.mobile.viewmodel.AuthViewModelFactory
import com.lovepaws.mobile.viewmodel.DetalleMascotaViewModel
import com.lovepaws.mobile.viewmodel.MascotaViewModel
import com.lovepaws.mobile.viewmodel.MisAdopcionesViewModel
import com.lovepaws.mobile.viewmodel.RegistroAdoptanteViewModel
import com.lovepaws.mobile.viewmodel.SolicitudViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext)
    )

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        composable("login") {

            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("catalogo") {
                        popUpTo("login") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onCrearCuenta = {
                    navController.navigate("registro")
                }
            )
        }

        composable("registro") {

            val registroViewModel: RegistroAdoptanteViewModel = viewModel(
                factory = AuthViewModelFactory(context.applicationContext)
            )

            RegistroAdoptanteScreen(
                registroViewModel = registroViewModel,
                onRegistroExitoso = {
                    navController.navigate("login") {
                        popUpTo("registro") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onVolverLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("catalogo") {

            val mascotaViewModel: MascotaViewModel = viewModel()
            val detalleViewModel: DetalleMascotaViewModel = viewModel()
            val solicitudViewModel: SolicitudViewModel = viewModel()

            val mascotasConSolicitudPendiente by mascotaViewModel.mascotasConSolicitudPendiente.collectAsState()
            val cargandoSolicitudes by mascotaViewModel.cargandoSolicitudes.collectAsState()
            val misAdopcionesViewModel: MisAdopcionesViewModel = viewModel()

            var mascotaSeleccionada by remember { mutableStateOf<Long?>(null) }
            var mostrarSolicitud by remember { mutableStateOf(false) }
            var mostrarMisAdopciones by remember { mutableStateOf(false) }
            var solicitudSeleccionada by remember { mutableStateOf<Long?>(null) }

            BackHandler(
                enabled = solicitudSeleccionada != null ||
                        mostrarSolicitud ||
                        mostrarMisAdopciones ||
                        mascotaSeleccionada != null
            ) {
                when {
                    solicitudSeleccionada != null -> solicitudSeleccionada = null
                    mostrarSolicitud -> mostrarSolicitud = false
                    mostrarMisAdopciones -> mostrarMisAdopciones = false
                    else -> mascotaSeleccionada = null
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {

                // El catálogo se dibuja una sola vez.
                CatalogoScreen(
                    mascotaViewModel = mascotaViewModel,
                    onMascotaClick = { idMascota ->
                        mascotaSeleccionada = idMascota
                        mostrarSolicitud = false
                    },
                    onMisAdopcionesClick = {
                        mostrarMisAdopciones = true
                    }
                )

                if (solicitudSeleccionada != null) {
                    DetalleSolicitudScreen (
                        idSolicitud = solicitudSeleccionada!!,
                        onBack = {
                            solicitudSeleccionada = null
                        }
                    )
                } else if (mostrarMisAdopciones) {
                    MisAdopcionesScreen(
                        viewModel = misAdopcionesViewModel,
                        onSolicitudClick = { idSolicitud ->
                            solicitudSeleccionada = idSolicitud
                        },
                        onBack = {
                            mostrarMisAdopciones = false
                        }
                    )
                } else {

                    mascotaSeleccionada?.let { idMascota ->

                        DetalleMascotaScreen(
                            idMascota = idMascota,
                            detalleMascotaViewModel = detalleViewModel,
                            onBack = {
                                mascotaSeleccionada = null
                            },
                            onSolicitar = {
                                mostrarSolicitud = true
                            },
                            solicitudPendiente = idMascota in mascotasConSolicitudPendiente,
                            verificandoSolicitud = cargandoSolicitudes
                        )

                        if (mostrarSolicitud) {

                            SolicitudAdopcionScreen (
                                idMascota = idMascota,
                                solicitudViewModel = solicitudViewModel,
                                onBack = {
                                    mostrarSolicitud = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
