package com.example.tricommconnect_v1.data.repository

import com.example.tricommconnect_v1.data.local.dao.ChatDao
import com.example.tricommconnect_v1.data.local.entity.ChatEntity
import com.example.tricommconnect_v1.data.model.Chat
import com.example.tricommconnect_v1.data.model.LoginRequest
import com.example.tricommconnect_v1.data.model.User
import com.example.tricommconnect_v1.network.ApiService
import kotlinx.coroutines.flow.first // ✅ Added
import kotlinx.coroutines.flow.map
import com.example.tricommconnect_v1.data.local.mapper.toChat
import kotlinx.coroutines.flow.firstOrNull


class ChatRepository(
    private val api: ApiService,
    private val chatDao: ChatDao // ✅ Added: Inject ChatDao for local Room operations
) {

    suspend fun login(request: LoginRequest): Result<User> = try {
        val res = api.login(mapOf("email" to request.email, "password" to request.password))
        if (res.isSuccessful) Result.success(res.body()!!)
        else Result.failure(Exception("Login failed: ${res.code()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserChats(userId: String): List<Chat> {
        return try {
            val res = api.getUserChats(userId)
            if (res.isSuccessful) {
                val chatList = res.body() ?: emptyList()
                chatDao.insertChats(chatList.map { ChatEntity.fromChat(it) })
                chatList
            } else {
                chatDao.getAllChats().firstOrNull()?.map { it.toChat() } ?: emptyList()
            }
        } catch (e: Exception) {
            chatDao.getAllChats().firstOrNull()?.map { it.toChat() } ?: emptyList()
        }
    }

    suspend fun getLocalChats(): List<ChatEntity> {
        return chatDao.getAllChats().firstOrNull() ?: emptyList() // ✅ Convert Flow to List
    }
}

// this is before version 2
//class ChatRepository(private val api: ApiService) {
//
//    suspend fun login(request: LoginRequest): Result<User> = try {
//        val res = api.login(mapOf("email" to request.email, "password" to request.password))
//        if (res.isSuccessful) Result.success(res.body()!!)
//        else Result.failure(Exception("Login failed: ${res.code()}"))
//    } catch (e: Exception) {
//        Result.failure(e)
//    }
//
////    suspend fun getUserChats(userId: String): Result<List<Chat>> = try {
////        val res = api.getUserChats(userId)
////        if (res.isSuccessful) Result.success(res.body()!!)
////        else Result.failure(Exception("Failed to load chats"))
////    } catch (e: Exception) {
////        Result.failure(e)
////    }
////    suspend fun getUserChats(userId: String): Result<List<String>> = try {
////        val res = api.getUserChats(userId)
////        if (res.isSuccessful) {
////            val chatList = res.body()?.chatNames?.map { Chat(name = it, lastMessage = "Last message placeholder") } ?: emptyList()
////            Result.success(chatList)
////        } else {
////            Result.failure(Exception("Failed to load chats"))
////        }
////
////    } catch (e: Exception) {
////        Result.failure(e)
////    }
//    suspend fun getUserChats(userId: String): Result<List<Chat>> = try {
//        val res = api.getUserChats(userId)
//        if (res.isSuccessful) {
//            val chatList = res.body() ?: emptyList()
//            Result.success(chatList)
//        } else {
//            Result.failure(Exception("Failed to load chats"))
//        }
//    } catch (e: Exception) {
//        Result.failure(e)
//    }
//
////    suspend fun getLocalChats(): List<ChatEntity> {
////        return chatDao.getAllChats()
////    }
//
//}



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
