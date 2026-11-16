package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudAdopcionDetalleResponse(
    val idSolicitud: Long,
    val idMascota: Long,
    val nombreMascota: String,
    val tipoMascota: String,
    val fotoMascotaUrl: String?,
    val nombresAdoptante: String,
    val apellidosAdoptante: String,
    val correoAdoptante: String,
    val fechaSolicitud: String,
    val estado: String,
    val fechaResolucion: String?,
    val nombreResponsable: String?,
    val observacionResolucion: String?,
    val respuestas: List<RespuestaSolicitudDetalleResponse>
)