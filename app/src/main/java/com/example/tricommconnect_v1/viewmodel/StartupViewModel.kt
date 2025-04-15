package com.example.tricommconnect_v1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tricommconnect_v1.data.preferences.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class StartupViewModel(private val prefs: UserPreferences) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination

    init {
        viewModelScope.launch {
            val token = prefs.userTokenFlow.firstOrNull()
            val userId = prefs.userIdFlow.firstOrNull()

            _startDestination.value = if (!token.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                "chatlist"
            } else {
                "login"
            }
        }
    }
}
