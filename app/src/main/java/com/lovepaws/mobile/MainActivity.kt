package com.lovepaws.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.lovepaws.mobile.data.remote.RetrofitInstance
import com.lovepaws.mobile.ui.navigation.AppNavigation
import com.lovepaws.mobile.ui.theme.LovePawsMobileTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        RetrofitInstance.initialize(applicationContext)

        setContent {
            LovePawsMobileTheme {
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