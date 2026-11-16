package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudEstadoResponse(
    val idSolicitud: Long,
    val idMascota: Long,
    val nombreMascota: String,
    val tipoMascota: String,
    val fechaSolicitud: String,
    val estado: String
)