package com.example.tricommconnect_v1.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tricommconnect_v1.data.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property for DataStore
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private val gson = Gson()

    companion object {
        private val USER_KEY = stringPreferencesKey("user_json")
    }

    // Save full User object as JSON string
    suspend fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        context.dataStore.edit { prefs ->
            prefs[USER_KEY] = userJson
        }
    }

    // Read full User object from DataStore
    val userFlow: Flow<User?> = context.dataStore.data.map { prefs ->
        prefs[USER_KEY]?.let { json ->
            gson.fromJson(json, User::class.java)
        }
    }

    // Optionally expose token and ID directly
    val userTokenFlow: Flow<String?> = userFlow.map { it?.token }
    val userIdFlow: Flow<String?> = userFlow.map { it?.user?._id }
}


//this is second code
//package com.example.tricommconnect_v1.data.preferences
//
//import android.content.Context
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.preferencesDataStore
//import kotlinx.coroutines.flow.map
//import com.example.tricommconnect_v1.data.model.User
//
//// Correct way to define extension property for DataStore
//private val Context.dataStore by preferencesDataStore(name = "user_prefs")
//
//class UserPreferences(private val context: Context) {
//
//    // Use the extension correctly
//    private val dataStore = context.dataStore
//
//    val userToken = dataStore.data.map { preferences ->
//        preferences[PreferencesKeys.TOKEN] ?: ""
//    }
//
//    val userId = dataStore.data.map { preferences ->
//        preferences[PreferencesKeys.USER_ID] ?: ""
//    }
//
//    suspend fun saveUser(user: User) {
//        dataStore.edit { preferences ->
//            preferences[PreferencesKeys.TOKEN] = user.token
//            preferences[PreferencesKeys.USER_ID] = user.id
//        }
//    }
//
//    private object PreferencesKeys {
//        val TOKEN = stringPreferencesKey("token")
//        val USER_ID = stringPreferencesKey("user_id")
//    }
//}

// this is first code
//package com.example.tricommconnect_v1.data.preferences
//
//import android.content.Context
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.preferencesDataStore
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.flow.map
//import com.example.tricommconnect_v1.data.model.User
//
//// Extension property for DataStore
//val Context.dataStore by preferencesDataStore(name = "user_prefs")
//
//// data/preferences/UserPreferences.kt
//class UserPreferences(context: Context) {
//    private val dataStore = context.createDataStore("user_prefs")
//
//    val userToken = dataStore.data.map { it[PreferencesKeys.TOKEN] ?: "" }
//    val userId = dataStore.data.map { it[PreferencesKeys.USER_ID] ?: "" }
//
//    suspend fun saveUser(user: User) {
//        dataStore.edit {
//            it[PreferencesKeys.TOKEN] = user.token
//            it[PreferencesKeys.USER_ID] = user.id
//        }
//    }
//
//    private object PreferencesKeys {
//        val TOKEN = stringPreferencesKey("token")
//        val USER_ID = stringPreferencesKey("user_id")
//    }
//}
