package com.lovepaws.mobile.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lovepaws.mobile.data.local.TokenStorage
import com.lovepaws.mobile.data.repository.AuthRepository

class AuthViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        val tokenStorage = TokenStorage(
            context.applicationContext
        )

        val authRepository = AuthRepository(
            tokenStorage = tokenStorage
        )

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }

        if (modelClass.isAssignableFrom(RegistroAdoptanteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistroAdoptanteViewModel(authRepository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}