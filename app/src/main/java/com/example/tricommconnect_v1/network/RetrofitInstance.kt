package com.example.tricommconnect_v1.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// network/RetrofitInstance.kt
object RetrofitInstance {
    private const val BASE_URL = "https://organic-meet-monarch.ngrok-free.app"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
