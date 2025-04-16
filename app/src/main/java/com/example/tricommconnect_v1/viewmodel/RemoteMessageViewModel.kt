package com.example.tricommconnect_v1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.data.remote.repository.RemoteMessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RemoteMessageState {
    object Idle : RemoteMessageState()
    object Loading : RemoteMessageState()
    data class Success(val messages: List<Message>) : RemoteMessageState()
    data class Sent(val message: Message) : RemoteMessageState()
    data class Error(val error: String) : RemoteMessageState()
}

class RemoteMessageViewModel(
    private val repository: RemoteMessageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<RemoteMessageState>(RemoteMessageState.Idle)
    val state: StateFlow<RemoteMessageState> = _state

    fun fetchMessages(chatId: String) {
        viewModelScope.launch {
            _state.value = RemoteMessageState.Loading
            try {
                val result = repository.fetchMessages(chatId)
                _state.value = RemoteMessageState.Success(result)
            } catch (e: Exception) {
                _state.value = RemoteMessageState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun sendMessage(chatId: String, senderId: String, message: String) {
        viewModelScope.launch {
            _state.value = RemoteMessageState.Loading
            try {
                val result = repository.sendMessage(chatId, senderId, message)
                _state.value = RemoteMessageState.Sent(result!!)
            } catch (e: Exception) {
                _state.value = RemoteMessageState.Error(e.message ?: "Failed to send message")
            }
        }
    }

    fun resetState() {
        _state.value = RemoteMessageState.Idle
    }
}
