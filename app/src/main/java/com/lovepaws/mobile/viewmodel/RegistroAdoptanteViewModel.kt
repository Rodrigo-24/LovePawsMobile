package com.lovepaws.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovepaws.mobile.data.dto.ApiErrorResponse
import com.lovepaws.mobile.data.dto.RegistroAdoptanteRequest
import com.lovepaws.mobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class RegistroAdoptanteViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso

    fun registrar(request: RegistroAdoptanteRequest) {

        if (_cargando.value) return

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null
            _registroExitoso.value = false

            try {
                val response = authRepository.registrarAdoptante(request)

                if (response.isSuccessful) {
                    _registroExitoso.value = true
                } else {
                    val cuerpoError = response.errorBody()?.string()

                    _mensaje.value = try {
                        cuerpoError?.let {
                            Json { ignoreUnknownKeys = true }
                                .decodeFromString<ApiErrorResponse>(it)
                                .mensaje
                        } ?: "No se pudo crear la cuenta."
                    } catch (e: Exception) {
                        "No se pudo crear la cuenta."
                    }
                }

            } catch (e: Exception) {
                _mensaje.value = "No se pudo conectar con el servidor."
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarEstado() {
        _mensaje.value = null
        _registroExitoso.value = false
    }
}