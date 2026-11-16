package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val mensaje: String? = null
)