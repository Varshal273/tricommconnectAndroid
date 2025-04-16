package com.example.tricommconnect_v1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tricommconnect_v1.data.preferences.UserPreferences
import com.example.tricommconnect_v1.data.repository.ChatRepository
import com.example.tricommconnect_v1.network.RetrofitInstance
import com.example.tricommconnect_v1.ui.chat.ChatScreen
import com.example.tricommconnect_v1.ui.chatlist.ChatListScreen
import com.example.tricommconnect_v1.ui.login.LoginScreen
import com.example.tricommconnect_v1.ui.theme.TriCommConnect_V1Theme
import com.example.tricommconnect_v1.viewmodel.ChatListViewModel
import com.example.tricommconnect_v1.viewmodel.LoginViewModel
import com.example.tricommconnect_v1.viewmodel.StartupViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TriCommConnect_V1Theme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TriCommConnect_V1Theme {
        Greeting("Android")
    }
}


@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val repo = remember { ChatRepository(RetrofitInstance.api) }
    val startupViewModel = remember { StartupViewModel(prefs) }
    val startDestination by startupViewModel.startDestination.collectAsState()
    // Only build navigation graph once the destination is known
    if (startDestination != null) {
        NavigationGraph(navController, startDestination!!, prefs, repo)
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String,
    prefs: UserPreferences,
    repo: ChatRepository
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            val vm = remember { LoginViewModel(repo, prefs) }
            LoginScreen(vm) {
                navController.navigate("chatlist") {
                    popUpTo("login") { inclusive = true } // Prevent back to login
                }
            }
        }
        composable("chatlist") {
            val vm = remember { ChatListViewModel(repo, prefs) }
            ChatListScreen(vm)
        }
        composable(
            "chat/{chatId}/{senderId}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("senderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val senderId = backStackEntry.arguments?.getString("senderId") ?: ""
            ChatScreen(chatId = chatId, senderId = senderId)
        }
    }
}

//first code
//// MainActivity.kt
//@Composable
//fun AppNavigator() {
//    val navController = rememberNavController()
//    val context = LocalContext.current
//    val prefs = remember { UserPreferences(context) }
//    val repo = remember { ChatRepository(RetrofitInstance.api) }
//
//    NavHost(navController, startDestination = "login") {
//        composable("login") {
//            val vm = remember { LoginViewModel(repo, prefs) }
//            LoginScreen(vm) { navController.navigate("chatlist") }
//        }
//        composable("chatlist") {
//            val vm = remember { ChatListViewModel(repo, prefs) }
//            ChatListScreen(vm)
//        }
//    }
//}
//