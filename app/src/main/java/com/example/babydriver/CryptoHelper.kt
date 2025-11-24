
package com.example.babydriver

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {

    // ¡¡¡IMPORTANTE!!! Esta clave es para el ejemplo. En una app real, debe ser más compleja y segura.
    private const val SECRET_KEY = "BabyDriverSecretKey2024"
    private const val ALGORITHM = "AES"

    private fun getKey(): SecretKeySpec {
        val keyBytes = SECRET_KEY.toByteArray(Charsets.UTF_8)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    fun encrypt(password: String): String? {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, getKey())
            val encryptedBytes = cipher.doFinal(password.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun decrypt(encryptedString: String): String? {
        return try {
            val decodedBytes = Base64.decode(encryptedString, Base64.DEFAULT)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, getKey())
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
