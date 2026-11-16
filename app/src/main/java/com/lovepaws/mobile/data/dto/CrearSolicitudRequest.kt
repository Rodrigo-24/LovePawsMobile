package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CrearSolicitudRequest(
    val idMascota: Long,
    val respuestas: List<RespuestaSolicitudRequest>
)