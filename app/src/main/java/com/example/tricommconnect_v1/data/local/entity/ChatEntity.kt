package com.example.tricommconnect_v1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val name: String,
    val lastMessage: String?,
    val timestamp: Long
)
