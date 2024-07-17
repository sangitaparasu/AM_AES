package com.example.am_aes.Repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.am_aes.Utils.AESUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SharedPrefsMessageRepository @Inject constructor(private val context: Context) : MessageRepository {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("EncryptedMessages", Context.MODE_PRIVATE)
    }

    override suspend fun saveMessage(number: String, message: String, currentTimeMillis: Long) {

        val encryptedMessage = message
        val encryptedNumber = number
        val editor = prefs.edit()
        editor.putString(
            "message_${System.currentTimeMillis()}",
            "$encryptedNumber:$encryptedMessage:$currentTimeMillis"
        )
        editor.apply()
        Log.d("SharedPrefsMessageRepo", "Saved message: $encryptedNumber:$encryptedMessage:$currentTimeMillis")
    }

    override suspend fun getMessages(): Flow<List<Pair<String, List<Pair<String, Long>>>>> {
        return flow {
            val messageMap = mutableMapOf<String, MutableList<Pair<String, Long>>>()

            prefs.all.values.forEach { entry ->
                try {
                    val encryptedData = entry as String
                    val decryptedPair = encryptedData.split(":")
                    val decryptedNumber = AESUtils.decrypt(decryptedPair[0])
                    val decryptedMessage = AESUtils.decrypt(decryptedPair[1])
                    val timestamp = decryptedPair[2].toLongOrNull() ?: 0L

                    val pair = Pair(decryptedMessage, timestamp)
                    if (messageMap.containsKey(decryptedNumber)) {
                        messageMap[decryptedNumber]!!.add(pair)
                    } else {
                        messageMap[decryptedNumber] = mutableListOf(pair)
                    }
                } catch (e: Exception) {
                    Log.e("SharedPrefsMessageRepo", "Decryption error: ${e.message}")
                }
            }

            val groupedMessages = messageMap.map { (phoneNumber, messages) ->
                phoneNumber to messages.toList()

            }
            emit(groupedMessages)
        }

    }
}
