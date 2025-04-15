package com.example.tricommconnect_v1.data.model

// data/model/Chat.kt
//data class Chat(
//    val chatId: String,
//    val chatName: String,
//    val lastMessage: String
//)
data class Chat(
    val name: String,
    val lastMessage: String
)
data class ChatResponse(
    val chatNames: List<String>
)

data class ChatName(
    val name: String
)
