package com.example.tricommconnect_v1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tricommconnect_v1.data.model.Chat

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val chatId: String,
    val groupName: String,
    val isGroup: Boolean,
    val lastMessage: String? = null,
    val updatedAt: Long
) {
    // ✅ Added: Converts Chat (from API) to ChatEntity (for Room)
    companion object {
        fun fromChat(chat: Chat): ChatEntity {
            return ChatEntity(
                chatId = chat._id,
                groupName = chat.groupName,
                isGroup = chat.group.size > 2, // ✅ Assumes group if more than 2 users
                lastMessage = chat.lastMessage?.msgBody,
                updatedAt = chat.lastMessage?.time?.toLongOrNull() ?: 0L // ✅ Time conversion to Long
            )
        }
    }
}