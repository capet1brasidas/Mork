package com.example.fit5046a2.data.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fit5046a2.ui.screens.notice.ChatEmailMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Date
import java.util.Properties
import javax.mail.Folder
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress

class ColleagueChatViewModel(application: Application): AndroidViewModel(application){
    private val _chatMessages = MutableStateFlow<List<ChatEmailMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatEmailMessage>> = _chatMessages.asStateFlow()

    private var job: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadChatMessages(
        sender: String,
        receiver: String,
        pwd: String,
        host: String,
        port: String
    ) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                Log.d("chat_page","receiving message")
            try {
                val messages = receiveEmailsInteg( sender,receiver, pwd, host, port)
                _chatMessages.value = messages
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Failed to load emails", e)
            }
                delay(5_000)}

        }
    }

    fun stopLoading(){
        job?.cancel()
        Log.d("chat_page","job cancelled by button")
    }

    override fun onCleared() {
        job?.cancel()
        Log.d("chat_page","job cancelled")
        super.onCleared()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun receiveEmailsInteg(senderUsername: String, receiverUsername: String, password: String, host: String, port: String): List<ChatEmailMessage> {
        val emailChatMessagesReceiver = receiveEmails(
            senderUsername = senderUsername,
            receiverUsername =  receiverUsername,
            password,
            host,
            port,
            false
        )
        val emailChatMessagesSender = receiveEmails(
            senderUsername = receiverUsername,
            receiverUsername = senderUsername,
            password,
            host,
            port,
            true
        )
        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.ENGLISH)
        var allChatMessages = (emailChatMessagesReceiver + emailChatMessagesSender).sortedBy {
            try {
                ZonedDateTime.parse(it.timestamp, formatter).toLocalDateTime()
            } catch (e: Exception) {
                LocalDateTime.MIN
            }
        }
        return  allChatMessages

    }

    fun receiveEmails(senderUsername: String,receiverUsername: String, password: String, host: String, port: String, sentByUser: Boolean): List<ChatEmailMessage> {
        val messagesList = mutableListOf<ChatEmailMessage>()
        try {
            val props = Properties()
            props["mail.store.protocol"] = "imap"
            props["mail.imap.host"] = host
            props["mail.imap.port"] = port

            val session = Session.getInstance(props, null)
            val store = session.getStore("imap")
            store.connect(host, receiverUsername, password)

            val inbox = store.getFolder("INBOX")
            inbox.open(Folder.READ_ONLY)

            val messages = inbox.messages
            for (msg in messages) {
                val from = (msg.from.firstOrNull() as? InternetAddress)?.address ?: "unknown"
                val to = (msg.getRecipients(Message.RecipientType.TO)?.firstOrNull() as? InternetAddress)?.address ?: "unknown"
                val content = msg.content.toString().trim().lines().firstOrNull() ?: ""
                val timestamp = msg.sentDate?: Date()
                if(from.contains(senderUsername)){
                    messagesList.add(ChatEmailMessage(from, to, content, sentByUser = sentByUser, timestamp = timestamp.toString()))
                }

            }

            inbox.close(false)
            store.close()
        } catch (e: Exception) {
            println("Failed to receive emails: ${e.message}")
            e.printStackTrace()
        }

        return messagesList
    }
}