package com.example.tricommconnect_v1.data.local.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tricommconnect_v1.data.local.entity.MessageEntity
import com.example.tricommconnect_v1.data.local.entity.MessageStatus
import com.example.tricommconnect_v1.data.model.Message
import com.example.tricommconnect_v1.data.model.SenderUser
import java.time.Instant
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
fun Message.toEntity(): MessageEntity {
    val safeId = _id ?: UUID.randomUUID().toString()
    val safeChatId = chatId ?: ""
    val safeSenderId = senderUserId?._id ?: ""
    val safeUsername = senderUserId?.username

    val parsedTimestamp = try {
        Instant.parse(time).toEpochMilli()
    } catch (e: Exception) {
        time.toLongOrNull() ?: System.currentTimeMillis()
    }

    return MessageEntity(
        messageId = safeId,
        chatId = safeChatId,
        senderId = safeSenderId,
        senderUsername = safeUsername,
        content = msgBody,
        timestamp = parsedTimestamp,
        status = MessageStatus.SENT
    )
}


//fun Message.toEntity(): MessageEntity {
//    return MessageEntity(
//        messageId = _id ?: "", // fallback in case null
//        chatId = chatId ?: "",
//        senderId = senderUserId?._id ?: "",
//        senderUsername = senderUserId?.username,
//        content = msgBody,
//        timestamp = time.toLongOrNull() ?: System.currentTimeMillis(),
//        status = MessageStatus.SENT
//    )
//}

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
