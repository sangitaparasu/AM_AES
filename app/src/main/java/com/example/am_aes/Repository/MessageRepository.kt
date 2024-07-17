package com.example.am_aes.Repository

import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun saveMessage(number: String, message: String, currentTimeMillis: Long)
    suspend fun getMessages(): Flow<List<Pair<String, List<Pair<String, Long>>>>>

}