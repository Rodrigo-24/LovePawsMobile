package com.lovepaws.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovepaws.mobile.data.dto.ActualizarPerfilRequest
import com.lovepaws.mobile.data.dto.ApiErrorResponse
import com.lovepaws.mobile.data.dto.CambiarPasswordRequest
import com.lovepaws.mobile.data.dto.PerfilAdoptanteResponse
import com.lovepaws.mobile.data.repository.PerfilRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class PerfilViewModel(
    private val perfilRepository: PerfilRepository = PerfilRepository()
) : ViewModel() {

    // Datos del adoptante
    private val _perfil =
        MutableStateFlow<PerfilAdoptanteResponse?>(null)

    val perfil: StateFlow<PerfilAdoptanteResponse?> =
        _perfil.asStateFlow()

    // Estados de carga
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _actualizando = MutableStateFlow(false)
    val actualizando: StateFlow<Boolean> = _actualizando.asStateFlow()

    private val _cambiandoPassword = MutableStateFlow(false)
    val cambiandoPassword: StateFlow<Boolean> =
        _cambiandoPassword.asStateFlow()

    // Mensajes
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Resultados de las operaciones
    private val _perfilActualizado = MutableStateFlow(false)
    val perfilActualizado: StateFlow<Boolean> =
        _perfilActualizado.asStateFlow()

    private val _passwordCambiado = MutableStateFlow(false)
    val passwordCambiado: StateFlow<Boolean> =
        _passwordCambiado.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private var perfilCargado = false

    // CONSULTAR PERFIL
    fun cargarPerfil(forzar: Boolean = false) {

        if (perfilCargado && !forzar) return
        if (_cargando.value) return

        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = null

            try {

                val response = perfilRepository.obtenerPerfil()

                if (response.isSuccessful) {

                    val datos = response.body()

                    if (datos != null) {
                        _perfil.value = datos
                        perfilCargado = true
                    } else {
                        _mensaje.value = "No se recibieron los datos del perfil."
                    }

                } else {
                    _mensaje.value = obtenerMensajeError(
                        response.errorBody()?.string(),
                        response.code()
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensaje.value = "Error de conexión: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // ACTUALIZAR USUARIO Y CORREO
    fun actualizarPerfil(request: ActualizarPerfilRequest) {

        if (_actualizando.value) return

        viewModelScope.launch {

            _actualizando.value = true
            _perfilActualizado.value = false
            _mensaje.value = null

            try {

                val response = perfilRepository.actualizarPerfil(request)

                if (response.isSuccessful) {

                    val datos = response.body()

                    if (datos != null) {
                        _perfil.value = datos
                        _perfilActualizado.value = true
                        _mensaje.value = "Perfil actualizado correctamente."
                    } else {
                        _mensaje.value = "No se recibieron los datos actualizados."
                    }

                } else {
                    _mensaje.value = obtenerMensajeError(
                        response.errorBody()?.string(),
                        response.code()
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensaje.value = "Error de conexión: ${e.message}"
            } finally {
                _actualizando.value = false
            }
        }
    }

    // CAMBIAR CONTRASEÑA
    fun cambiarPassword(request: CambiarPasswordRequest) {

        if (_cambiandoPassword.value) return

        viewModelScope.launch {

            _cambiandoPassword.value = true
            _passwordCambiado.value = false
            _mensaje.value = null

            try {

                val response = perfilRepository.cambiarPassword(request)

                if (response.isSuccessful) {

                    _passwordCambiado.value = true
                    _mensaje.value = "Contraseña actualizada correctamente."

                } else {
                    _mensaje.value = obtenerMensajeError(
                        response.errorBody()?.string(),
                        response.code()
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensaje.value = "Error de conexión: ${e.message}"
            } finally {
                _cambiandoPassword.value = false
            }
        }
    }

    // LIMPIAR RESULTADOS ANTERIORES
    fun limpiarEstado() {
        _mensaje.value = null
        _perfilActualizado.value = false
        _passwordCambiado.value = false
    }

    // INTERPRETAR ERRORES DEL BACKEND
    private fun obtenerMensajeError(
        cuerpoError: String?,
        codigo: Int
    ): String {

        if (cuerpoError.isNullOrBlank()) {
            return "Error HTTP: $codigo"
        }

        return try {

            json.decodeFromString<ApiErrorResponse>(cuerpoError)
                .mensaje
                ?.takeIf { it.isNotBlank() }
                ?: "Error HTTP: $codigo"

        } catch (e: Exception) {
            "Error HTTP: $codigo"
        }
    }
}