package com.lovepaws.mobile.data.repository

import com.lovepaws.mobile.data.dto.CrearSolicitudRequest
import com.lovepaws.mobile.data.dto.PreguntaSolicitudResponse
import com.lovepaws.mobile.data.dto.SolicitudAdopcionDetalleResponse
import com.lovepaws.mobile.data.dto.SolicitudAdopcionResponse
import com.lovepaws.mobile.data.dto.SolicitudEstadoResponse
import com.lovepaws.mobile.data.remote.RetrofitInstance
import com.lovepaws.mobile.data.remote.SolicitudApi
import retrofit2.Response

class SolicitudRepository(
    private val solicitudApi: SolicitudApi = RetrofitInstance.solicitudApi
) {

    suspend fun obtenerPreguntas():
            Response<List<PreguntaSolicitudResponse>> {

        return solicitudApi.obtenerPreguntas()
    }

    suspend fun crearSolicitud(
        request: CrearSolicitudRequest
    ): Response<SolicitudAdopcionResponse> {

        return solicitudApi.crearSolicitud(request)
    }

    suspend fun obtenerSolicitudes():
            Response<List<SolicitudEstadoResponse>> {

        return solicitudApi.obtenerSolicitudes()
    }

    suspend fun obtenerDetalleSolicitud(
        idSolicitud: Long
    ): Response<SolicitudAdopcionDetalleResponse> {
        return solicitudApi.obtenerDetalleSolicitud(idSolicitud)
    }
}