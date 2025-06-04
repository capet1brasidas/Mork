package com.example.fit5046a2.ui.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fit5046a2.R
import com.example.fit5046a2.ui.components.BottomBar
import com.example.fit5046a2.data.viewmodel.HomeViewModel
import com.example.fit5046a2.data.entity.ToDo
import com.example.fit5046a2.AI.HomeViewModel as AIHomeViewModel
import kotlin.collections.get


// Enum to identify box types
enum class BoxType {
    RANKING,
    PROGRESS,
    DAY_COUNT,
    RECT_PROGRESS,
    RECT_DAY_COUNT
}

// Data class to hold box content
data class BoxData(
    val type: BoxType,
    val content: @Composable () -> Unit
)


@Composable
fun HomePageSquareBoxes(homeViewModel: HomeViewModel) {
    val recentTaskProgress by homeViewModel.recentTaskProgress.collectAsState()

    val boxContents = listOf(
        BoxData(
            type = BoxType.RANKING,
            content = { RankingBox(rank = 4.5f, title = "Your Ranking") }
        ),
        BoxData(
            type = BoxType.RANKING,
            content = { RankingBox(rank = 3.8f, title = "Team Ranking") }
        ),
        BoxData(
            type = BoxType.DAY_COUNT,
            content = { TimeBox(time = 30f, title = "on time", units = "days") }
        ),
        BoxData(
            type = BoxType.PROGRESS,
            content = { ProgressBox(progress = recentTaskProgress, title = "today task") }
        ),
        BoxData(
            type = BoxType.PROGRESS,
            content = { ProgressBox(progress = recentTaskProgress, title = "weekly task") }
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        boxContents.forEach {
            DashBoradSquareBoxLayout(content = it.content)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AndroidPreview_Home_page() {
//    RankingBox(rank = 4.5f, title = "Preview Ranking")
//    ProgressBox(progress = 0.75f, title = "Preview Progress")
//    TimeBox(time = 5f, title = "Preview Time", units = "days")
//    RectBox(data = 80f, progress = 0.8f, title = "Preview Box", descr = "Description", icon = R.drawable.clock_icon)
//    DashBoradSquareBoxLayout { Text("Preview") }
//    DashBoradRectBoxLayout { Text("Preview") }
//    Home_page()
    Navi_Homepage()
}

@Composable
fun Navi_Homepage() {
    val navController = rememberNavController()
    Scaffold (
//        topBar = {TopBarAfterLogin()},
        bottomBar = {BottomBar(navController)}
    ){ paddingValues ->
        // padding of the scaffold is enforced to be used
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ){
//            MainNavigation(navController = navController)
            Home_page(navController = navController)
        }
    }

}

@Composable
fun CircleImageButton(
    navController: NavHostController, 
    onClick: () -> Unit = { navController.navigate("chat_ai")}

) {
    Box(
        modifier = Modifier
            .fillMaxSize() // Takes up full screen
            .padding(bottom = 10.dp, end = 25.dp), // More padding from the right edge
        contentAlignment = Alignment.BottomCenter // Align content to the bottom center
    ) {
        Image(
            painter = painterResource(id = R.drawable.morki_bot),
            contentDescription = "AI Button",
            modifier = Modifier
                .size(150.dp) // increased size
                .clip(CircleShape) // clip the image into a circle
                .clickable { onClick() } // make it clickable
        )
    }
}

@Composable
fun CircleImageButtonWithTalkBox(
    viewModel: AIHomeViewModel = viewModel()
) {
    val message by viewModel.aiMessage.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 3.dp).padding(start = 3.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Talk box
        if (message.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .background(
                        color = Gray,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun Home_page(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel()
) {
    // State for drawer and selected project
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedProjectIndex by remember { mutableStateOf(0) }

    // Collect data from HomeViewModel
    val currentUser by homeViewModel.currentUser.collectAsState()
    val userProjects by homeViewModel.userProjects.collectAsState()
    val projectProgress by homeViewModel.projectProgress.collectAsState()
    val recentTaskTodos by homeViewModel.recentTaskTodos.collectAsState()
    val recentTaskProgress by homeViewModel.recentTaskProgress.collectAsState()

    // Load todos for initial project
    LaunchedEffect(userProjects) {
        if (userProjects.isNotEmpty() && selectedProjectIndex < userProjects.size) {
            homeViewModel.onProjectSelected(userProjects[selectedProjectIndex].projectId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xffB2E2EF), Color(0xff61A5D8))
                )
            )
    ) {
        // Navigation drawer implementation
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                // Drawer content
                ModalDrawerSheet(
                    modifier = Modifier
                        .width(250.dp)
                        .fillMaxHeight(),
                    drawerContainerColor = Color(0xEEFFFFFF)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x66FFF0E8E8))
                            .padding(16.dp)
                    ) {
                        Text(
                            "Your Performance",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Divider()
                    Spacer(modifier = Modifier.height(24.dp))

                    // Square boxes in drawer
                    HomePageSquareBoxes(homeViewModel)
                }
            }
        ) {
            // Main content
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left column (dashboard button)
                        Column(
                            modifier = Modifier
                                .weight(0.15f)
                                .fillMaxHeight()
                                .padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Dashboard button
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.apply {
                                            if (isClosed) open() else close()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .background(Color(0x66FFFFFF), CircleShape)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.dashboard_icon),
                                    contentDescription = "Menu",
                                    tint = Color.Black,
                                    modifier = Modifier.fillMaxSize().padding(8.dp)
                                )
                            }
                        }

                        // Right column (vertical progress and todo list)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color.White)
                                .padding(end = 10.dp),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            // Project selector
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Dropdown for project selection
                                Box {
                                    var expanded by remember { mutableStateOf(false) }

                                    Row(
                                        modifier = Modifier
                                            .clickable { expanded = true }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (userProjects.isNotEmpty() && selectedProjectIndex < userProjects.size) {
                                                userProjects[selectedProjectIndex].name ?: "Unnamed Project"
                                            } else "No Projects",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color(0xFF4695DF)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Select Project",
                                            tint = Color(0xFF4695DF),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        userProjects.forEachIndexed { index, project ->
                                            DropdownMenuItem(
                                                text = { Text(project.name ?: "Unnamed Project") },
                                                onClick = {
                                                    selectedProjectIndex = index
                                                    expanded = false
                                                    homeViewModel.onProjectSelected(project.projectId)
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Use fillMaxHeight with weight for proper distribution
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.5f)
                            ) {
                                if (userProjects.isNotEmpty() && selectedProjectIndex < userProjects.size) {
                                    val project = userProjects[selectedProjectIndex]
                                    val progress = projectProgress[project.projectId] ?: 0f
                                    DashBoradRectBoxLayout {
                                            VerticalProgressBox(
                                                data = progress * 100,
                                                progress = progress,
                                                title = project.name ?: "Unnamed Project",
                                                projectId = project.projectId
                                            )
                                    }
                                }
                            }

                            // Todo list box with same style as progress box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.5f)
                            ) {
                                DashBoradRectBoxLayout {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Log.d(
                                            "HomePage",
                                            "Todo list size: ${recentTaskTodos.size}"
                                        )
                                        Column(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "Todo List",
                                                style = MaterialTheme.typography.titleLarge,
                                                color = Color(0xFF4695DF),
                                                modifier = Modifier.padding(16.dp)
                                            )
                                            LazyColumn(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .padding(horizontal = 16.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                items(
                                                    items = recentTaskTodos,
                                                    key = { todo -> todo.todoId }
                                                ) { todo ->
                                                    TaskItem(
                                                        todo = todo,
                                                        onCheckedChange = { isChecked ->
                                                            homeViewModel.updateTodoStatus(
                                                                todo,
                                                                isChecked
                                                            )
                                                        },
                                                        onDelete = {
                                                            homeViewModel.deleteTodo(todo)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Chat bot background box that spans both columns
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(0.4f)) {
                                    CircleImageButton(navController = navController)
                                }
                                Box(modifier = Modifier.weight(0.6f)) {
                                    CircleImageButtonWithTalkBox()
                                }

                            }
                        }
                }
            }
        }
    }



}

@Composable
fun DashBoradSquareBoxLayout(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Box(
            modifier = modifier
                .padding(3.dp)
                .size(110.dp, 110.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x66FFF0E8E8))
        ) {
            content()
        }
    }

@Composable
fun DashBoradRectBoxLayout(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Box(
            modifier = modifier
                .padding(3.dp).padding(start = 3.dp)
                .fillMaxHeight()
                .width(350.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x66FFF0E8E8))
        ) {
            content()
        }
    }

@Composable
fun RankingBox(
        rank: Float,
        title: String,
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ranking_star),
                contentDescription = "Star Rating",
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            // Rank number overlaid on top
            Text(
                text = String.format("%.1f", rank),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center).padding(4.dp)
            )
        }

    }

