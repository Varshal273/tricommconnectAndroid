package com.example.tricommconnect_v1.network

import com.example.tricommconnect_v1.data.model.Chat
import com.example.tricommconnect_v1.data.model.ChatListResponse
import com.example.tricommconnect_v1.data.model.ChatResponse
import com.example.tricommconnect_v1.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path



// network/ApiService.kt
interface ApiService {
    @POST("api/users/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<User>

    @GET("api/users/getUserChats/{userId}")
    suspend fun getUserChats(@Path("userId") userId: String): Response<ChatListResponse>
}
