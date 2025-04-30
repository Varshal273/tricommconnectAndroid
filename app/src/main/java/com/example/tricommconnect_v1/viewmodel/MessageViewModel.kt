package com.example.tricommconnect_v1.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tricommconnect_v1.data.local.mapper.toModel
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.data.model.SenderUser
import com.example.tricommconnect_v1.data.repository.MessageRepository
import com.example.tricommconnect_v1.network.socket.SocketManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class MessageListState {
    object Loading : MessageListState()
    data class Success(val messages: List<Message>) : MessageListState()
    data class Error(val error: String) : MessageListState()
}

sealed class SendState {
    object Idle    : SendState()
    object Sending : SendState()
    object Sent    : SendState()
    data class Error(val error: String) : SendState()
}

@RequiresApi(Build.VERSION_CODES.O)
class MessageViewModel(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _messageListState = MutableStateFlow<MessageListState>(MessageListState.Loading)
    val messageListState: StateFlow<MessageListState> = _messageListState

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState

    init {
        // A: socket → Room
        viewModelScope.launch {
            SocketManager.observeNewMessages()
                .collect { incoming ->
                    incoming.chatId?.let { cid ->
                        Log.d("VM", "Saving socket message ${incoming._id} into Room")
                        messageRepository.saveMessagesToLocal(cid, listOf(incoming))
                    }
                }
        }
    }

    fun observeMessages(chatId: String) {
        viewModelScope.launch {
            // 1) start your UI from Room
            _messageListState.value = MessageListState.Loading
            messageRepository
                .getLocalMessagesFlow(chatId)
                .map { ents -> ents.map { it.toModel() } }
                .onEach { msgs ->
                    Log.d("VM", "Room emitted ${msgs.size} messages")
                    _messageListState.value = MessageListState.Success(msgs)
                }
                .launchIn(this)

            // 2) populate initial data from API
            try {
                val remote = messageRepository.fetchRemoteMessages(chatId)
                Log.d("VM", "Fetched ${remote.size} from API")
                if (remote.isNotEmpty()) messageRepository.saveMessagesToLocal(chatId, remote)
            } catch (e: Exception) {
                Log.e("VM", "API sync failed", e)
                _messageListState.value = MessageListState.Error("Sync failed: ${e.message}")
            }

            // 3) join the socket room
            SocketManager.joinRoom(chatId)
        }
    }

    fun sendMessage(chatId: String, senderId: String, msgBody: String) {
        viewModelScope.launch {
            _sendState.value = SendState.Sending
            try {
                val newMsg = Message(
                    _id = null,
                    chatId = chatId,
                    msgBody = msgBody,
                    time = System.currentTimeMillis().toString(),
                    senderUserId = SenderUser(_id = senderId, username = "")
                )
                SocketManager.sendMessage(newMsg)
                Log.d("VM", "📤 Sent $newMsg")
                _sendState.value = SendState.Sent
            } catch (e: Exception) {
                _sendState.value = SendState.Error(e.message ?: "Send failed")
            }
        }
    }

    fun resetSendState() {
        _sendState.value = SendState.Idle
    }
}

