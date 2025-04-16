package com.example.tricommconnect_v1.data.remote.repository

import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteMessageRepository(
    private val apiService: ApiService
) {
    suspend fun sendMessage(chatId: String, senderId: String, messageBody: String): Message? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.sendMessageToChat(
                    chatId = chatId,
                    requestBody = mapOf(
                        "senderId" to senderId,
                        "msgBody" to messageBody
                    )
                )
                response.newMessage
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun fetchMessages(chatId: String): List<Message> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMessagesForChat(chatId)
                response.messages
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
