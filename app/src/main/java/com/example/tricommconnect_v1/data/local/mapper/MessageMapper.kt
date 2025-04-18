package com.example.tricommconnect_v1.data.local.mapper

import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import com.example.tricommconnect_v1.data.local.entity.MessageStatus
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.data.model.SenderUser

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        messageId = _id ?: "", // fallback in case null
        chatId = chatId ?: "",
        senderId = senderUserId?._id ?: "",
        senderUsername = senderUserId?.username,
        content = msgBody,
        timestamp = time.toLongOrNull() ?: System.currentTimeMillis(),
        status = MessageStatus.SENT
    )
}

fun MessageEntity.toModel(): Message {
    return Message(
        _id = messageId,
        chatId = chatId,
        senderUserId = SenderUser(
            _id = senderId,
            username = senderUsername ?: "Unknown"
        ),
        msgBody = content,
        time = timestamp.toString()
    )
}
