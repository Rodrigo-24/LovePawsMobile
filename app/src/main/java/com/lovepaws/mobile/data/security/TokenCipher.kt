package com.lovepaws.mobile.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class TokenCipher {

    companion object {
        private const val KEY_ALIAS = "lovepaws_token_key"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_LENGTH = 12
    }

    fun encrypt(token : String) : String {

        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())

        val iv = cipher.iv

        val encryptedBytes = cipher.doFinal(token.toByteArray(Charsets.UTF_8))

        val combined = iv +encryptedBytes

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decrypt(encryptedToken : String) : String {

        val combined = Base64.decode(encryptedToken, Base64.NO_WRAP)

        require(combined.size > IV_LENGTH + 16) {
            "El token cifrado no es válido"
        }

        val iv = combined.copyOfRange(0, IV_LENGTH)

        val encryptedBytes = combined.copyOfRange(IV_LENGTH, combined.size)

        val key = getExistingKey() ?: throw IllegalStateException("No se encontró la clave de cifrado")

        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))

        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, Charsets.UTF_8)
    }

    private fun getOrCreateKey(): SecretKey {

        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey

        if (existingKey != null) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEY_STORE
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(keySpec)

        return keyGenerator.generateKey()
    }

    private fun getExistingKey(): SecretKey? {

        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
    }
}