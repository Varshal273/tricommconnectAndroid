package com.example.tricommconnect_v1.data.repository

import com.example.tricommconnect_v1.data.local.dao.ChatDao
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.entity.ChatEntity
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

class LocalChatRepository(private val chatDao: ChatDao, private val messageDao: MessageDao) {


    fun getAllChats(): Flow<List<ChatEntity>> = chatDao.getAllChats()

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> = messageDao.getMessagesForChat(chatId)

    suspend fun insertChat(chat: List<ChatEntity>) {
        chatDao.insertChats(chat)
    }

    suspend fun insertMessage(message: MessageEntity) {
        messageDao.insert(message)
    }
}
