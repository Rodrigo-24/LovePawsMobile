package com.lovepaws.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovepaws.mobile.data.dto.SolicitudEstadoResponse
import com.lovepaws.mobile.data.repository.SolicitudRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MisAdopcionesViewModel(
    private val solicitudRepository: SolicitudRepository =
        SolicitudRepository()
) : ViewModel() {

    // Solicitudes del adoptante
    private val _solicitudes =
        MutableStateFlow<List<SolicitudEstadoResponse>>(emptyList())

    val solicitudes: StateFlow<List<SolicitudEstadoResponse>> =
        _solicitudes.asStateFlow()

    // Estado de carga
    private val _cargando = MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    // Mensajes para la interfaz
    private val _mensaje = MutableStateFlow("")

    val mensaje: StateFlow<String> =
        _mensaje.asStateFlow()

    private var solicitudesCargadas = false

    fun cargarSolicitudes(forzar: Boolean = false) {

        if (solicitudesCargadas && !forzar) {
            return
        }

        if (_cargando.value) {
            return
        }

        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = "Consultando solicitudes..."

            try {

                val response =
                    solicitudRepository.obtenerSolicitudes()

                if (response.isSuccessful) {

                    _solicitudes.value = response.body().orEmpty()

                    solicitudesCargadas = true

                    _mensaje.value =
                        if (_solicitudes.value.isEmpty()) {
                            "Aún no tienes solicitudes registradas."
                        } else {
                            "Solicitudes cargadas correctamente."
                        }

                } else {

                    _mensaje.value =
                        "Error al consultar solicitudes: HTTP ${response.code()}"
                }

            } catch (e: CancellationException) {

                throw e

            } catch (e: Exception) {

                _mensaje.value =
                    "Error de conexión: ${e.message ?: "Error inesperado"}"

            } finally {

                _cargando.value = false
            }
        }
    }
}