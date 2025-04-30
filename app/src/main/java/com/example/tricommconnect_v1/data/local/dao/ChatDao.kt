package com.example.tricommconnect_v1.data.local.dao

import androidx.room.*
import com.example.tricommconnect_v1.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    @Query("SELECT * FROM chats ORDER BY updatedAt DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("DELETE FROM chats")
    suspend fun clearChats()


}
