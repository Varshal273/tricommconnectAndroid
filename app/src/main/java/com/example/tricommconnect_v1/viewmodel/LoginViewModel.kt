package com.example.tricommconnect_v1.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tricommconnect_v1.data.model.LoginRequest
import com.example.tricommconnect_v1.data.model.User
import com.example.tricommconnect_v1.data.preferences.UserPreferences
import com.example.tricommconnect_v1.data.repository.ChatRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: ChatRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    var loginState by mutableStateOf<Result<User>?>(null)
        private set

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val request = LoginRequest(username, password)
            val result = repository.login(request)

            if (result.isSuccess) {
                result.getOrNull()?.let {
                    prefs.saveUser(it)  // Save whole User (token + user obj)
                }
            }

            loginState = result
        }
    }
}



//this is second where user model is not correct
//package com.example.tricommconnect_v1.viewmodel
//
//import androidx.compose.runtime.*
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//import com.example.tricommconnect_v1.data.repository.ChatRepository
//import com.example.tricommconnect_v1.data.preferences.UserPreferences
//import com.example.tricommconnect_v1.data.model.User
//
//// viewmodel/LoginViewModel.kt
//class LoginViewModel(
//    private val repository: ChatRepository,
//    private val prefs: UserPreferences
//) : ViewModel() {
//
//    var loginState by mutableStateOf<Result<User>?>(null)
//        private set
//
//    fun login(username: String, password: String) {
//        viewModelScope.launch {
//            val result = repository.login(username, password)
//            if (result.isSuccess) {
//                prefs.saveUser(result.getOrNull()!!)
//            }
//            loginState = result
//        }
//    }
//}
