package com.example.tricommconnect_v1.sync

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.tricommconnect_v1.data.local.entity.MessageEntity // 🔥 Unused – can be removed
import com.example.tricommconnect_v1.data.local.entity.MessageStatus
import com.example.tricommconnect_v1.data.local.repository.LocalMessageRepository
import com.example.tricommconnect_v1.data.remote.repository.RemoteMessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class MessageSyncManager(
    private val localMessageRepository: LocalMessageRepository,
    private val remoteMessageRepository: RemoteMessageRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun syncPendingMessages() = withContext(Dispatchers.IO) {
        val pendingMessages = localMessageRepository.getPendingMessages()

        for (message in pendingMessages) {
            try {
                val result = remoteMessageRepository.sendMessage(
                    chatId = message.chatId,
                    senderId = message.senderId,
                    messageBody = message.content
                )

                if (result != null) {
                    // Conflict check: avoid duplicate if already synced
                    val existingMessages = localMessageRepository.getMessagesForChatOnce(message.chatId)
                    val conflict = existingMessages.any {
                        it.content == message.content &&
                                it.senderId == message.senderId &&
                                Math.abs(it.timestamp - Instant.parse(result.time).toEpochMilli()) < 1000L
                    }

                    if (!conflict) {
                        val syncedMessage = message.copy(
                            messageId = result._id ?: message.messageId, // ✅ modified: now gets _id from Message model
                            timestamp = Instant.parse(result.time).toEpochMilli(), // ✅ modified: time is ISO 8601
                            senderUsername = result.senderUserId?.username, // ✅ modified: now accesses nested username
                            status = MessageStatus.SENT // ✅ added: set to SENT
                        )
                        localMessageRepository.insertMessage(syncedMessage)
                    } else {
                        val resolved = message.copy(status = MessageStatus.SENT) // ✅ added: set pending message as SENT if conflict
                        localMessageRepository.insertMessage(resolved)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // Optional: Mark as FAILED
                // val failed = message.copy(status = MessageStatus.FAILED)
                // localMessageRepository.insertMessage(failed)
            }
        }
    }
}
