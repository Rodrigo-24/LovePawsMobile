package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudAdopcionResponse(
    val idSolicitud: Long,
    val estado: String
)