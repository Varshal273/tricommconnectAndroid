package com.example.tricommconnect_v1.data.repository

import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

class MessageRepository(
    private val messageDao: MessageDao
) {

    // Save a new message locally
    suspend fun insertMessage(message: MessageEntity) {
        messageDao.insert(message)
    }

    // Fetch all messages for a given chatId
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChat(chatId)
    }

    // Delete messages for a chat (e.g. on logout or chat delete)
    suspend fun clearMessagesForChat(chatId: String) {
        messageDao.deleteMessagesForChat(chatId)
    }
}
