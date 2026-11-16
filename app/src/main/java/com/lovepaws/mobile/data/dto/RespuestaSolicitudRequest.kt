package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RespuestaSolicitudRequest(
    val idPregunta: Long,
    val respuesta: String
)