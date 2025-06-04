package com.example.fit5046a2.ui.screens.analysis_ai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.fit5046a2.AI.ChatViewModel
import com.example.fit5046a2.R
import com.example.fit5046a2.data.viewmodel.AnalysisViewModel
import com.example.fit5046a2.data.viewmodel.UserViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.Locale

@Composable
fun AnalysisPage(navController: NavController) {
    val userViewModel: UserViewModel = viewModel()
    val aiAnalysisViewModel: com.example.fit5046a2.AI.AnalysisViewModel = viewModel()
    val analysisViewModel: AnalysisViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()

    // Trigger loading of the current user
    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    val currentUser by userViewModel.currentUser.collectAsState()
    val email = currentUser?.email ?: ""
    val position = currentUser?.position?.lowercase(Locale.getDefault()) ?: "employee"
    
    // Observe the bar entries and x-axis labels for AI analysis
    val barEntries by analysisViewModel.barEntries.observeAsState(emptyList())
    val xAxisLabels by analysisViewModel.xAxisLabels.observeAsState(emptyList())
    val analysisType by analysisViewModel.currentAnalysisType.observeAsState("Task Completion")
    
    // Trigger analysis when data is available
    LaunchedEffect(barEntries, xAxisLabels, analysisType) {
        if (barEntries.isNotEmpty() && xAxisLabels.isNotEmpty()) {
            println("DEBUG: Triggering analysis from AnalysisPage with ${barEntries.size} entries")
            aiAnalysisViewModel.analyzeGraphData(
                barEntries = barEntries,
                xAxisLabels = xAxisLabels,
                analysisType = analysisType
            )
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Box(modifier = Modifier.weight(1f)) {
            if (position == "employee") {
                AnalysisEmployeePage(navController = navController, email = email)
            } else {
                AnalysisBossPage(navController = navController, currentUserEmail = email)
            }
        }
        
        // Add AIPageGate at the bottom
        Spacer(modifier = Modifier.height(8.dp))
        AIPageGate(
            viewModel = aiAnalysisViewModel,
            navController = navController as NavHostController,
            analysisViewModel = analysisViewModel,
            chatViewModel = chatViewModel
        )
    }
}

@Composable
fun AIPageGate(
    viewModel: com.example.fit5046a2.AI.AnalysisViewModel,
    navController: NavHostController,
    analysisViewModel: AnalysisViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel()
) {
    val graphState by viewModel.graphState.collectAsState()
    val barEntries by analysisViewModel.barEntries.observeAsState(emptyList())
    val xAxisLabels by analysisViewModel.xAxisLabels.observeAsState(emptyList())
    val analysisType by analysisViewModel.currentAnalysisType.observeAsState("Task Completion")
    
    // Debug: Log when graphState changes
    LaunchedEffect(graphState) {
        println("DEBUG: GraphState updated - Description: ${graphState.graphDescription.take(20)}... Recommendation: ${graphState.graphRecommendation.take(20)}...")
    }
    
    // Handle click to navigate to AI chat page
    val onClick = {
        // First, make sure we're on the analysis page
        chatViewModel.updateContext("analysis")
        
        // Update analysis context with graph data
        chatViewModel.updateAnalysisContext(
            barEntries = barEntries,
            xAxisLabels = xAxisLabels,
            analysisType = analysisType
        )
        
        // Update analysis content with description and recommendation
        chatViewModel.updateAnalysisContent(
            description = graphState.graphDescription,
            recommendation = graphState.graphRecommendation
        )
        
        println("DEBUG: Navigating to chat_ai with analysis content - Description: ${graphState.graphDescription.take(20)}...")
        
        // Set the current page to AI Chat, which will preserve "analysis" as the previous page
        chatViewModel.updateContext("AI Chat")
        
//        // Add a small delay to ensure context updates are processed
//        kotlinx.coroutines.delay(100)
        
        // Navigate to chat page
        navController.navigate("chat_ai")
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Create a single clickable card that contains both the image and text
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp) // Set minimum height
                .clickable(onClick = onClick)
                .padding(4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Row for the image and title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Morki bot image at the top left
                    Image(
                        painter = painterResource(id = R.drawable.morki_bot),
                        contentDescription = "AI Analysis",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(4.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Title
                    Text(
                        text = "AI Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Content area with scrolling capability
                if (graphState.graphDescription.isNotEmpty()) {
                    // Use a scrollable column for the content with fixed height
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 200.dp) // Set fixed height range for scroll area
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = graphState.graphDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            if (graphState.graphRecommendation.isNotEmpty()) {
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
                                    text = graphState.graphRecommendation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // Show a message when no analysis is available
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp), // Match the height of the content area
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Click to analyze your data with AI",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateTab(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf("day", "month", "year").forEach { tab ->
            Text(
                text = tab.uppercase(),
                color = if (selectedTab == tab) Color.Blue else Gray,
                modifier = Modifier
                    .clickable { onTabSelected(tab) }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun AnalysisDropdown(
    selectedAnalysisType: String,
    analysisTypes: List<String>,
    onAnalysisTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F7FA))
        ) {
            Text(selectedAnalysisType,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF4695DF))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            analysisTypes.forEach { analysisType ->
                DropdownMenuItem(
                    text = { Text(analysisType) },
                    onClick = {
                        onAnalysisTypeSelected(analysisType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun EmployeeMenu(
    selectedEmail: String,
    emails: List<String>,
    onEmailSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F7FA))
        ) {
            Text(selectedEmail, color = Color.Black)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            emails.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onEmailSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun BarChartView(
    barEntries: List<BarEntry>,
    xAxisLabels: List<String>,
    analysisType: String
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Y-axis label
        Box(
            modifier = Modifier
                .width(70.dp) // Reduced from 120.dp to 20.dp
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No of Tasks",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier
                    .rotate(-90f)
                    .padding(horizontal = 2.dp), // minimal padding
                maxLines = 1,
                softWrap = false
            )
        }

        // BarChart
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 2.dp), // minimal spacing from Y-axis label
            factory = { context ->
                BarChart(context).apply {
                    val label = when (analysisType) {
                        "Task Duration" -> "Average Task Duration (hours)"
                        else -> "Task Completion Count"
                    }

                    val barDataSet = BarDataSet(barEntries, label).apply {
                        colors = ColorTemplate.COLORFUL_COLORS.toList()
                        valueTextSize = 14f
                    }

                    val barData = BarData(barDataSet).apply {
                        barWidth = 0.9f
                    }

                    isDragEnabled = true
                    setTouchEnabled(true)
                    setVisibleXRangeMaximum(5f)         // Show only 5 bars at once
                    moveViewToX(barEntries.size - 5f)      // Scroll to end

                    data = barData

                    legend.textSize = 14f

                    description.isEnabled = false
                    setFitBars(true)

                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                    xAxis.granularity = 1f
                    xAxis.setCenterAxisLabels(false)
                    xAxis.textSize = 14f

                    axisLeft.granularity = 1f
                    axisLeft.axisMinimum = 0f
                    axisLeft.textSize = 14f

                    axisRight.isEnabled = false

                    animateY(1500)
                    invalidate()
                }
            },
            update = { chart ->
                val label = when (analysisType) {
                    "Task Duration" -> "Average Task Duration (hours)"
                    else -> "Task Completion Count"
                }
                val updatedDataSet = BarDataSet(barEntries, label).apply {
                    colors = ColorTemplate.COLORFUL_COLORS.toList()
                    valueTextSize = 14f
                }

                chart.data = BarData(updatedDataSet).apply { barWidth = 0.9f }
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                chart.xAxis.textSize = 14f



                chart.setVisibleXRangeMaximum(5f)         // Show only 5 bars at once
                chart.moveViewToX(barEntries.size - 5f)      // Scroll to end

                chart.axisLeft.textSize = 14f
                chart.legend.textSize = 14f

                chart.invalidate()
            }
        )
    }
}




@Composable
fun AnalysisEmployeePage(navController: NavController, email: String) {
    val analysisViewModel: AnalysisViewModel = viewModel()
    val selectedPeriod = remember { mutableStateOf("day") }

    // Set up the view model with the user's email
    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            analysisViewModel.setUserEmail(email)
            analysisViewModel.setPeriod(selectedPeriod.value)
            analysisViewModel.loadTaskCompletionData()
        }
    }

    // Observe the bar entries and x-axis labels
    val barEntries by analysisViewModel.barEntries.observeAsState(emptyList())
    val xAxisLabels by analysisViewModel.xAxisLabels.observeAsState(emptyList())

    // Available analysis types
    val analysisTypes = listOf("Task Completion", "Task Duration")
    var selectedAnalysisType by remember { mutableStateOf(analysisTypes[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(Color.White)
    ) {
        // Analysis type dropdown
        AnalysisDropdown(
            selectedAnalysisType = selectedAnalysisType,
            analysisTypes = analysisTypes,
            onAnalysisTypeSelected = { newType ->
                selectedAnalysisType = newType
                analysisViewModel.setAnalysisType(newType)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Period selector tabs
        DateTab(
            selectedTab = selectedPeriod.value,
            onTabSelected = { period ->
                selectedPeriod.value = period
                analysisViewModel.setPeriod(period)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bar chart
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
        ) {
            if (barEntries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No completed tasks found for this period", color = Gray)
                }
            } else {
                BarChartView(
                    barEntries = barEntries,
                    xAxisLabels = xAxisLabels,
                    analysisType = selectedAnalysisType
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AnalysisBossPage(navController: NavController, currentUserEmail: String) {
    val analysisViewModel: AnalysisViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    // State for selected employee and period
    var selectedEmail by remember { mutableStateOf(currentUserEmail) }
    var selectedPeriod by remember { mutableStateOf("day") }

    // Load emails once on first composition
    LaunchedEffect(Unit) {
        userViewModel.loadUserEmails()
    }

    val userEmails by userViewModel.userEmails.collectAsState()

    // Set up the view model with the selected employee's email
    LaunchedEffect(selectedEmail, selectedPeriod) {
        if (selectedEmail.isNotEmpty()) {
            analysisViewModel.setUserEmail(selectedEmail)
            analysisViewModel.setPeriod(selectedPeriod)
            analysisViewModel.loadTaskCompletionData()
        }
    }

    // Observe the bar entries and x-axis labels
    val barEntries by analysisViewModel.barEntries.observeAsState(emptyList())
    val xAxisLabels by analysisViewModel.xAxisLabels.observeAsState(emptyList())

    // Available analysis types
    val analysisTypes = listOf("Task Completion", "Task Duration")
    var selectedAnalysisType by remember { mutableStateOf(analysisTypes[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(Color.White)
    ) {
        // Analysis type dropdown
        AnalysisDropdown(
            selectedAnalysisType = selectedAnalysisType,
            analysisTypes = analysisTypes,
            onAnalysisTypeSelected = { newType ->
                selectedAnalysisType = newType
                analysisViewModel.setAnalysisType(newType)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Employee dropdown
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Employee: ",
                color = Color.Black,
                modifier = Modifier.padding(end = 8.dp)
            )

            EmployeeMenu(
                selectedEmail = selectedEmail,
                emails = userEmails,
                onEmailSelected = { email -> selectedEmail = email }
            )
        }

        // Period selector tabs
        DateTab(
            selectedTab = selectedPeriod,
            onTabSelected = { period -> selectedPeriod = period }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bar chart
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .padding(2.dp)
        ) {
            if (barEntries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No completed tasks found for this period", color = Gray)
                }
            } else {
                BarChartView(
                    barEntries = barEntries,
                    xAxisLabels = xAxisLabels,
                    analysisType = selectedAnalysisType
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
