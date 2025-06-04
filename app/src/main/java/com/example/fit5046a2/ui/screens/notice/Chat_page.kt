package com.example.fit5046a2.ui.screens.notice

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fit5046a2.data.viewmodel.UserViewModel
import com.example.fit5046a2.tensorflowModel.TextClassifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Date
import javax.mail.*
import javax.mail.internet.InternetAddress
import java.util.Properties
import com.example.fit5046a2.data.viewmodel.ColleagueChatViewModel
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.mail.internet.MimeMessage

data class ChatEmailMessage(
    val from: String,
    val to: String,
    val content: String,
    val subject: String = "Chat Message",
    val sentByUser: Boolean = false,
    val timestamp: String
)

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition",
    "UnrememberedGetBackStackEntry"
)
@Composable
    fun Chat_page(
    navController: NavController,
    userViewModel: UserViewModel,
    email: String,
    colleagueChatViewModel: ColleagueChatViewModel
) {
        // Dialog and toast state
        val openDialog = remember { mutableStateOf(false) }
        val toastMessage = remember { mutableStateOf("") }
        val context = LocalContext.current
        val classifier = remember { TextClassifier(context) }
        userViewModel.loadCurrentUser()
        userViewModel.loadAllUsersFromFirebase()
        var currentUser = userViewModel.currentUser
        var allUsers = userViewModel.firebaseUsers.collectAsState(initial = emptyList())
        val messageReceiver = allUsers.value.find { it.email == email }
        val senderEmail = "a${currentUser?.value?.email?.split("@")[0]}"
        val receiverEmail = "a${messageReceiver?.email?.split("@")[0]}"
        val emailPwd = "1234"
        val emalServer = "4.147.153.183"
        val emailSendPort = 587
        val emailReceivePort = 143
        val subject = "Chat Message"
        println("sender :${senderEmail}")
        println("receiver: ${receiverEmail}")

        var inputMessage by remember { mutableStateOf("") }
        var allChatMessages by remember { mutableStateOf(listOf<ChatEmailMessage>()) }
//        val chatViewModel: ChatViewModel = viewModel(
//            viewModelStoreOwner = navController.getBackStackEntry("chat/${email}")
//        )
        colleagueChatViewModel.loadChatMessages(receiverEmail, senderEmail, emailPwd, emalServer, emailReceivePort.toString())

//    LaunchedEffect(Unit) {
//            chatViewModel.loadChatMessages(receiverEmail, senderEmail, emailPwd, emalServer, emailReceivePort.toString())
//        }

        allChatMessages = colleagueChatViewModel.chatMessages.collectAsState().value


//        try {
//            CoroutineScope(Dispatchers.IO).launch {
//                allChatMessages = receiveEmailsInteg(receiverEmail,senderEmail,emailPwd,emalServer,emailReceivePort.toString())
//            }
//        } catch (e: Exception) {
//                    e.printStackTrace()
//                }


    Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF453a63))
                .padding(8.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color(0xFFe0a8af),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(32.dp)
                            .background(Color(0xFFe0a8af), shape = CircleShape)
                    )
                    Column {
                        Text(text = email, color = Color.White, fontSize = 14.sp)
                        Text("${messageReceiver?.position}", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Online", fontSize = 12.sp, color = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Analyze Emotion",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFf7c97f), shape = RoundedCornerShape(8.dp))
                            .clickable {
                                val targetMessages = allChatMessages.filter { !it.sentByUser }
                                if (targetMessages.isNotEmpty()) {
                                    val combinedText = targetMessages.joinToString(" ") { it.content }
                                    val result = classifier.classify(combinedText)
                                    val comment = when {
                                        result[1] >= 0.5 -> "Congrats your colleague is satisfied with you"
                                        result[0] > 0.5 -> "Sorry, it seems your colleague doesn't like you"
                                        else -> "😐 Mixed or neutral tone detected."
                                    }
                                    toastMessage.value = "Negative: %.2f, Positive: %.2f\n%s\n\nDisclaimer: This result is based on AI model predictions.".format(
                                        result[0], result[1], comment
                                    )
                                    openDialog.value = true
                                } else {
                                    toastMessage.value = "No messages to analyze"
                                    openDialog.value = true
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                    Text(
                        text = "Back",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFa6d9f7), shape = RoundedCornerShape(8.dp))
                            .clickable {
                                navController.navigate("notice")
                                colleagueChatViewModel.stopLoading()
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                items(allChatMessages.size) { index ->
                    ChatBubble(allChatMessages[index])
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    placeholder = { Text("text...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Text(
                    text = "Send",
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFFa6d9f7), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clickable {
                            if (inputMessage.isNotBlank()) {
                                val message = ChatEmailMessage(
                                    from = senderEmail,
                                    to = receiverEmail,
                                    subject = subject,
                                    content = inputMessage,
                                    timestamp = LocalDateTime.now().toString()
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        sendEmail(
                                            message = message,
                                            username = senderEmail,
                                            password = emailPwd,
                                            host = emalServer,
                                            port = emailSendPort.toString()
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                inputMessage = ""
                            }
                        }
                )
            }
            // AlertDialog for analysis result
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = { openDialog.value = false },
                    confirmButton = {
                        Text(
                            text = "OK",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { openDialog.value = false }
                        )
                    },
                    title = { Text("Analysis Result") },
                    text = { Text(toastMessage.value) }
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun ChatBubble(chatEmailMessage: ChatEmailMessage) {
        val backgroundColor = if (chatEmailMessage.sentByUser) Color(0xFFF1F1F1) else Color(0xFFa6d9f7)
        val alignment = if (chatEmailMessage.sentByUser) Arrangement.End else Arrangement.Start
        val textColor = if (chatEmailMessage.sentByUser) Color.Black else Color.Black

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = alignment
        ) {
            Column(horizontalAlignment = if (chatEmailMessage.sentByUser) Alignment.End else Alignment.Start) {
                val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                val timeOnly = try {
                    ZonedDateTime.parse(chatEmailMessage.timestamp, formatter)
                        .toLocalTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm"))
                } catch (e: Exception) {
                    chatEmailMessage.timestamp
                }

                Text(
                    text = "${chatEmailMessage.from.split("@")[0].substring(1)} $timeOnly",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                )
                Box(
                    modifier = Modifier
                        .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(chatEmailMessage.content, color = textColor)
                }
            }
        }
    }



fun sendEmail(message: ChatEmailMessage, username: String, password: String, host: String, port: String) {
    try {
        val props = System.getProperties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.host"] = host
        props["mail.smtp.port"] = port

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        val mimeMessage = MimeMessage(session)
        mimeMessage.setFrom(InternetAddress(message.from))
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(message.to))
        mimeMessage.subject = message.subject

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val timestamp = formatter.format(Date())
        mimeMessage.setText("${message.content}\n\nSent at: $timestamp")

        Transport.send(mimeMessage)
        println("Email sent successfully.")


    } catch (e: Exception) {
        println("Failed to send email: ${e.message}")
        e.printStackTrace()
    }
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
    val formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
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
