package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MascotaResponse(
    val idMascota: Long,
    val nombre: String,
    val tipoMascota: String,
    val raza: String? = null,
    val edadAnios: Int? = null,
    val edadMeses: Int? = null,
    val sexo: String? = null,
    val descripcion: String? = null,
    val fotoUrl: String? = null
)