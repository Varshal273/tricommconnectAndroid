package com.example.tricommconnect_v1.data.repository

import android.content.Context
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import com.example.tricommconnect_v1.data.local.mapper.toEntity
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.network.ApiService
import kotlinx.coroutines.flow.Flow
import com.example.tricommconnect_v1.data.local.repository.LocalMessageRepository
import com.example.tricommconnect_v1.data.local.db.AppDatabase
import com.example.tricommconnect_v1.data.model.SenderUser
import com.example.tricommconnect_v1.network.RetrofitInstance
import com.example.tricommconnect_v1.socket.MessageSocketHandler
import org.json.JSONObject

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

//    suspend fun sendMessage(chatId: String, senderId: String, msgBody: String): Message? {
//        val response = apiService.sendMessageToChat(
//            chatId,
//            mapOf("senderId" to senderId, "msgBody" to msgBody)
//        )
//        return response.newMessage
//    }

    suspend fun sendMessage(chatId: String, senderId: String, msgBody: String): Message {
        val payload = JSONObject().apply {
            put("chatId", chatId)
            put("senderId", senderId)
            put("msgBody", msgBody)
        }

        // Optimistically insert locally while socket emits
        val optimisticMsg = Message(
            _id = "local_${System.currentTimeMillis()}",
            chatId = chatId,
            senderUserId = SenderUser(senderId,""), // Username can be ignored or fetched from cache
            msgBody = msgBody,
            time = System.currentTimeMillis().toString()
        )

        saveMessagesToLocal(chatId, listOf(optimisticMsg))
        MessageSocketHandler.emitSendMessage(payload)

        return optimisticMsg // Immediately return optimistic message
    }


    companion object {
        @Volatile
        private var INSTANCE: MessageRepository? = null

        fun getInstance(context: Context): MessageRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val api = RetrofitInstance.api
                val dao = db.messageDao()
                INSTANCE ?: MessageRepository(api, dao).also { INSTANCE = it }
            }
        }
    }
}
