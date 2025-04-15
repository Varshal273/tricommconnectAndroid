package com.example.tricommconnect_v1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val chatId: String,              // ID of the chat this message belongs to
    val senderId: String,            // User ID of the sender
    val receiverId: String,          // User ID of the receiver
    val content: String,             // Message text/content
    val timestamp: Long,            // Time the message was sent
    val isSentByMe: Boolean         // Flag to show if message is from current user
)

// this is till v2c1
//package com.example.tricommconnect_v1.data.local.entity
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//@Entity(tableName = "messages")
//data class MessageEntity(
//    @PrimaryKey val id: String,
//    val chatId: String,
//    val senderId: String,
//    val content: String,
//    val timestamp: Long,
//    val status: String // "sent", "pending", etc.
//)
