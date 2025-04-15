// data/model/User.kt
package com.example.tricommconnect_v1.data.model

data class User(
    val token: String,
    val user: UserDetails
)

data class UserDetails(
    val _id: String,
    val username: String,
    val name: String,
    val email: String,
    val phoneNumber: Long,
    val joinedTo: List<String>,
    val devices: List<String>,
    val settings: Settings
)

data class Settings(
    val notification: Boolean,
    val preferred_channel: String
)
