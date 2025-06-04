package com.example.fit5046a2.ui.screens.task

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.fit5046a2.data.viewmodel.UserTaskViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fit5046a2.data.entity.Task
import com.example.fit5046a2.data.viewmodel.ProjectTaskViewModel
import com.example.fit5046a2.data.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TaskBoardViewModel(application: Application) : AndroidViewModel(application) {
    private val userViewModel = UserViewModel(application)
    private val userTaskViewModel = UserTaskViewModel(application)
    
    val currentUser = userViewModel.currentUser
    
    init {
        userViewModel.loadCurrentUser()
    }
    
    fun getTasks(email: String) = userTaskViewModel.getTasksByUser(email)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Task_Board(navController: NavController) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel()
    val userTaskViewModel: UserTaskViewModel = viewModel()
    val projectTaskViewModel: ProjectTaskViewModel = viewModel()
    val currentUser by userViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val user = currentUser!!
    val taskLists = remember { mutableStateListOf("To Do", "In Progress", "Done") }
    val projects =projectTaskViewModel.getProjectsByUser(user.email).collectAsState(initial = emptyList()).value
    val tasks = remember { mutableStateListOf<Task>() }

    LaunchedEffect(projects) {
        tasks.clear()
        projects.forEach { project ->
            launch {
                userTaskViewModel.getTasksByProject(project.projectId).collect { projectTasks ->
                   tasks.addAll(projectTasks)

                }
            }
        }
    }
//    val tasks by userTaskViewModel.getTasksByUser(user.email).collectAsState(initial = emptyList())
    val pagerState = rememberPagerState()

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        val (header, pager, bottomBar) = createRefs()

        HorizontalPager(
            count = taskLists.size + 1,
            state = pagerState,
            modifier = Modifier
                .constrainAs(pager) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(bottomBar.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) { page ->
            if (page < taskLists.size) {
                val status = taskLists[page]
                val filteredTasks = tasks.filter { it.status == status }
                TaskListScreen(status, filteredTasks, navController)
            } else {
                AddListScreen(onAdd = { newList ->
                    if (newList.isNotBlank()) taskLists.add(newList)
                })
            }
        }
        }
    }



@Composable
fun TaskListScreen(title: String, tasks: List<Task>, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7E9))
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 108.dp, top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(tasks) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate("task_detail/${task.taskId}")
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Due: ${
                                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                                        Date(task.endDate)
                                    )}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("task_detail/null") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("+ Add Task")
                    }
                }
            }
        }
    }
}


@Composable
fun AddListScreen(onAdd: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("+ Add a new list", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = text, onValueChange = { text = it }, label = { Text("List name") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onAdd(text)
            text = ""
        }) {
            Text("Add")
        }
    }
}


