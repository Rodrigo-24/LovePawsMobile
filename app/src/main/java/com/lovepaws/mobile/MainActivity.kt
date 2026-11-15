package com.lovepaws.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.lovepaws.mobile.data.remote.RetrofitInstance
import com.lovepaws.mobile.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Retrofit
        RetrofitInstance.initialize(applicationContext)

        // Iniciar la aplicación
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}