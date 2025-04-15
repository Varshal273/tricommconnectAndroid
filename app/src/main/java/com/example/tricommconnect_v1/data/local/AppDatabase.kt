package com.example.tricommconnect_v1.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tricommconnect_v1.data.local.dao.ChatDao
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.entity.*

//@Database(entities = [ChatEntity::class, MessageEntity::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun ChatDao(): ChatDao
//    abstract fun messageDao(): MessageDao
//}

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tricomm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}