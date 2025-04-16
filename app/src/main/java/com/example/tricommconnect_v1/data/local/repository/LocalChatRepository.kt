package com.example.tricommconnect_v1.data.local.repository

import com.example.tricommconnect_v1.data.local.dao.ChatDao
import com.example.tricommconnect_v1.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

class LocalChatRepository(
    private val chatDao: ChatDao
) {

    suspend fun saveChatsLocally(chatList: List<ChatEntity>) {
        chatDao.insertChats(chatList)
    }

    fun getLocalChats(): Flow<List<ChatEntity>> {
        return chatDao.getAllChats()
    }

    suspend fun clearAllChats() {
        chatDao.clearChats()
    }
}
