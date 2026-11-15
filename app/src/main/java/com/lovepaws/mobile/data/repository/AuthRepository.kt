package com.lovepaws.mobile.data.repository

import com.lovepaws.mobile.data.dto.LoginRequest
import com.lovepaws.mobile.data.dto.TokenResponse
import com.lovepaws.mobile.data.local.TokenStorage
import com.lovepaws.mobile.data.remote.AuthApi
import com.lovepaws.mobile.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

class AuthRepository (
    private val tokenStorage: TokenStorage,
    private val authApi : AuthApi = RetrofitInstance.authApi) {

    suspend fun login(username : String, password : String) : Response<TokenResponse> {

        val request = LoginRequest(username = username, password = password)
        val response = authApi.login(request)

        if (response.isSuccessful) {
            val token = response.body()?.accessToken
                ?: throw IllegalStateException("El servidor no devolvió un token")

            tokenStorage.saveToken(token)
        }

        return response
    }

    fun observeTokenStored(): Flow<Boolean> {
        return tokenStorage.accessToken.map { token ->
            !token.isNullOrBlank()
        }
    }
}