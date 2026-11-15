package com.lovepaws.mobile.data.repository

import com.lovepaws.mobile.data.dto.MascotaResponse
import com.lovepaws.mobile.data.remote.MascotaApi
import com.lovepaws.mobile.data.remote.RetrofitInstance
import retrofit2.Response

class MascotaRepository(
    private val mascotaApi: MascotaApi = RetrofitInstance.mascotaApi
) {

    suspend fun obtenerMascotas(): Response<List<MascotaResponse>> {
        return mascotaApi.obtenerMascotas()
    }

    suspend fun obtenerDetalleMascota(idMascota: Long): Response<MascotaResponse> {
        return mascotaApi.obtenerDetalleMascota(idMascota)
    }
}