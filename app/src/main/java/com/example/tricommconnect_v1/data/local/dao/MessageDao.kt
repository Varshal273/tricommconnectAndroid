package com.example.tricommconnect_v1.data.local.dao

import androidx.room.*
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE status = 'PENDING'")
    suspend fun getPendingMessages(): List<MessageEntity>

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearMessagesForChat(chatId: String)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    suspend fun getMessagesOnce(chatId: String): List<MessageEntity>
}
