package com.example.tricommconnect_v1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val chatId: String,
    val groupName: String,
    val isGroup: Boolean,
    val lastMessage: String? = null,
    val updatedAt: Long
)
