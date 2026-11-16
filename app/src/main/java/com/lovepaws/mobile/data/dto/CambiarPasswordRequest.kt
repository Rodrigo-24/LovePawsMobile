package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CambiarPasswordRequest(
    val passwordActual: String,
    val passwordNueva: String,
    val confirmarPassword: String
)