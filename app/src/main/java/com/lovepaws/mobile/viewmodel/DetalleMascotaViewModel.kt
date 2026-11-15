package com.lovepaws.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovepaws.mobile.data.dto.MascotaResponse
import com.lovepaws.mobile.data.repository.MascotaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleMascotaViewModel(
    private val mascotaRepository: MascotaRepository = MascotaRepository()
) : ViewModel() {

    private val _mascota = MutableStateFlow<MascotaResponse?>(null)
    val mascota: StateFlow<MascotaResponse?> = _mascota.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private var cargaActual: Job? = null

    fun cargarDetalle(idMascota: Long) {

        // Cancelamos cualquier consulta anterior.
        cargaActual?.cancel()

        // Limpiamos el estado inmediatamente.
        _cargando.value = true
        _mascota.value = null
        _mensaje.value = "Consultando información..."

        cargaActual = viewModelScope.launch {

            try {

                val response =
                    mascotaRepository.obtenerDetalleMascota(idMascota)

                if (response.isSuccessful) {

                    val mascotaObtenida = response.body()

                    if (mascotaObtenida != null) {
                        _mascota.value = mascotaObtenida
                        _mensaje.value = "Información cargada correctamente."
                    } else {
                        _mensaje.value =
                            "No se encontró información de la mascota."
                    }

                } else {
                    _mensaje.value = "Error HTTP: ${response.code()}"
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                _mensaje.value =
                    "Error de conexión: ${e.message ?: "Error inesperado"}"

            } finally {

                // Solo la consulta vigente puede finalizar la carga.
                if (cargaActual === currentCoroutineContext()[Job]) {
                    _cargando.value = false
                }
            }
        }
    }
}
