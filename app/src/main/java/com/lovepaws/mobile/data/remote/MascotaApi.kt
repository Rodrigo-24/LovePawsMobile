package com.lovepaws.mobile.data.remote

import com.lovepaws.mobile.data.dto.MascotaResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MascotaApi {

    @GET ("api/adoptante/mascotas")
    suspend fun obtenerMascotas(): Response<List<MascotaResponse>>

    @GET("api/adoptante/mascotas/{idMascota}")
    suspend fun obtenerDetalleMascota(
        @Path("idMascota") idMascota: Long
    ): Response<MascotaResponse>
}