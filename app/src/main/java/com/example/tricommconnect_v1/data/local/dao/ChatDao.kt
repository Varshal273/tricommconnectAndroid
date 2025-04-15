package com.example.tricommconnect_v1.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tricommconnect_v1.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    @Query("SELECT * FROM chats ORDER BY timestamp DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("DELETE FROM chats")
    suspend fun clearChats()

    @Query("SELECT * FROM chats WHERE id = :id LIMIT 1")  // Changed from 'chatId' to 'id'
    suspend fun getChatById(id: Long): ChatEntity?  // Changed from 'chatId' to 'id'
}
