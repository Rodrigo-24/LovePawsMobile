package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PerfilAdoptanteResponse(
    val idUsuario: Long,
    val username: String,
    val email: String,
    val nombres: String,
    val apellidos: String? = null,
    val fotoPerfilUrl: String? = null,
    val rol: String,
    val estado: String,
    val fechaCreacion: String
)