package com.example.tricommconnect_v1.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tricommconnect_v1.data.local.dao.ChatDao
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.entity.ChatEntity
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import androidx.room.Room

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tricomm_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
