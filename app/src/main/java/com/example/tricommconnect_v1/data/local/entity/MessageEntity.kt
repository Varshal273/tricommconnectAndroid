package com.example.tricommconnect_v1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val messageId: String,
    val chatId: String,
    val senderId: String,
    val senderUsername: String? = null, // 👈 Added
    val content: String,
    val timestamp: Long,
    val status: MessageStatus = MessageStatus.PENDING
)

enum class MessageStatus {
    SENT,
    PENDING,
    FAILED,
    RECEIVED
}
