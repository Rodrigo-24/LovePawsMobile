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

import com.lovepaws.mobile.ui.screens.LoginScreen
import com.lovepaws.mobile.ui.screens.CatalogoScreen
import com.lovepaws.mobile.ui.screens.DetalleMascotaScreen
import com.lovepaws.mobile.viewmodel.AuthViewModel
import com.lovepaws.mobile.viewmodel.AuthViewModelFactory
import com.lovepaws.mobile.viewmodel.DetalleMascotaViewModel
import com.lovepaws.mobile.viewmodel.MascotaViewModel

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
                }
            )
        }

        composable("catalogo") {

            val mascotaViewModel: MascotaViewModel = viewModel()
            val detalleViewModel: DetalleMascotaViewModel = viewModel()

            var mascotaSeleccionada by remember {
                mutableStateOf<Long?>(null)
            }

            // El botón físico de volver cierra el detalle.
            BackHandler(enabled = mascotaSeleccionada != null) {
                mascotaSeleccionada = null
            }

            Box(modifier = Modifier.fillMaxSize()) {

                // El catálogo se dibuja una sola vez.
                CatalogoScreen(
                    mascotaViewModel = mascotaViewModel,
                    onMascotaClick = { idMascota ->
                        mascotaSeleccionada = idMascota
                    }
                )

                // El detalle aparece encima del catálogo.
                mascotaSeleccionada?.let { idMascota ->

                    DetalleMascotaScreen(
                        idMascota = idMascota,
                        detalleMascotaViewModel = detalleViewModel,
                        onBack = {
                            mascotaSeleccionada = null
                        }
                    )
                }
            }
        }
    }
}
