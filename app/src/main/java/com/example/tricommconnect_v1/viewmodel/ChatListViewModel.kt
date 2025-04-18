package com.example.tricommconnect_v1.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.example.tricommconnect_v1.data.repository.ChatRepository
import com.example.tricommconnect_v1.data.preferences.UserPreferences
import com.example.tricommconnect_v1.data.model.Chat
import com.example.tricommconnect_v1.data.model.LastMessage
import com.example.tricommconnect_v1.data.model.UserChat
import com.example.tricommconnect_v1.data.local.mapper.toChat

class ChatListViewModel(
    private val repository: ChatRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    var chatList by mutableStateOf<List<Chat>>(emptyList())
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var userId: String? = null // ✅ Added: to hold userId from DataStore

    fun loadChats() {
        viewModelScope.launch {
            loading = true

            userId = prefs.userIdFlow.firstOrNull()

            if (userId.isNullOrEmpty()) {
                error = "User ID not found."
                loading = false
                return@launch
            }

            try {
                // ✅ Step 1: Load from local Room DB
                val localChats = repository.getLocalChats()
                chatList = localChats.map { it.toChat() }

                // ✅ Step 2: Load from API
                val latestChats = repository.getUserChats(userId!!)
                chatList = latestChats // ✅ Replace with fresh list from server
                error = null
            } catch (e: Exception) {
                error = e.message ?: "Failed to fetch chats"
                // chatList already has local cache
            }

            loading = false
        }
    }


    // ✅ Helper extension to convert ChatEntity → Chat (basic form)
    private fun com.example.tricommconnect_v1.data.local.entity.ChatEntity.toChat(): Chat {
        return Chat(
            _id = chatId,
            groupName = groupName,
            group = emptyList(), // Placeholder, not available in local entity
            lastMessage = lastMessage?.let {
                LastMessage(senderUserId = "", msgBody = it, time = updatedAt.toString())
            }
        )
    }
}

//this code is just before version 2 is completed
//package com.example.tricommconnect_v1.viewmodel
//
//import androidx.compose.runtime.*
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.launch
//import com.example.tricommconnect_v1.data.repository.ChatRepository
//import com.example.tricommconnect_v1.data.preferences.UserPreferences
//import com.example.tricommconnect_v1.data.model.Chat
//
//class ChatListViewModel(
//    private val repository: ChatRepository,
//    private val prefs: UserPreferences
//) : ViewModel() {
//
////    var chatList by mutableStateOf<List<Chat>>(emptyList())
//    var chatList by mutableStateOf<List<Chat>>(emptyList())
//    var loading by mutableStateOf(false)
//    var error by mutableStateOf<String?>(null)
//    var userId: String? = null  // ⬅️ Add this line // Version_2_Cycle_2
//
//
//    fun loadChats() {
//        viewModelScope.launch {
//            loading = true
//            userId = prefs.userIdFlow.firstOrNull()
//
//            if (!userId.isNullOrEmpty()) {
//                val result = repository.getUserChats(userId!!)
//
//                if (result.isSuccess) {
//                    chatList = result.getOrNull() ?: emptyList()
//                    error = null
//                } else {
//                    error = result.exceptionOrNull()?.message ?: "Unknown error"
//                }
//            } else {
//                error = "User ID not found."
//            }
//
//            loading = false
//        }
//    }
//}


// this is first code
//package com.example.tricommconnect_v1.viewmodel
//
//import androidx.compose.runtime.*
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.launch
//import com.example.tricommconnect_v1.data.repository.ChatRepository
//import com.example.tricommconnect_v1.data.preferences.UserPreferences
//import com.example.tricommconnect_v1.data.model.Chat
//
//// viewmodel/ChatListViewModel.kt
//class ChatListViewModel(
//    private val repository: ChatRepository,
//    private val prefs: UserPreferences
//) : ViewModel() {
//
//    var chatList by mutableStateOf<List<Chat>>(emptyList())
//    var loading by mutableStateOf(false)
//    var error by mutableStateOf<String?>(null)
//
//    fun loadChats() {
//        viewModelScope.launch {
//            loading = true
//            prefs.userId.firstOrNull()?.let {
//                when (val result = repository.getUserChats(it)) {
//                    is Result.Success -> chatList = result.getOrNull() ?: emptyList()
//                    is Result.Failure -> error = result.exceptionOrNull()?.message
//                }
//            }
//            loading = false
//        }
//    }
//}
