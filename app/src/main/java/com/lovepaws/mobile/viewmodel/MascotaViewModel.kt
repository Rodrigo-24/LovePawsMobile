package com.lovepaws.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovepaws.mobile.data.dto.MascotaResponse
import com.lovepaws.mobile.data.repository.MascotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MascotaViewModel(
    private val mascotaRepository: MascotaRepository = MascotaRepository()
) : ViewModel() {

    private val _mascotas =
        MutableStateFlow<List<MascotaResponse>>(emptyList())

    val mascotas: StateFlow<List<MascotaResponse>> =
        _mascotas.asStateFlow()

    private val _mensaje =
        MutableStateFlow("Aún no se han consultado mascotas")

    val mensaje: StateFlow<String> =
        _mensaje.asStateFlow()

    private val _cargando = MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    private var catalogoCargado = false

    fun cargarMascotas(forzar: Boolean = false) {

        // Evitar consultas innecesarias
        if (catalogoCargado && !forzar) {
            return
        }

        // Evitar consultas simultáneas
        if (_cargando.value) {
            return
        }

        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = "Consultando mascotas..."

            try {

                val response = mascotaRepository.obtenerMascotas()

                if (response.isSuccessful) {

                    _mascotas.value = response.body().orEmpty()

                    // Registrar que el catálogo ya fue cargado
                    catalogoCargado = true

                    _mensaje.value = if (_mascotas.value.isEmpty()) {
                        "No hay mascotas disponibles."
                    } else {
                        "Mascotas cargadas correctamente."
                    }

                } else {

                    _mensaje.value = "Error HTTP: ${response.code()}"
                }

            } catch (e: Exception) {

                _mensaje.value =
                    "Error de conexión: ${e.message ?: "Error inesperado"}"

            } finally {

                _cargando.value = false
            }
        }
    }
}