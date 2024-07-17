package com.example.am_aes.Repository

import android.util.Log
import com.example.am_aes.Database.MessageDao
import com.example.am_aes.Utils.AESUtils
import com.example.am_aes.Database.MessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomMessageImpl @Inject constructor(private val messageDao: MessageDao) : MessageRepository {

    override suspend fun saveMessage(number: String, message: String, timestamp: Long) {
                val messageEntity = MessageEntity(
            number = number,
            message = message,
            timestamp = timestamp
        )

        withContext(Dispatchers.IO) {
            messageDao.insertMessage(messageEntity)
        }
    }


    override suspend fun getMessages(): Flow<List<Pair<String, List<Pair<String, Long>>>>> {
        return messageDao.getMessages().map { messages ->
            messages
                .mapNotNull { message ->
                    try {
                        val decryptedNumber = AESUtils.decrypt(message.number)
                        val decryptedMessage = AESUtils.decrypt(message.message)
                        val timestamp = message.timestamp
                        decryptedNumber to Pair(decryptedMessage, timestamp)
                    } catch (e: Exception) {
                        Log.e("RoomMessageRepo", "Decryption error: ${e.message}")
                        null // Return null for messages that couldn't be decrypted
                    }
                }
                .groupBy({ it.first }, { it.second })
                .map { (phoneNumber, messagesWithTimestamps) ->
                    phoneNumber to messagesWithTimestamps.toList()
                }
        }
    }

}