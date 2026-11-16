package com.lovepaws.mobile.data.remote

import android.content.Context
import com.lovepaws.mobile.data.local.TokenStorage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInstance {

    const val BASE_URL = "http://192.168.18.65:8080/"

    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val okHttpClient: OkHttpClient by lazy {

        val context = checkNotNull(appContext) {
            "RetrofitInstance no ha sido inicializado"
        }

        OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor(
                    TokenStorage(context)
                )
            )
            .build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    val retrofit: Retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val mascotaApi: MascotaApi by lazy {
        retrofit.create(MascotaApi::class.java)
    }

    val solicitudApi: SolicitudApi by lazy {
        retrofit.create(SolicitudApi::class.java)
    }
}