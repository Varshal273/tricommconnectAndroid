package com.example.tricommconnect_v1.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tricommconnect_v1.viewmodel.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit) {
    val state = viewModel.loginState
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.login(username, password) }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Add null check here
        state?.let {
            when {
                it.isSuccess -> {
                    LaunchedEffect(Unit) {
                        onLoginSuccess()
                    }
                }
                it.isFailure -> {
                    Text(
                        "Login failed: ${it.exceptionOrNull()?.message ?: "Unknown error"}",
                        color = Color.Red
                    )
                }
            }
        }
    }
}


// this is first code
//package com.example.tricommconnect_v1.ui.login
//
//
//import androidx.compose.foundation.layout.*
////noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material.*
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.*
//import androidx.compose.ui.graphics.Color
//import com.example.tricommconnect_v1.viewmodel.LoginViewModel
//
//// ui/login/LoginScreen.kt
//@Composable
//fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit) {
//    val state = viewModel.loginState
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//
//    Column {
//        TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
//        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
//        Button(onClick = { viewModel.login(username, password) }) {
//            Text("Login")
//        }
//
//        when (state) {
//            is Result.Success -> {
//                LaunchedEffect(Unit) { onLoginSuccess() }
//            }
//            is Result.Failure -> {
//                Text("Login failed: ${state.exceptionOrNull()?.message}", color = Color.Red)
//            }
//            else -> {}
//        }
//    }
//}
