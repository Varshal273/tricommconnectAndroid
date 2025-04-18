package com.example.tricommconnect_v1.data.model

data class Message(
    val _id: String? = null,
    val chatId: String? = null,
    val senderUserId: SenderUser? = null, // Modified: nested user info
    val msgBody: String,
    val time: String
)

data class SenderUser(
    val _id: String,
    val username: String
)
