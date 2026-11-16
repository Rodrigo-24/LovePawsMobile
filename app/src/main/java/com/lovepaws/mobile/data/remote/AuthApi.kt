package com.lovepaws.mobile.data.remote

import com.lovepaws.mobile.data.dto.LoginRequest
import com.lovepaws.mobile.data.dto.RegistroAdoptanteRequest
import com.lovepaws.mobile.data.dto.TokenResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login (@Body request: LoginRequest) : Response<TokenResponse>

    @POST("api/auth/registro")
    suspend fun registrarAdoptante(
        @Body request: RegistroAdoptanteRequest): Response<JsonElement>
}