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
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.preferences.UserPreferences
import com.example.tricommconnect_v1.data.remote.repository.RemoteMessageRepository
import com.example.tricommconnect_v1.data.repository.ChatRepository
import com.example.tricommconnect_v1.network.RetrofitInstance
import com.example.tricommconnect_v1.ui.chat.ChatScreen
import com.example.tricommconnect_v1.ui.chatlist.ChatListScreen
import com.example.tricommconnect_v1.ui.login.LoginScreen
import com.example.tricommconnect_v1.ui.theme.TriCommConnect_V1Theme
import com.example.tricommconnect_v1.viewmodel.ChatListViewModel
import com.example.tricommconnect_v1.viewmodel.LoginViewModel
import com.example.tricommconnect_v1.viewmodel.RemoteMessageViewModel
import com.example.tricommconnect_v1.viewmodel.StartupViewModel
import com.example.tricommconnect_v1.data.local.db.LocalDatabaseProvider
import com.example.tricommconnect_v1.data.repository.MessageRepository
import com.example.tricommconnect_v1.socket.MessageSocketHandler
import com.example.tricommconnect_v1.viewmodel.MessageViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Create & connect
        MessageSocketHandler.connect()
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
    val chatDao = remember { LocalDatabaseProvider.provideChatDao(context) }
    val messageDao = remember { LocalDatabaseProvider.provideMessageDao(context) }
    val repo = remember { ChatRepository(RetrofitInstance.api,chatDao) }
    val remoteRepo = RemoteMessageRepository(RetrofitInstance.api,) // or however you're creating it // Version_2_Cycle_2
    val messageRepository = remember { MessageRepository(RetrofitInstance.api, messageDao) } // this line is modified/change


    val startupViewModel = remember { StartupViewModel(prefs) }
    val startDestination by startupViewModel.startDestination.collectAsState()
    // Only build navigation graph once the destination is known
    if (startDestination != null) {
        NavigationGraph(navController, startDestination!!, prefs, repo, messageRepository) // this line is modified/change
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String,
    prefs: UserPreferences,
    repo: ChatRepository,
    messageRepository: MessageRepository // this line is modified/change
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
            ChatListScreen(vm,navController = navController)
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
            ChatScreen(
                chatId = chatId,
                senderId = senderId,
                viewModel = remember { MessageViewModel(messageRepository) } // this line is modified/change
            )
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