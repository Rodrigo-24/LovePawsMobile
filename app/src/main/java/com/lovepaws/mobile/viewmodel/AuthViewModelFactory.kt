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

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {

            val tokenStorage = TokenStorage(
                context.applicationContext
            )

            val authRepository = AuthRepository(
                tokenStorage = tokenStorage
            )

            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido"
        )
    }
}