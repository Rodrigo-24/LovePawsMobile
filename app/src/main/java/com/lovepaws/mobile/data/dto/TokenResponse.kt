package com.lovepaws.mobile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse (
    val accessToken : String,
    val tokenType : String,
    val expiresIn : Long
)