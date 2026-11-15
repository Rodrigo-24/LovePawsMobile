package com.lovepaws.mobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lovepaws.mobile.data.security.TokenCipher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tokenDataStore by preferencesDataStore(name = "lovepaws_secure_preferences")

class TokenStorage(context: Context, private val tokenCipher: TokenCipher = TokenCipher()) {

    private val dataStore = context.applicationContext.tokenDataStore

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("encrypted_access_token")
    }

    suspend fun saveToken(token: String) {

        require(token.isNotBlank()) {
            "El token no puede estar vacío"
        }

        val encryptedToken = tokenCipher.encrypt(token)

        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = encryptedToken
        }
    }

    val accessToken: Flow<String?> = dataStore.data.map { preferences ->

        preferences[ACCESS_TOKEN_KEY]?.let { encryptedToken ->
            tokenCipher.decrypt(encryptedToken)
        }
    }

    suspend fun clearToken() {

        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
        }
    }
}