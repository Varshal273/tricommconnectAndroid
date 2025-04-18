package com.example.tricommconnect_v1.data.local.mapper

import com.example.tricommconnect_v1.data.local.entity.ChatEntity
import com.example.tricommconnect_v1.data.model.Chat
import com.example.tricommconnect_v1.data.model.LastMessage

fun ChatEntity.toChat(): Chat {
    return Chat(
        _id = chatId,
        groupName = groupName,
        group = emptyList(), // Placeholder
        lastMessage = lastMessage?.let {
            LastMessage(senderUserId = "", msgBody = it, time = updatedAt.toString())
        }
    )
}