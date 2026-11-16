package com.lovepaws.mobile.data.remote

import com.lovepaws.mobile.data.dto.CrearSolicitudRequest
import com.lovepaws.mobile.data.dto.PreguntaSolicitudResponse
import com.lovepaws.mobile.data.dto.SolicitudAdopcionDetalleResponse
import com.lovepaws.mobile.data.dto.SolicitudAdopcionResponse
import com.lovepaws.mobile.data.dto.SolicitudEstadoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SolicitudApi {

    @GET("api/adoptante/solicitudes/preguntas")
    suspend fun obtenerPreguntas(): Response<List<PreguntaSolicitudResponse>>

    @POST("api/adoptante/solicitudes")
    suspend fun crearSolicitud(
        @Body request: CrearSolicitudRequest
    ): Response<SolicitudAdopcionResponse>

    @GET("api/adoptante/solicitudes")
    suspend fun obtenerSolicitudes():
            Response<List<SolicitudEstadoResponse>>

    @GET("api/adoptante/solicitudes/{id}")
    suspend fun obtenerDetalleSolicitud(
        @Path("id") idSolicitud: Long
    ): Response<SolicitudAdopcionDetalleResponse>
}