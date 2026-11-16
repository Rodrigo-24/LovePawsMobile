package com.lovepaws.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovepaws.mobile.data.dto.CrearSolicitudRequest
import com.lovepaws.mobile.data.dto.RespuestaSolicitudRequest
import com.lovepaws.mobile.data.dto.PreguntaSolicitudResponse
import com.lovepaws.mobile.data.dto.SolicitudAdopcionResponse
import com.lovepaws.mobile.data.repository.SolicitudRepository
import com.lovepaws.mobile.data.dto.ApiErrorResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SolicitudViewModel(
    private val solicitudRepository: SolicitudRepository = SolicitudRepository()
) : ViewModel() {

    // Preguntas del formulario
    private val _preguntas =
        MutableStateFlow<List<PreguntaSolicitudResponse>>(emptyList())

    val preguntas: StateFlow<List<PreguntaSolicitudResponse>> =
        _preguntas.asStateFlow()

    // Respuestas del adoptante, identificadas por el ID de pregunta
    private val _respuestas =
        MutableStateFlow<Map<Long, String>>(emptyMap())

    val respuestas: StateFlow<Map<Long, String>> =
        _respuestas.asStateFlow()

    // Estados de carga
    private val _cargandoPreguntas = MutableStateFlow(false)

    val cargandoPreguntas: StateFlow<Boolean> =
        _cargandoPreguntas.asStateFlow()

    private val _enviandoSolicitud = MutableStateFlow(false)

    val enviandoSolicitud: StateFlow<Boolean> =
        _enviandoSolicitud.asStateFlow()

    // Mensaje para la interfaz
    private val _mensaje = MutableStateFlow("")

    val mensaje: StateFlow<String> =
        _mensaje.asStateFlow()

    // Resultado del registro
    private val _solicitudCreada =
        MutableStateFlow<SolicitudAdopcionResponse?>(null)

    val solicitudCreada: StateFlow<SolicitudAdopcionResponse?> =
        _solicitudCreada.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private var preguntasCargadas = false

    fun cargarPreguntas() {

        if (preguntasCargadas || _cargandoPreguntas.value) {
            return
        }

        viewModelScope.launch {

            _cargandoPreguntas.value = true
            _mensaje.value = "Consultando preguntas..."

            try {

                val response = solicitudRepository.obtenerPreguntas()

                if (response.isSuccessful) {

                    val preguntasObtenidas = response.body()

                    if (preguntasObtenidas != null) {

                        _preguntas.value = preguntasObtenidas
                        _respuestas.value = emptyMap()
                        preguntasCargadas = true

                        _mensaje.value =
                            if (preguntasObtenidas.isEmpty()) {
                                "No hay preguntas disponibles."
                            } else {
                                "Preguntas cargadas correctamente."
                            }

                    } else {
                        _mensaje.value = "No se pudieron obtener las preguntas."
                    }

                } else {

                    val cuerpoError = response.errorBody()?.string()

                    _mensaje.value = obtenerMensajeError(
                        cuerpoError,
                        response.code()
                    )
                }

            } catch (e: Exception) {

                _mensaje.value =
                    "Error de conexión: ${e.message ?: "Error inesperado"}"

            } finally {
                _cargandoPreguntas.value = false
            }
        }
    }

    fun actualizarRespuesta(idPregunta: Long, respuesta: String) {

        _respuestas.value = _respuestas.value +
                (idPregunta to respuesta)
    }

    fun crearSolicitud(idMascota: Long) {

        if (_enviandoSolicitud.value) {
            return
        }

        // Comprobar que se hayan cargado las preguntas
        if (!preguntasCargadas || _preguntas.value.isEmpty()) {
            _mensaje.value = "No hay preguntas disponibles para enviar."
            return
        }

        // Validar las preguntas obligatorias
        val preguntaSinResponder = _preguntas.value.firstOrNull { pregunta ->

            pregunta.obligatoria &&
                    _respuestas.value[pregunta.idPregunta].isNullOrBlank()
        }

        if (preguntaSinResponder != null) {
            _mensaje.value =
                "Debes responder: ${preguntaSinResponder.texto}"
            return
        }

        // Preparar todas las respuestas
        val respuestasSolicitud = _preguntas.value.map { pregunta ->

            RespuestaSolicitudRequest(
                idPregunta = pregunta.idPregunta,
                respuesta = _respuestas.value[pregunta.idPregunta].orEmpty()
            )
        }

        val request = CrearSolicitudRequest(
            idMascota = idMascota,
            respuestas = respuestasSolicitud
        )

        viewModelScope.launch {

            _enviandoSolicitud.value = true
            _solicitudCreada.value = null
            _mensaje.value = "Enviando solicitud..."

            try {

                val response =
                    solicitudRepository.crearSolicitud(request)

                if (response.isSuccessful) {

                    val resultado = response.body()

                    if (resultado != null) {

                        _solicitudCreada.value = resultado
                        _mensaje.value =
                            "Solicitud registrada correctamente."

                    } else {
                        _mensaje.value =
                            "La solicitud fue enviada, pero no se recibió una respuesta válida."
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

                _enviandoSolicitud.value = false
            }
        }
    }
    fun reiniciarFormulario() {

        // Limpiar las respuestas anteriores.
        _respuestas.value = emptyMap()

        // Limpiar mensajes y resultados anteriores.
        _mensaje.value = ""
        _solicitudCreada.value = null
    }

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
