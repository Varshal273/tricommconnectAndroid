package com.example.tricommconnect_v1.data.local.repository

import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

class LocalMessageRepository(
    private val messageDao: MessageDao
) {

    suspend fun insertMessage(message: MessageEntity) {
        messageDao.insertMessage(message)
    }

    suspend fun insertMessages(messages: List<MessageEntity>) {
        messageDao.insertMessages(messages)
    }

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChat(chatId)
    }

    suspend fun clearMessagesForChat(chatId: String) {
        messageDao.clearMessagesForChat(chatId)
    }

    suspend fun deleteAllMessages() {
        messageDao.deleteAllMessages()
    }

    suspend fun getPendingMessages(): List<MessageEntity> {
        return messageDao.getPendingMessages()
    }

    suspend fun getMessagesForChatOnce(chatId: String): List<MessageEntity> {
        return messageDao.getMessagesOnce(chatId)
    }
}
