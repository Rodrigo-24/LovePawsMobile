package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegistroAdoptanteRequest(
    val username: String,
    val email: String,
    val password: String,
    val nombres: String,
    val apellidos: String
)