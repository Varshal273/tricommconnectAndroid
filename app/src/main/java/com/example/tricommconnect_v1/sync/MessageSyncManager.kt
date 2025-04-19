package com.example.tricommconnect_v1.sync

import android.content.Context
import android.util.Log
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.data.repository.ChatRepository
import com.example.tricommconnect_v1.data.repository.MessageRepository
import com.example.tricommconnect_v1.socket.MessageSocketHandler
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

object MessageSyncManager {

    private val gson = Gson()
    private lateinit var messageRepo: MessageRepository
    private lateinit var chatRepo: ChatRepository
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun init(
        messageRepo: MessageRepository,
        chatRepo: ChatRepository,
        socketHandler: MessageSocketHandler
    ) {
        this.messageRepo = messageRepo
        this.chatRepo = chatRepo

        // Listen for new incoming messages
        socketHandler.on("receive_message") { data ->
            ioScope.launch {
                try {
                    val message = gson.fromJson(data.toString(), Message::class.java)
                    Log.d("MessageSync", "Received message: $message")

                    // Save to Room
                    messageRepo.saveMessagesToLocal(message.chatId.toString(), listOf(message))

                    // Update chat preview
                    chatRepo.updateChatPreview(
                        chatId = message.chatId.toString(),
                        lastMessage = message.msgBody,
                        updatedAt = message.time
                    )

                } catch (e: Exception) {
                    Log.e("MessageSync", "Failed to handle receive_message", e)
                }
            }
        }

        // Listen for chat preview updates
        socketHandler.on("chat_updated") { data ->
            ioScope.launch {
                try {
                    val chat = data.getJSONObject("chat")
                    val chatId = chat.getString("_id")
                    val lastMessage = chat.getString("lastMessage")
                    val updatedAt = chat.getString("updatedAt")

                    Log.d("MessageSync", "Chat updated: $chatId")

                    chatRepo.updateChatPreview(chatId, lastMessage, updatedAt)

                } catch (e: Exception) {
                    Log.e("MessageSync", "Failed to handle chat_updated", e)
                }
            }
        }
    }
}


//package com.example.tricommconnect_v1.sync
//
//import android.content.Context
//import android.util.Log
//import com.example.tricommconnect_v1.data.model.Message
//import com.example.tricommconnect_v1.data.repository.ChatRepository
//import com.example.tricommconnect_v1.data.repository.MessageRepository
//import com.google.gson.Gson
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import com.example.tricommconnect_v1.socket.MessageSocketHandler
//import org.json.JSONObject
//
//object MessageSyncManager {
//
//    private val gson = Gson()
//    private lateinit var messageRepo: MessageRepository
//    private lateinit var chatRepo: ChatRepository
//
//    fun init(context: Context) {
//        // Inject repositories properly if using Hilt later
//        messageRepo = MessageRepository.getInstance(context)
//        chatRepo = ChatRepository.getInstance(context)
//
//        // 💬 Listen for messages // this is old function
////        MessageSocketHandler.on("receive_message") { data ->
////            CoroutineScope(Dispatchers.IO).launch {
////                try {
////                    val msg = gson.fromJson(data.toString(), Message::class.java)
////                    messageRepo.saveMessagesToLocal(msg.chatId.toString(), listOf(msg))
////
////                    // Also update the chat preview
////                    chatRepo.updateChatPreview(
////                        chatId = msg.chatId.toString(),
////                        lastMessage = msg.msgBody,
////                        updatedAt = msg.time
////                    )
////                } catch (e: Exception) {
////                    Log.e("SyncManager", "Failed to parse message: ${e.message}")
////                }
////            }
////        }
//
//        // 🟡 Optionally handle chat updates
//        MessageSocketHandler.on("chat_updated") { data ->
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val obj = data.getJSONObject("chat")
//                    val chatId = obj.getString("_id")
//                    val lastMsg = obj.getString("lastMessage")
//                    val time = obj.getString("updatedAt")
//
//                    chatRepo.updateChatPreview(chatId, lastMsg, time)
//                } catch (e: Exception) {
//                    Log.e("SyncManager", "Failed to update chat: ${e.message}")
//                }
//            }
//        }
//
//        MessageSocketHandler.on("receive_message") { args ->
//            val data = args[0.toString()] as? JSONObject ?: return@on
//
//            try {
//                val message = gson.fromJson(data.toString(), Message::class.java)
//
//                CoroutineScope(Dispatchers.IO).launch {
//                    // Save to Room DB
//                    messageRepo.saveMessagesToLocal(message.chatId.toString(), listOf(message))
//
//                    // Update chat preview (for chat list)
//                    chatRepo.updateChatPreview(
//                        chatId = message.chatId.toString(),
//                        lastMessage = message.msgBody,
//                        updatedAt = message.time
//                    )
//                }
//
//            } catch (e: Exception) {
//                Log.e("MessageSyncManager", "Error parsing socket message", e)
//            }
//        }
//
//    }
//}


//this code is just after version 2 is completed
//class MessageSyncManager(
//    private val localMessageRepository: LocalMessageRepository,
//    private val remoteMessageRepository: RemoteMessageRepository
//) {
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    suspend fun syncPendingMessages() = withContext(Dispatchers.IO) {
//        val pendingMessages = localMessageRepository.getPendingMessages()
//
//        for (message in pendingMessages) {
//            try {
//                val result = remoteMessageRepository.sendMessage(
//                    chatId = message.chatId,
//                    senderId = message.senderId,
//                    messageBody = message.content
//                )
//
//                if (result != null) {
//                    // Conflict check: avoid duplicate if already synced
//                    val existingMessages = localMessageRepository.getMessagesForChatOnce(message.chatId)
//                    val conflict = existingMessages.any {
//                        it.content == message.content &&
//                                it.senderId == message.senderId &&
//                                Math.abs(it.timestamp - Instant.parse(result.time).toEpochMilli()) < 1000L
//                    }
//
//                    if (!conflict) {
//                        val syncedMessage = message.copy(
//                            messageId = result._id ?: message.messageId, // ✅ modified: now gets _id from Message model
//                            timestamp = Instant.parse(result.time).toEpochMilli(), // ✅ modified: time is ISO 8601
//                            senderUsername = result.senderUserId?.username, // ✅ modified: now accesses nested username
//                            status = MessageStatus.SENT // ✅ added: set to SENT
//                        )
//                        localMessageRepository.insertMessage(syncedMessage)
//                    } else {
//                        val resolved = message.copy(status = MessageStatus.SENT) // ✅ added: set pending message as SENT if conflict
//                        localMessageRepository.insertMessage(resolved)
//                    }
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                // Optional: Mark as FAILED
//                // val failed = message.copy(status = MessageStatus.FAILED)
//                // localMessageRepository.insertMessage(failed)
//            }
//        }
//    }
//}



//Purpose:
//Observes socket events from MessageSocketHandler
//Converts JSON into Message
//Writes to Room via MessageRepository & ChatRepository