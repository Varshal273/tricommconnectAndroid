package com.example.tricommconnect_v1.data.local.db

import android.content.Context
import androidx.room.Room
import com.example.tricommconnect_v1.data.local.dao.ChatDao
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.db.AppDatabase

object LocalDatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "tricomm_database"
            ).fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }

    fun provideChatDao(context: Context): ChatDao {
        return provideDatabase(context).chatDao()
    }

    fun provideMessageDao(context: Context): MessageDao {
        return provideDatabase(context).messageDao()
    }
}