@Composable
fun ProgressBox(
        progress: Float,
        title: String
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 10.dp,
                color = Color(0xFF4695DF)  // Progress color
            )

            // Percentage text in the center of the circle
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center).padding(8.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp)
            )
        }

    }

@Composable
fun TaskItem(
        todo: ToDo,
        onCheckedChange: (Boolean) -> Unit,
        onDelete: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = onCheckedChange
                )
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 3.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task"
                )
            }
        }
    }

@Composable
fun TimeBox(
        time: Float,
        title: String,
        units: String
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.clock_icon),
                contentDescription = "Star Rating",
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(Color(0x669F8374))
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            Text(
                text = String.format("%.1f", time),
                style = MaterialTheme.typography.displayMedium,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center).padding(4.dp)
            )
            Text(
                text = units,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black,
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
            )
        }

    }

@Composable
fun VerticalProgressBox(
    data: Float,
    progress: Float,
    title: String,
    projectId: Int? = null,
    homeViewModel: HomeViewModel = viewModel()
) {
    val projectTaskStatusPercentages by homeViewModel.projectTaskStatusPercentages.collectAsState()

    val statusPercentages = projectId?.let { projectTaskStatusPercentages[it] } ?: mapOf(
        "Done" to 0f,
        "In Progress" to 0f,
        "To Do" to 0f
    )

    val done = statusPercentages["Done"] ?: 0f
    val inProgress = statusPercentages["In Progress"] ?: 0f
    val toDo = statusPercentages["To Do"] ?: 0f

    Box(modifier = Modifier.fillMaxSize()) {
        val labelStyle = MaterialTheme.typography.bodySmall

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 15.dp, bottom = 15.dp)
                .width(120.dp)
                .align(Alignment.Center)
        ) {
            // Left-aligned labels
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp) // increased from 80.dp
                    .align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                if (done > 0f) {
                    Text(
                        text = "Done: ${(done * 100).toInt()}%",
                        style = labelStyle,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier
                            .align(Alignment.End)
                            .weight(done)
                            .wrapContentWidth(unbounded = true)
                    )
                }
                if (inProgress > 0f) {
                    Text(
                        text = "In Progress: ${(inProgress * 100).toInt()}%",
                        style = labelStyle,
                        color = Color(0xFF4695DF),
                        modifier = Modifier
                            .align(Alignment.End)
                            .weight(inProgress)
                            .wrapContentWidth(unbounded = true)
                    )
                }
                if (toDo > 0f) {
                    Text(
                        text = "To Do: ${(toDo * 100).toInt()}%",
                        style = labelStyle,
                        color = Gray,
                        modifier = Modifier
                            .align(Alignment.End)
                            .weight(toDo)
                            .wrapContentWidth(unbounded = true)
                    )
                }
            }

            // Vertical Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(20.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
                    .align(Alignment.CenterEnd)
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(20.dp)
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                if (done > 0f) {
                    Box(modifier = Modifier.weight(done).fillMaxWidth().background(Color(0xFF4CAF50)))
                }
                if (inProgress > 0f) {
                    Box(modifier = Modifier.weight(inProgress).fillMaxWidth().background(Color(0xFF4695DF)))
                }
                if (toDo > 0f) {
                    Box(modifier = Modifier.weight(toDo).fillMaxWidth().background(Gray))
                }
            }
        }

        // Legend below bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 4.dp)
        ) {
            listOf("Done" to Color(0xFF4CAF50), "In Progress" to Color(0xFF4695DF), "To Do" to Gray).forEach { (label, color) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).background(color, shape = CircleShape))
                    Text(text = label, style = labelStyle, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}
