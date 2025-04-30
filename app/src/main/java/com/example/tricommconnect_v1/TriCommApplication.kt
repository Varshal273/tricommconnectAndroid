package com.example.tricommconnect_v1

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tricommconnect_v1.data.preferences.UserPreferences
import com.example.tricommconnect_v1.network.socket.SocketManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class TriCommApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val prefs = UserPreferences(applicationContext)

        // Connect socket if user is already logged in
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            val token = prefs.userTokenFlow.firstOrNull()
            val userId = prefs.userIdFlow.firstOrNull()

            if (!token.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                Log.d("TriCommApp", "Connecting socket on app start")
                SocketManager.connect()
            } else {
                Log.d("TriCommApp", "User not logged in — socket not connected")
            }
        }
    }
}