//package com.example.tricommconnect_v1.viewmodel
//
//import android.os.Build
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.tricommconnect_v1.data.model.Message
//import com.example.tricommconnect_v1.data.local.entity.MessageEntity
//import com.example.tricommconnect_v1.data.local.mapper.toEntity
//import com.example.tricommconnect_v1.data.local.mapper.toModel
//import com.example.tricommconnect_v1.data.model.SenderUser
//import com.example.tricommconnect_v1.data.repository.MessageRepository
//import com.example.tricommconnect_v1.network.socket.SocketManager
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//// in viewmodel/MessageViewModel.kt
//
//sealed class MessageListState {
//    object Loading : MessageListState()
//    data class Success(val messages: List<Message>) : MessageListState()
//    data class Error(val error: String) : MessageListState()
//}
//
//sealed class SendState {
//    object Idle : SendState()
//    object Sending : SendState()
//    object Sent : SendState()
//    data class Error(val error: String) : SendState()
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//class MessageViewModel(
//    private val messageRepository: MessageRepository
//) : ViewModel() {
//
//    private val _messageListState = MutableStateFlow<MessageListState>(MessageListState.Loading)
//    val messageListState: StateFlow<MessageListState> = _messageListState
//
//    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
//    val sendState: StateFlow<SendState> = _sendState
//
//    init {
//        viewModelScope.launch {
//            SocketManager.observeNewMessages()
//                .collect { incomingMessage ->
//                    Log.d("MessageViewModel", "🧩 Received from socket: $incomingMessage") // 👈 ADD
//                    incomingMessage.chatId?.let { chatId ->
//                        messageRepository.saveMessagesToLocal(
//                            chatId = chatId,
//                            messages = listOf(incomingMessage)
//                        )
//                        observeMessages(incomingMessage.chatId!!)
//                    }
//                }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun observeMessages(chatId: String) {
//        viewModelScope.launch {
//            SocketManager.joinRoom(chatId)
//
//            // 1. Emit loading state
//            _messageListState.value = MessageListState.Loading
//
//            // 2. Observe local messages → React to updates from Room (includes socket-inserts)
//            messageRepository
//                .getLocalMessagesFlow(chatId)
//                .map { list -> list.map { it.toModel() } }
//                .onEach { models ->
//                    Log.d("MessageViewModel", "Room emitted ${models.size} messages (ids=${models.map { it._id }})")
//                    _messageListState.value = MessageListState.Success(models)
//                }
//                .launchIn(viewModelScope)
//
//            // 3. Try fetching remote once to refresh
//            try {
//                val remote = messageRepository.fetchRemoteMessages(chatId)
//                Log.d("MessageViewModel", "Fetched ${remote.size} messages from API for chatId=$chatId")
//                if(remote.isNotEmpty()){
//                    messageRepository.saveMessagesToLocal(chatId, remote)
//                }
//            } catch (e: Exception) {
//                Log.e("MessageViewModel", "API fetch failed", e)
//                _messageListState.value = MessageListState.Error("Sync failed: ${e.message}")
//            }
//        }
//    }
//
////    fun observeMessages(chatId: String) {
////        viewModelScope.launch {
////            SocketManager.joinRoom(chatId)
////            try {
////                val remote = messageRepository.fetchRemoteMessages(chatId)
////                Log.d("MessageViewModel", "Fetched ${remote.size} messages from API for chatId=$chatId")
////                messageRepository.saveMessagesToLocal(chatId,remote)
////            } catch (e: Exception) {
////                Log.e("MessageViewModel", "API fetch failed", e)
////                _messageListState.value = MessageListState.Error("Sync failed: ${e.message}")
////            }
////
////            _messageListState.value = MessageListState.Loading
////
////            // For Debug only remove this later.
////            messageRepository
////                .getLocalMessagesFlow(chatId)
////                .map { list -> list.map { it.toModel() } }
////                .onEach { models ->
////                    Log.d(
////                        "MessageViewModel",
////                        "Room emitted ${models.size} messages (ids=${models.map{it._id}})"
////                    )
////                    _messageListState.value = MessageListState.Success(models)
////                }
////                .launchIn(viewModelScope)
////
////
////            // Subscribe to local updates
////            messageRepository.getLocalMessagesFlow(chatId)
////                .map { list -> list.map { it.toModel() } }
////                .onEach { _messageListState.value = MessageListState.Success(it) }
////                .launchIn(viewModelScope)
////
////            // Fetch remote once
////            try {
////                val remote = messageRepository.fetchRemoteMessages(chatId)
////                messageRepository.saveMessagesToLocal(chatId,remote)
////            } catch (e: Exception) {
////                _messageListState.value = MessageListState.Error(e.message ?: "Sync failed")
////                messageRepository.getLocalMessagesFlow(chatId)
////                    .map { list -> list.map { it.toModel() } }
////                    .onEach { _messageListState.value = MessageListState.Success(it) }
////                    .launchIn(viewModelScope)
////            }
////        }
////    }
//
//    fun sendMessage(chatId: String, senderId: String, msgBody: String) {
//        viewModelScope.launch {
//            _sendState.value = SendState.Sending
//            try {
//                // Build the message locally
//                val newMessage = Message(
//                    _id = null, // will be assigned by server
//                    chatId = chatId,
//                    msgBody = msgBody,
//                    time = System.currentTimeMillis().toString(),
//                    senderUserId = SenderUser(_id = senderId, username = "") // Fill username if needed
//                )
//
//                //// Optimistic save to Room (optional, if you want to show immediately)
////                messageRepository.saveMessagesToLocal(chatId, listOf(newMessage))
//
//                // Send via socket
//                SocketManager.sendMessage(newMessage)
//                Log.d("MessageViewModel", "Sent message: $newMessage")
//
//                _sendState.value = SendState.Sent
//            } catch (e: Exception) {
//                _sendState.value = SendState.Error(e.message ?: "Send failed")
//            }
//        }
//    }
//
//    fun resetSendState() {
//        _sendState.value = SendState.Idle
//    }
//}
