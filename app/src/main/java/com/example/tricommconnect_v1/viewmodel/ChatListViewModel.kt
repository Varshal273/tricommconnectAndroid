package com.example.tricommconnect_v1.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.example.tricommconnect_v1.data.repository.ChatRepository
import com.example.tricommconnect_v1.data.preferences.UserPreferences
import com.example.tricommconnect_v1.data.model.Chat

class ChatListViewModel(
    private val repository: ChatRepository,
    private val prefs: UserPreferences
) : ViewModel() {

//    var chatList by mutableStateOf<List<Chat>>(emptyList())
    var chatList by mutableStateOf<List<Chat>>(emptyList())
    var loading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun loadChats() {
        viewModelScope.launch {
            loading = true
            val userId = prefs.userIdFlow.firstOrNull()

            if (!userId.isNullOrEmpty()) {
                val result = repository.getUserChats(userId)

                if (result.isSuccess) {
                    chatList = result.getOrNull() ?: emptyList()
                    error = null
                } else {
                    error = result.exceptionOrNull()?.message ?: "Unknown error"
                }
            } else {
                error = "User ID not found."
            }

            loading = false
        }
    }
}


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
