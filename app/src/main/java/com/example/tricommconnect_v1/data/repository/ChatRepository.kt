package com.example.tricommconnect_v1.data.repository

import com.example.tricommconnect_v1.data.model.Chat
import com.example.tricommconnect_v1.data.model.LoginRequest
import com.example.tricommconnect_v1.data.model.User
import com.example.tricommconnect_v1.network.ApiService

class ChatRepository(private val api: ApiService) {

    suspend fun login(request: LoginRequest): Result<User> = try {
        val res = api.login(mapOf("email" to request.email, "password" to request.password))
        if (res.isSuccessful) Result.success(res.body()!!)
        else Result.failure(Exception("Login failed: ${res.code()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }

//    suspend fun getUserChats(userId: String): Result<List<Chat>> = try {
//        val res = api.getUserChats(userId)
//        if (res.isSuccessful) Result.success(res.body()!!)
//        else Result.failure(Exception("Failed to load chats"))
//    } catch (e: Exception) {
//        Result.failure(e)
//    }
//    suspend fun getUserChats(userId: String): Result<List<String>> = try {
//        val res = api.getUserChats(userId)
//        if (res.isSuccessful) {
//            val chatList = res.body()?.chatNames?.map { Chat(name = it, lastMessage = "Last message placeholder") } ?: emptyList()
//            Result.success(chatList)
//        } else {
//            Result.failure(Exception("Failed to load chats"))
//        }
//
//    } catch (e: Exception) {
//        Result.failure(e)
//    }
    suspend fun getUserChats(userId: String): Result<List<Chat>> = try {
        val res = api.getUserChats(userId)
        if (res.isSuccessful) {
            val chatList = res.body()?.chatNames?.map { name ->
                Chat(
                    name = name,
                    lastMessage = "Last message placeholder"
                )
            } ?: emptyList()
            Result.success(chatList)
        } else {
            Result.failure(Exception("Failed to load chats"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

}



//this is first code
//package com.example.tricommconnect_v1.data.repository
//
//import com.example.tricommconnect_v1.data.model.Chat
//import com.example.tricommconnect_v1.data.model.User
//import com.example.tricommconnect_v1.network.ApiService
//
//// data/repository/ChatRepository.kt
//class ChatRepository(private val api: ApiService) {
//    suspend fun login(username: String, password: String): Result<User> = try {
//        val res = api.login(mapOf("username" to username, "password" to password))
//        if (res.isSuccessful) Result.success(res.body()!!) else Result.failure(Exception("Login failed"))
//    } catch (e: Exception) {
//        Result.failure(e)
//    }
//
//    suspend fun getUserChats(userId: String): Result<List<Chat>> = try {
//        val res = api.getUserChats(userId)
//        if (res.isSuccessful) Result.success(res.body()!!) else Result.failure(Exception("Failed to load chats"))
//    } catch (e: Exception) {
//        Result.failure(e)
//    }
//}
