package com.example.tricommconnect_v1.network.socket

import android.util.Log
import com.google.gson.Gson
import com.example.tricommconnect_v1.data.model.Message
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class MessageSocketHandler {

    private var socket: Socket? = null

    fun connectSocket(serverUrl: String) {
        try {
            socket = IO.socket(serverUrl)
            socket?.connect()
            Log.d("Socket", "Connected to $serverUrl")
        } catch (e: Exception) {
            Log.e("Socket", "Connection error: ${e.localizedMessage}")
        }
    }

    fun disconnectSocket() {
        socket?.disconnect()
        Log.d("Socket", "Disconnected")
    }

    fun joinChatRoom(chatId: String) {
        val json = JSONObject().put("chatId", chatId)
        socket?.emit("join_chat", json)
    }

    fun sendMessage(chatId: String, senderId: String, msgBody: String) {
        val json = JSONObject()
            .put("chatId", chatId)
            .put("senderId", senderId)
            .put("msgBody", msgBody)

        socket?.emit("send_message", json)
    }

    fun setOnMessageReceived(callback: (Message) -> Unit) {
        socket?.on("receive_message", Emitter.Listener { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val message = Gson().fromJson(data.toString(), Message::class.java)
                callback(message)
            }
        })
    }

    fun removeMessageListener() {
        socket?.off("receive_message")
    }
}
