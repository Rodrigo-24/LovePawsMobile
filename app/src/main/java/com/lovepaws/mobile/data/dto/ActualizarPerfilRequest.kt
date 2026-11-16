package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActualizarPerfilRequest(
    val username: String,
    val email: String,
    val passwordActual: String
)