package com.example.tricommconnect_v1.socket

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

object MessageSocketHandler {
    private lateinit var socket: Socket
    private const val SOCKET_URL = "https://organic-meet-monarch.ngrok-free.app" // Replace with real backend
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        try {
            val opts = IO.Options().apply {
                reconnection = true
                reconnectionAttempts = Int.MAX_VALUE
                reconnectionDelay = 2000
                reconnectionDelayMax = 5000
                timeout = 5000
            }
            socket = IO.socket(SOCKET_URL, opts)

            // 🌐 Socket events
            socket.on(Socket.EVENT_CONNECT) {
                Log.d("Socket", "Connected to server")
            }
            socket.on(Socket.EVENT_DISCONNECT) { args ->
                val reason = if (args.isNotEmpty()) args[0]?.toString() else "unknown"
                Log.w("Socket", "Disconnected: $reason")
            }

//            // 🧠 Manager-level reconnection events
//            val manager = socket.io()
//
//            manager.on(io.socket.engineio.client.Socket.EVENT_RECONNECT) {
//                Log.i("Socket", "Reconnected successfully")
//            }
//            manager.on(io.socket.engineio.client.Socket.EVENT_RECONNECT_ATTEMPT) {
//                Log.d("Socket", "Attempting to reconnect...")
//            }
//            manager.on(io.socket.engineio.client.Socket.EVENT_RECONNECT_ERROR) { args ->
//                Log.e("Socket", "Reconnect error: ${args.firstOrNull()?.toString() ?: "Unknown"}")
//            }
//            manager.on(io.socket.engineio.client.Socket.EVENT_RECONNECT_FAILED) {
//                Log.e("Socket", "Reconnect failed after all attempts")
//            }

            isInitialized = true
        } catch (e: URISyntaxException) {
            Log.e("SocketInit", "Invalid socket URL: ${e.message}")
        }
    }



    fun connect() {
        if (!isInitialized) return
        socket.connect()
        Log.d("Socket", "Socket connected")
    }

    fun disconnect() {
        if (!isInitialized) return
        socket.disconnect()
        Log.d("Socket", "Socket disconnected")
    }

    fun emitSendMessage(payload: JSONObject) {
        if (!isInitialized || !socket.connected()) {
            Log.e("Socket", "Not connected")
            return
        }
        socket.emit("send_message", payload)
    }

    fun on(event: String, listener: (JSONObject) -> Unit) {
        if (!isInitialized) return
        socket.on(event) { args ->
            if (args.isNotEmpty() && args[0] is JSONObject) {
                listener(args[0] as JSONObject)
            }
        }
    }
}


//Purpose:
//Manages Socket.IO connection
//Handles connection, emit, on events (e.g. receive_message, chat_updated)
//Ping/pong & reconnection logic