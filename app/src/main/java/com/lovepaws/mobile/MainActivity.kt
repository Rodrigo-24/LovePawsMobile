package com.lovepaws.mobile

import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.lovepaws.mobile.data.remote.RetrofitInstance
import com.lovepaws.mobile.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mantener un comportamiento edge-to-edge uniforme
        enableEdgeToEdge()

        // Inicializar Retrofit
        RetrofitInstance.initialize(applicationContext)

        // Iniciar la aplicación
        setContent {
            MaterialTheme {

                // Mantener todo el contenido dentro del área segura
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}