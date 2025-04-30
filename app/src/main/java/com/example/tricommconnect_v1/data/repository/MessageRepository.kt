package com.example.tricommconnect_v1.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import com.example.tricommconnect_v1.data.local.mapper.toEntity
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.network.ApiService
import kotlinx.coroutines.flow.Flow

class MessageRepository(
    private val apiService: ApiService,
    private val messageDao: MessageDao
) {
    // Local: Room DB
    fun getLocalMessagesFlow(chatId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChat(chatId)
    }

    suspend fun getLocalMessagesOnce(chatId: String): List<MessageEntity> {
        return messageDao.getMessagesOnce(chatId)
    }

//    suspend fun saveMessagesToLocal(messages: List<Message>) {
//        val entities = messages.map { it.toEntity() }
//        messageDao.insertMessages(entities)
//    }
//
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveMessagesToLocal(chatId: String, messages: List<Message>) {
        val entities = messages.map { msg ->
            msg.toEntity().copy(chatId = chatId)
        }
        messageDao.insertMessages(entities)
    }
    suspend fun clearLocalMessages(chatId: String) {
        messageDao.clearMessagesForChat(chatId)
    }

    // Remote: API
    suspend fun fetchRemoteMessages(chatId: String): List<Message> {
        val response = apiService.getMessagesForChat(chatId)
        return response.messages
    }

    suspend fun sendMessage(chatId: String, senderId: String, msgBody: String): Message? {
        val response = apiService.sendMessageToChat(
            chatId,
            mapOf("senderId" to senderId, "msgBody" to msgBody)
        )
        return response.newMessage
    }
}
