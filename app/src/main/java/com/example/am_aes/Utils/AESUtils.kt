package com.example.am_aes.Utils

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import com.example.am_aes.BuildConfig
import java.security.SecureRandom

object AESUtils {
    private const val AES = "AES"
    private const val AES_MODE = "AES/CBC/PKCS7Padding"
    private const val KEY = BuildConfig.API_KEY
    private const val IV_SIZE = 16

    @Throws(Exception::class)
    fun encrypt(data: String): String {
        val keySpec = SecretKeySpec(KEY.toByteArray(), AES)
        val cipher = Cipher.getInstance(AES_MODE)

        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encryptedBytes = cipher.doFinal(data.toByteArray())

        val ivAndEncryptedData = iv + encryptedBytes
        return Base64.encodeToString(ivAndEncryptedData, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun decrypt(data: String): String {
        val ivAndEncryptedData = Base64.decode(data, Base64.DEFAULT)


        val iv = ivAndEncryptedData.copyOfRange(0, IV_SIZE)
        val encryptedData = ivAndEncryptedData.copyOfRange(IV_SIZE, ivAndEncryptedData.size)

        val keySpec = SecretKeySpec(KEY.toByteArray(), AES)
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptedBytes = cipher.doFinal(encryptedData)

        return String(decryptedBytes)
    }
}
