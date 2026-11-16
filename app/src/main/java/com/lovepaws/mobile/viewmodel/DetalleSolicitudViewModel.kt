package com.lovepaws.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovepaws.mobile.data.dto.SolicitudAdopcionDetalleResponse
import com.lovepaws.mobile.data.repository.SolicitudRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetalleSolicitudViewModel(
    private val solicitudRepository: SolicitudRepository = SolicitudRepository()
) : ViewModel() {

    private val _solicitud = MutableStateFlow<SolicitudAdopcionDetalleResponse?>(null)
    val solicitud: StateFlow<SolicitudAdopcionDetalleResponse?> = _solicitud

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun cargarDetalle(idSolicitud: Long) {
        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null
            _solicitud.value = null

            try {
                val response = solicitudRepository.obtenerDetalleSolicitud(idSolicitud)

                if (response.isSuccessful) {
                    _solicitud.value = response.body()
                } else {
                    _mensaje.value = "No se pudo cargar el detalle de la solicitud."
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value = "Error de conexión. Inténtalo nuevamente."
            } finally {
                _cargando.value = false
            }
        }
    }
}