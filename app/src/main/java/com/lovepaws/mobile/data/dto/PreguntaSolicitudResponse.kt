package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class PreguntaSolicitudResponse (
    val idPregunta: Long,
    val texto: String,
    val tipoRespuesta: String,
    val opciones: JsonElement? = null,
    val orden: Int,
    val obligatoria: Boolean
)