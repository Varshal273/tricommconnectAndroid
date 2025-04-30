package com.example.tricommconnect_v1.network.socket

import android.util.Log
import com.example.tricommconnect_v1.data.model.Chat
import com.example.tricommconnect_v1.data.model.LastMessage
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.data.model.SenderUser
import com.example.tricommconnect_v1.data.model.UserChat
import io.socket.client.Ack
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

object SocketManager {

    private var socket: Socket? = null
    private const val SOCKET_URL = "https://organic-meet-monarch.ngrok-free.app" // 🔥 Replace with your backend URL

    private val newMessageFlow = MutableSharedFlow<Message>()
    private val chatUpdateFlow = MutableSharedFlow<Chat>()

    fun connect() {
        if (socket == null || !socket!!.connected()) {
            try {
                val opts = IO.Options()
                // You can set options here if needed (e.g., authentication headers)
                socket = IO.socket(SOCKET_URL, opts)
                setupListeners()
                socket?.connect()
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off() // Remove all listeners
        socket = null
    }

    fun sendMessage(message: Message) {
        val messageJson = JSONObject().apply {
            put("chatId", message.chatId)
            put("senderId", message.senderUserId?._id ?: "")
            put("msgBody", message.msgBody)
            put("time", message.time)
        }

        Log.d("SocketManager", "Emitting send_message: $messageJson")

        socket?.emit("send_message", messageJson, Ack { args ->
            Log.d("SocketManager", "Server acknowledged send_message: ${args.joinToString()}")
        })
    }

    fun joinRoom(chatId: String) {
        socket?.emit("join_room", chatId)
        Log.d("SocketManager", "Joined room for chatId=$chatId")
    }


//    fun joinChat(chatId: String) {
//        val joinData = JSONObject()
//        joinData.put("chatId", chatId)
//        socket?.emit("join_chat", joinData)
//    }

    fun leaveChat(chatId: String) {
        val leaveData = JSONObject()
        leaveData.put("chatId", chatId)
        socket?.emit("leave_chat", leaveData)
    }

    fun observeNewMessages(): SharedFlow<Message> = newMessageFlow
    fun observeChatUpdates(): SharedFlow<Chat> = chatUpdateFlow

    private fun setupListeners() {
        socket?.on(Socket.EVENT_CONNECT, onConnect)
        socket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket?.on("new_message", onNewMessage)
        socket?.on("chat_updated", onChatUpdated)
        socket?.on(Socket.EVENT_CONNECT_ERROR, onError)
        socket?.on("send_message_ack") { args ->
            args.getOrNull(0)?.let {
                Log.d("SocketManager", "ACK from server: ${(it as JSONObject).toString(2)}")
            }
        }

//        socket?.on(Socket.EVENT_ERROR, onError) <-- Invalid
    }

    private val onConnect = Emitter.Listener {
        println("Socket Connected")
    }

    private val onDisconnect = Emitter.Listener {
        println("Socket Disconnected")
    }

    private val onError = Emitter.Listener { args ->
        println("Socket Error: ${args.getOrNull(0)}")
    }

    private val onNewMessage = Emitter.Listener { args ->
        args.getOrNull(0)?.let { data ->
            val json = data as JSONObject
            Log.d("SocketManager", "onNewMessage(): $json") // 🔍 ADD THIS
            val message = parseMessageFromJson(json)
            message?.let {
                newMessageFlow.tryEmit(it)
            }
        }
    }

    private val onChatUpdated = Emitter.Listener { args ->
        args.getOrNull(0)?.let { data ->
            val json = data as JSONObject
            val chat = parseChatFromJson(json)
            chat?.let {
                chatUpdateFlow.tryEmit(it)
            }
        }
    }

    private fun parseMessageFromJson(json: JSONObject): Message? {
        return try {
            val message = Message(
                _id = json.getString("_id"),
                chatId = json.getString("chatId"),
                msgBody = json.getString("msgBody"),
                time = json.getString("time"),
                senderUserId = SenderUser(
                    _id = json.getJSONObject("senderUserId").getString("_id"),
                    username = json.getJSONObject("senderUserId").getString("username")
                )
            )
            Log.d("SocketManager", "✅ Parsed message from socket: $message") // 👈 ADD THIS
            message
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun parseChatFromJson(json: JSONObject): Chat? {
        return try {
            val groupArray = json.getJSONArray("group")
            val groupList = mutableListOf<UserChat>()
            for (i in 0 until groupArray.length()) {
                val userJson = groupArray.getJSONObject(i)
                groupList.add(
                    UserChat(
                        _id = userJson.getString("_id"),
                        username = userJson.getString("username"),
                        email = userJson.getString("email")
                    )
                )
            }

            val lastMessageObj = json.optJSONObject("lastMessage")
            val lastMessage = lastMessageObj?.let {
                LastMessage(
                    senderUserId = it.getString("senderUserId"),
                    msgBody = it.getString("msgBody"),
                    time = it.getString("time")
                )
            }

            Chat(
                _id = json.getString("_id"),
                groupName = json.getString("groupName"),
                group = groupList,
                lastMessage = lastMessage
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
