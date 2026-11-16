package com.lovepaws.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovepaws.mobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow("Sin iniciar sesión")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _loginExitoso = MutableStateFlow(false)

    val loginExitoso: StateFlow<Boolean> =
        _loginExitoso.asStateFlow()

    private val _cerrandoSesion = MutableStateFlow(false)
    val cerrandoSesion: StateFlow<Boolean> = _cerrandoSesion.asStateFlow()

    private val _tokenStored = MutableStateFlow(false)
    val tokenStored: StateFlow<Boolean> = _tokenStored.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeTokenStored().collect { stored ->
                _tokenStored.value = stored
            }
        }
    }

    fun login(username: String, password: String) {
        _loginExitoso.value = false
        viewModelScope.launch {
            _mensaje.value = "Iniciar sesión..."
            try {
                val response = authRepository.login(username, password)

                if (response.isSuccessful) {
                    _mensaje.value = "¡Inicio de sesión exitoso!"
                    _loginExitoso.value = true
                } else {
                    _mensaje.value = "Error HTTP: ${response.code()}"
                }

            } catch (e: Exception) {
                _mensaje.value = "Error de conexión: ${e.message}"
            }
        }
    }

    fun cerrarSesion(onSesionCerrada: () -> Unit) {

        if (_cerrandoSesion.value) return
        viewModelScope.launch {
            _cerrandoSesion.value = true

            try {
                authRepository.cerrarSesion()

                _loginExitoso.value = false
                _mensaje.value = "Sin iniciar sesión"

                onSesionCerrada()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensaje.value = "No se pudo cerrar sesión: ${e.message}"

            } finally {
                _cerrandoSesion.value = false
            }
        }
    }
}
