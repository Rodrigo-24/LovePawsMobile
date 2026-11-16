package com.lovepaws.mobile.data.repository

import com.lovepaws.mobile.data.dto.ActualizarPerfilRequest
import com.lovepaws.mobile.data.dto.CambiarPasswordRequest
import com.lovepaws.mobile.data.remote.PerfilApi
import com.lovepaws.mobile.data.remote.RetrofitInstance
import com.lovepaws.mobile.data.dto.PerfilAdoptanteResponse
import retrofit2.Response

class PerfilRepository(
    private val perfilApi: PerfilApi = RetrofitInstance.perfilApi
) {

    suspend fun obtenerPerfil(): Response<PerfilAdoptanteResponse> {
        return perfilApi.obtenerPerfil()
    }

    suspend fun actualizarPerfil(
        request: ActualizarPerfilRequest
    ): Response<PerfilAdoptanteResponse> {
        return perfilApi.actualizarPerfil(request)
    }

    suspend fun cambiarPassword(
        request: CambiarPasswordRequest
    ): Response<Unit> {
        return perfilApi.cambiarPassword(request)
    }
}