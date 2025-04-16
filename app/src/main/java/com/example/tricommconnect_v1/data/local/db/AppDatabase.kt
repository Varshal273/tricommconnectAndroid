package com.example.tricommconnect_v1.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tricommconnect_v1.data.local.dao.ChatDao
import com.example.tricommconnect_v1.data.local.dao.MessageDao
import com.example.tricommconnect_v1.data.local.entity.ChatEntity
import com.example.tricommconnect_v1.data.local.entity.MessageEntity

@Database(entities = [ChatEntity::class, MessageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
