package com.example.fit5046a2.ui.screens.analysis_ai

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fit5046a2.AI.AnalysisViewModel
import com.example.fit5046a2.R
import com.example.fit5046a2.AI.ChatViewModel
import com.example.fit5046a2.AI.ChatMessage
import com.example.fit5046a2.ui.components.BottomBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun AndroidPreview_AIChat_page() {
    Scaffold {
        paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xffeeca4a),
                            Color(0xffc33524)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset.Infinite
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ){}
    }
}

@Composable
fun Navi_AIChat_page() {
    val navController = rememberNavController()
    Scaffold (
        bottomBar = {BottomBar(navController)}
    ){ paddingValues ->
        // padding of the scaffold is enforced to be used
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ){
            AIChatPage(navController = navController)
        }
    }
}

@Composable
fun AIChatPage(
    navController: NavController,
    viewModel: ChatViewModel = viewModel(),
    analysisViewModel: AnalysisViewModel = viewModel()
) {
    val image = painterResource(id = R.drawable.morki_bot)
    val chatState by viewModel.chatState.collectAsState()
    val graphState by analysisViewModel.graphState.collectAsState()
    var userInput by remember { mutableStateOf("") }
    
    // Get analysis content from ChatViewModel
    val (description, recommendation) = viewModel.getAnalysisContent()
    val isComingFromAnalysis = viewModel.isComingFromAnalysis()

    // Track if we have analysis data
    val hasAnalysisData = description.isNotEmpty() || isComingFromAnalysis

    // Debug: Log the analysis data
    LaunchedEffect(Unit) {
        println("DEBUG: AIChatPage - isComingFromAnalysis: $isComingFromAnalysis, hasAnalysisData: $hasAnalysisData")
        println("DEBUG: AIChatPage - ChatViewModel description: ${description.take(20)}...")
        println("DEBUG: AIChatPage - GraphState description: ${graphState.graphDescription.take(20)}...")
        println("DEBUG: AIChatPage - hasAnalysisData: $hasAnalysisData")
    }

    // Load chat history when the page is first displayed
    LaunchedEffect(Unit) {
        // First load chat history
        viewModel.loadChatHistory()
        
        // Log the current state for debugging
        println("DEBUG: AIChatPage - On load - isComingFromAnalysis: ${viewModel.isComingFromAnalysis()}")
        println("DEBUG: AIChatPage - On load - Description: ${description.take(20)}...")
        println("DEBUG: AIChatPage - On load - hasAnalysisData: $hasAnalysisData")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = image,
                contentDescription = "Avatar",
                modifier = Modifier
                    .padding(8.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x00000000), shape = CircleShape)
            )
            Column {
                Text("Your AI Friend", fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Spacer(modifier = Modifier.weight(0.5f))
            Text(
                        text = "Back",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFa6d9f7), shape = RoundedCornerShape(8.dp))
                            .clickable {  navController.popBackStack() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        // Info card if user coming from analysis page
        if (hasAnalysisData) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Analysis Results",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Use description from ChatViewModel if available, otherwise use graphState
                    val displayDescription = if (description.isNotEmpty()) description else graphState.graphDescription
                    Text(
                        text = displayDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Use recommendation from ChatViewModel if available, otherwise use graphState
                    val displayRecommendation = if (recommendation.isNotEmpty()) recommendation else graphState.graphRecommendation
                    if (displayRecommendation.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        Text(
                            text = "Recommendation:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = displayRecommendation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Add a button to send this analysis to the chat
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val analysisText = "Please help me understand this analysis:\n" +
                                "Description: $displayDescription\n" +
                                "Recommendation: $displayRecommendation"
                            viewModel.sendMessage(analysisText)
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Ask about this analysis")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(chatState.messages) { message ->
                AIChat(message)
            }
        }

        // Error message if any
        chatState.error?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Loading indicator
        if (chatState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            )
        }

        // Input field
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            placeholder = { Text("Type a message...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            viewModel.sendMessage(userInput)
                            userInput = ""
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.Black
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (userInput.isNotBlank()) {
                        viewModel.sendMessage(userInput)
                        userInput = ""
                    }
                }
            )
        )
    }
}

@Composable
fun AIChat(chatMessage: ChatMessage) {
    val image = painterResource(id = R.drawable.morki_bot)
    val backgroundColor = if (chatMessage.sentByUser) Color(0xFFF1F1F1) else Color(0xFFa6d9f7)
    val alignment = if (chatMessage.sentByUser) Arrangement.End else Arrangement.Start
    val textColor = if (chatMessage.sentByUser) Color.Black else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = alignment
    ) {
        Column(horizontalAlignment = if (chatMessage.sentByUser) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier
                    .background(backgroundColor, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!chatMessage.sentByUser) {
                        Image(
                            painter = image,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFFFFF), shape = CircleShape)
                        )
                    }
                    Text(chatMessage.text, color = textColor)
                }
            }
        }
    }
}
