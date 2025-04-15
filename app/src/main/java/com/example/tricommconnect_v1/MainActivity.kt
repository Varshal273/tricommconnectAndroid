package com.example.tricommconnect_v1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tricommconnect_v1.data.local.entity.ChatEntity
import com.example.tricommconnect_v1.data.preferences.UserPreferences
import com.example.tricommconnect_v1.data.repository.ChatRepository
import com.example.tricommconnect_v1.network.RetrofitInstance
import com.example.tricommconnect_v1.ui.chatlist.ChatListScreen
import com.example.tricommconnect_v1.ui.login.LoginScreen
import com.example.tricommconnect_v1.ui.theme.TriCommConnect_V1Theme
import com.example.tricommconnect_v1.viewmodel.ChatListViewModel
import com.example.tricommconnect_v1.viewmodel.LoginViewModel
import com.example.tricommconnect_v1.viewmodel.StartupViewModel
import com.example.tricommconnect_v1.data.local.AppDatabase

import com.example.tricommconnect_v1.data.local.dao.ChatDao
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.ui.chatdetail.ChatDetailScreen
import com.example.tricommconnect_v1.viewmodel.ChatDetailViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaunchedEffect(Unit) {
                val context = applicationContext
                val db = AppDatabase.getDatabase(context) // Get instance of Room DB
                val chatDao = db.chatDao() // Get DAO from it
                val chatList = listOf(
                    ChatEntity(
                        name = "Chat 1",
                        lastMessage = "Hello!",
                        timestamp = System.currentTimeMillis(),
                        id = "123"
                    ),
                    ChatEntity(
                        name = "Chat 2",
                        lastMessage = "How are you?",
                        timestamp = System.currentTimeMillis(),
                        id = "122"
                    )
                )

                chatDao.insertChats(chatList)

                chatDao.getAllChats().collect {
                    Log.d("ChatData", "Chats: $it")
                }
            }
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
            ChatListScreen(vm) { chatId ->
                // Navigate to ChatDetailScreen with the selected chatId
                navController.navigate("chatdetail/$chatId")
            }
        }
        composable("chatdetail/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId")
            val vm = remember { ChatDetailViewModel(repo, chatId ?: "") }
            ChatDetailScreen(viewModel = vm, currentUserId = "USER_ID_PLACEHOLDER") // Replace with actual user ID
        }
    }
}



//@Composable
//fun NavigationGraph(
//    navController: NavHostController,
//    startDestination: String,
//    prefs: UserPreferences,
//    repo: ChatRepository
//) {
//    NavHost(navController = navController, startDestination = startDestination) {
//        composable("login") {
//            val vm = remember { LoginViewModel(repo, prefs) }
//            LoginScreen(vm) {
//                navController.navigate("chatlist") {
//                    popUpTo("login") { inclusive = true } // Prevent back to login
//                }
//            }
//        }
//        composable("chatlist") {
//            val vm = remember { ChatListViewModel(repo, prefs) }
//            ChatListScreen(vm)
//        }
//    }
//}