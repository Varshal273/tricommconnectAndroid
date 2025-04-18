package com.example.tricommconnect_v1.network.socket

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import com.example.tricommconnect_v1.data.local.entity.MessageStatus
import com.example.tricommconnect_v1.data.local.repository.LocalMessageRepository
import com.example.tricommconnect_v1.data.model.Message
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
class MessageSocketHandler(
    serverUrl: String, // ✅ Modified: accept server URL directly
    private val localMessageRepository: LocalMessageRepository
) {

    private val socket: Socket = IO.socket(serverUrl) // ✅ Modified: initialize socket once

    init {
        setupListeners() // ✅ Set up listener once
    }

    fun connectSocket() {
        try {
            socket.connect()
            Log.d("Socket", "Connected to server")
        } catch (e: Exception) {
            Log.e("Socket", "Connection error: ${e.localizedMessage}")
        }
    }

    fun disconnectSocket() {
        socket.disconnect()
        Log.d("Socket", "Disconnected")
    }

    fun joinChatRoom(chatId: String) {
        val json = JSONObject().put("chatId", chatId)
        socket.emit("join_chat", json)
    }

    private fun setupListeners() {
        socket.on("receive_message") { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as JSONObject
                    val message = Gson().fromJson(data.toString(), Message::class.java) // ✅ Modified

                    val entity = MessageEntity(
                        messageId = message._id ?: "", // ✅ Modified
                        chatId = message.chatId ?: "", // ✅ Modified
                        senderId = message.senderUserId?._id ?: "", // ✅ Modified
                        senderUsername = message.senderUserId?.username ?: "", // ✅ Modified
                        content = message.msgBody,
                        timestamp = Instant.parse(message.time).toEpochMilli(), // ✅ Modified
                        status = MessageStatus.RECEIVED
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        localMessageRepository.insertMessage(entity)
                    }

                    Log.d("Socket", "Message received and stored locally: $entity")

                } catch (e: Exception) {
                    Log.e("Socket", "Error parsing received message: ${e.message}") // ✅ Added error handling
                }
            }
        }
    }

    fun sendMessage(chatId: String, senderId: String, msgBody: String) {
        val json = JSONObject()
            .put("chatId", chatId)
            .put("senderId", senderId)
            .put("msgBody", msgBody)

        socket.emit("send_message", json)
    }

    fun setOnMessageReceived(callback: (Message) -> Unit) {
        socket.off("receive_message") // ✅ Prevent duplicate listeners
        socket.on("receive_message", Emitter.Listener { args ->
            if (args.isNotEmpty()) {
                try {
                    val data = args[0] as JSONObject
                    val message = Gson().fromJson(data.toString(), Message::class.java)
                    callback(message)
                } catch (e: Exception) {
                    Log.e("Socket", "Error in callback parsing: ${e.message}") // ✅ Added error handling
                }
            }
        })
    }

    fun removeMessageListener() {
        socket.off("receive_message")
    }
}
