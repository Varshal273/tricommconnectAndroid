package com.example.tricommconnect_v1

import android.app.Application
import com.example.tricommconnect_v1.socket.MessageSocketHandler
import com.example.tricommconnect_v1.sync.MessageSyncManager
import com.example.tricommconnect_v1.data.repository.ChatRepository
import com.example.tricommconnect_v1.data.repository.MessageRepository

class TriCommApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Step 1: Initialize repositories (singleton)
        val messageRepo = MessageRepository.getInstance(applicationContext)
        val chatRepo = ChatRepository.getInstance(applicationContext)

        // Step 2: Initialize and connect socket (singleton)
        MessageSocketHandler.init(applicationContext)
        MessageSocketHandler.connect()

        // Step 3: Initialize Sync Manager with dependencies
        MessageSyncManager.init(
            messageRepo = messageRepo,
            chatRepo = chatRepo,
            socketHandler = MessageSocketHandler
        )
    }
}


//Purpose:
//Extend Application
//Initialize and manage global socket connection
//Inject shared components like MessageSyncManager