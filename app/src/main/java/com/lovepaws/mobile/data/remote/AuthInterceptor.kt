package com.lovepaws.mobile.data.remote

import com.lovepaws.mobile.data.local.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor

class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ) : okhttp3.Response {

        val originalRequest = chain.request()

        // Solo protegemos los endpoints del adoptante
        val path = originalRequest.url.encodedPath

        if (!path.startsWith("/api/adoptante/")) {
            return chain.proceed(originalRequest)
        }

        // Recuperamos el JWT almacenado
        val token = runBlocking (Dispatchers.IO) {
            tokenStorage.accessToken.first()
        }

        // Agregamos el encabezado de autorización
        val authenticatedRequest = if (!token.isNullOrBlank()) {

            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()

        } else {
            originalRequest
        }

        return chain.proceed(authenticatedRequest)
    }
}