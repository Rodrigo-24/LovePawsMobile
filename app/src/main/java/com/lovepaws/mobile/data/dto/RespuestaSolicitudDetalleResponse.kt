package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RespuestaSolicitudDetalleResponse(
    val pregunta: String,
    val respuesta: String,
    val ordenPregunta: Int
)