package com.example.tricommconnect_v1.data.model

// data/model/Chat.kt
//data class Chat(
//    val chatId: String,
//    val chatName: String,
//    val lastMessage: String
//)
//data class Chat(
//    val id: String,
//    val name: String,
//    val lastMessage: String
//)
// this is first code till Version_2_Cycle_1
//data class ChatResponse(
//    val chatNames: List<String>
//)
//
//data class ChatName(
//    val name: String
//)

data class Chat(
    val _id: String,
    val groupName: String,
    val group: List<UserChat>,
    val lastMessage: LastMessage?
)

data class LastMessage(
    val senderUserId: String,
    val msgBody: String,
    val time: String
)

data class UserChat(
    val _id: String,
    val username: String,
    val email: String
)