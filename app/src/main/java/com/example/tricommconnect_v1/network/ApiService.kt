package com.example.tricommconnect_v1.network

import com.example.tricommconnect_v1.data.model.Chat
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.data.model.SenderUser
import com.example.tricommconnect_v1.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class SendMessageResponse(
    val message: String,
    val newMessage: Message
)

data class NewMessage(
    val senderUserId:String,
    val msgBody:String,
    val time:String
)

data class FetchMessagesResponse(
    val messages: List<Message>
)

// network/ApiService.kt
interface ApiService {
    @POST("api/users/login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): Response<User>

    @GET("api/chats/user/{userId}")
    suspend fun getUserChats(
        @Path("userId") userId: String
    ): Response<List<Chat>>

    @POST("api/chats/{chatId}/message")
    suspend fun sendMessageToChat(
        @Path("chatId") chatId: String,
        @Body requestBody: Map<String, String>
    ): SendMessageResponse

    @GET("api/chats/{chatId}/messages")
    suspend fun getMessagesForChat(
        @Path("chatId") chatId: String
    ): FetchMessagesResponse
}
