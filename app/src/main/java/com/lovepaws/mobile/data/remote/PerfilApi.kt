package com.lovepaws.mobile.data.remote

import com.lovepaws.mobile.data.dto.ActualizarPerfilRequest
import com.lovepaws.mobile.data.dto.CambiarPasswordRequest
import com.lovepaws.mobile.data.dto.PerfilAdoptanteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface PerfilApi {

    @GET("api/adoptante/perfil")
    suspend fun obtenerPerfil(): Response<PerfilAdoptanteResponse>

    @PUT("api/adoptante/perfil")
    suspend fun actualizarPerfil(
        @Body request: ActualizarPerfilRequest
    ): Response<PerfilAdoptanteResponse>

    @PUT("api/adoptante/perfil/password")
    suspend fun cambiarPassword(
        @Body request: CambiarPasswordRequest
    ): Response<Unit>
}