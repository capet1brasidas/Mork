package com.example.fit5046a2.ui.screens.task

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fit5046a2.data.entity.Tag
import com.example.fit5046a2.data.entity.Task
import com.example.fit5046a2.data.entity.ToDo
import com.example.fit5046a2.data.viewmodel.ProjectTaskViewModel
import com.example.fit5046a2.data.viewmodel.UserTaskViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import androidx.core.graphics.toColorInt
import com.example.fit5046a2.data.entity.TaskTag
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetail_page(navController: NavController, taskId: Int?) {
    val context = LocalContext.current
    val userTaskViewModel: UserTaskViewModel = viewModel()
    val todoItems = remember { mutableStateListOf<ToDo>() }
    val coroutineScope = rememberCoroutineScope()
    val allTags = userTaskViewModel.getAllTags().collectAsState(initial = emptyList()).value
    var isNewTask = taskId == null
    var task: Task? = if (isNewTask) null else userTaskViewModel.getTaskById(taskId!!).collectAsState(initial = null).value
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedAssignee by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var endDateLong by remember { mutableStateOf<Long?>(null) }
    val taskTagsLoaded =  remember { mutableStateListOf<Tag>() }
//    if (!isNewTask && taskId != null){
//
//    }
    LaunchedEffect(task) {
        if (!isNewTask && task != null) {
            taskTitle = task.title
            taskDescription = task.description ?: ""
            selectedAssignee = task.email ?: "nobody"
            selectedStatus = task.status ?: "To Do"
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            selectedDate = formatter.format(Date(task.endDate))
            endDateLong = task.endDate
        }
    }

    var newTodoText by remember { mutableStateOf("") }
    val project by remember { mutableStateOf(task?.projectId?: 3) }

    var selectedProjectId by remember { mutableStateOf<Int?>(null) }
    val projectTaskViewModel: ProjectTaskViewModel = viewModel()
    var allUsersInCurrentProject = projectTaskViewModel.getUsersByProject(project).collectAsState(initial = emptyList()).value
    val allProjects = projectTaskViewModel.getAllProjects().collectAsState(initial = emptyList()).value
    val statusOptions = listOf("To Do", "In Progress", "Done")

    LaunchedEffect(taskId) {
        if (!isNewTask && taskId != null) {
            userTaskViewModel.getTodosForTask(taskId).collect { todos ->
                todoItems.clear()
                todoItems.addAll(todos)
            }


        }
    }
    LaunchedEffect(taskId) {
        if (!isNewTask && taskId != null) {
            println("tasktags")
            userTaskViewModel.getTagsByTaskId(taskId).collect { taskTags ->
                taskTagsLoaded.clear()
                taskTagsLoaded.addAll(taskTags)
            }
        }
    }
    taskTagsLoaded.forEach { tag ->
        println(tag)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xffdcf9fe))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Task Title
                TextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (taskTitle.isNotEmpty()) {
                            IconButton(onClick = { taskTitle = "" }) {
                                Icon(Icons.Rounded.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Task Description
                TextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Enter Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                    singleLine = false,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
            if(!isNewTask){
                item {
                    // Tags display
                    Text("Tags:", style = MaterialTheme.typography.subtitle1)
                    if (taskTagsLoaded.isEmpty()) {
                        Text("None", modifier = Modifier.padding(start = 8.dp))
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            taskTagsLoaded.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(tag.color.toColorInt()))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = tag.name ?: "Unnamed")
                                }
                            }
                        }
                    }
                }
                item{

                    // Add Tag Dropdown
                    Spacer(modifier = Modifier.height(8.dp))

                    // Add Tag Dropdown
                    var expandedTag by remember { mutableStateOf(false) }
                    var selectedTagToAdd by remember { mutableStateOf<Tag?>(null) }
                    val availableTags = allTags.filterNot { existing -> taskTagsLoaded.any { it.tagId == existing.tagId } }

                    ExposedDropdownMenuBox(
                        expanded = expandedTag,
                        onExpandedChange = { expandedTag = !expandedTag },
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedTagToAdd?.name ?: "Add Tag",
                            onValueChange = {},
                            label = { Text("Add Tag") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTag) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTag,
                            onDismissRequest = { expandedTag = false }
                        ) {
                            availableTags.forEach { tag ->
                                DropdownMenuItem(
                                    content = { Text(tag.name ?: "Unnamed") },
                                    onClick = {
                                        selectedTagToAdd = tag
                                        expandedTag = false
                                        coroutineScope.launch {
                                            if (taskId != null && tag.tagId != null) {
                                                val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                                                var newTaskTag = TaskTag(
                                                    taskId = taskId,
                                                    tagId = tag.tagId,
                                                    addedAt = now,
                                                    addedBy = "developer@example.com"
                                                )
                                                userTaskViewModel.insertTaskTag(newTaskTag)
                                                taskTagsLoaded.add(tag)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

            }

            item {
                // Assignee and Status Dropdowns (Refactored using ExposedDropdownMenuBox)
                var expandedAssignee by rememberSaveable { mutableStateOf(false) }
                val assigneeOptions = listOf("nobody") + allUsersInCurrentProject.mapNotNull { it.email }.distinct()
                var expandedStatus by rememberSaveable { mutableStateOf(false) }


                // Assignee Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedAssignee,
                    onExpandedChange = { expandedAssignee = !expandedAssignee },
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedAssignee,
                        onValueChange = {},
                        label = { Text("Assignee") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAssignee) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAssignee,
                        onDismissRequest = { expandedAssignee = false }
                    ) {
                        assigneeOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                content = { Text(selectionOption) },
                                onClick = {
                                    selectedAssignee = selectionOption
                                    expandedAssignee = false
                                }
                            )
                        }
                    }
                }
                // Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus },
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedStatus,
                        onValueChange = {},
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        statusOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                content = { Text(selectionOption) },
                                onClick = {
                                    selectedStatus = selectionOption
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                // Project Dropdown for new task
                if (isNewTask) {
                    var expandedProject by rememberSaveable { mutableStateOf(false) }
                    var project = allProjects.firstOrNull{ it.projectId == selectedProjectId }
                    ExposedDropdownMenuBox(
                        expanded = expandedProject,
                        onExpandedChange = { expandedProject = !expandedProject },
                    ) {
                        TextField(
                            readOnly = true,
                            value = project?.name ?: "Select Project",
                            onValueChange = {},
                            label = { Text("Project") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProject) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedProject,
                            onDismissRequest = { expandedProject = false }
                        ) {
                            allProjects.forEach { project ->
                                DropdownMenuItem(
                                    content = { Text("Project ${project.name}") },
                                    onClick = {
                                        selectedProjectId = project.projectId
                                        expandedProject = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                DatePickerField(
                    label = "End Date",
                    selectedDate = selectedDate,
                    onDateSelected = { dateStr, millis ->
                        selectedDate = dateStr
                        endDateLong = millis
                    }
                )
            }
            if (!isNewTask) {
                item {
                    // New ToDo Input
                    OutlinedTextField(
                        value = newTodoText,
                        onValueChange = { newTodoText = it },
                        label = { Text("New ToDo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (newTodoText.isNotBlank() && taskId != null) {
                                coroutineScope.launch {
                                    val todo = ToDo(
                                        title = newTodoText,
                                        isCompleted = false,
                                        createdAt = System.currentTimeMillis(),
                                        taskId = taskId
                                    )
                                    userTaskViewModel.insertToDo(todo)
                                    todoItems.add(todo)
                                    newTodoText = ""
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add ToDo")
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    // ToDo List
                    Column(modifier = Modifier.fillMaxWidth()) {
                        todoItems.forEach { todo ->
                            ToDoListItem(
                                todo = todo,
                                onCheckedChange = { isChecked ->
                                    coroutineScope.launch {
                                        todo.isCompleted = isChecked
                                        userTaskViewModel.updateToDo(todo)
                                    }
                                },
                                onDelete = {
                                    coroutineScope.launch {
                                        userTaskViewModel.deleteToDo(todo)
                                        todoItems.remove(todo)
                                    }
                                }
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            item {
                // Back Button
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                ) {
                    Text("Back", color = Color.White)
                }
            }
            item {
                // Save & Back Button
                Button(
                    onClick = {
                        var projectIdValid = true
                        var selectedStatusValid = true
                        val projectIdList = allProjects.mapNotNull { it.projectId }.distinct()
                        projectIdList.forEach {
                            projectid -> Log.d("debug save", "{$projectid}")
                        }
                        if (isNewTask) {
                            if (!projectIdList.contains(selectedProjectId)) {
                                projectIdValid = false
                            } else if (!statusOptions.contains(selectedStatus)) {
                                selectedStatusValid = false
                            }
                        }

                        coroutineScope.launch {
                            Log.d("debug save","save&back is clicked")

                            try {
                                val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                val defaultEndDate = LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                if (isNewTask) {
                                    Log.d("debug save","creating new task")
                                    if(!projectIdList.contains(selectedProjectId)){
                                        projectIdValid = false
                                    } else if(!statusOptions.contains(selectedStatus)){
                                        selectedStatusValid = false
                                    } else{
                                        projectIdValid = true
                                        val newTask = Task(
                                            title = taskTitle,
                                            description = taskDescription,
                                            email = (if (selectedAssignee == "nobody") null else selectedAssignee).toString(),
                                            status = selectedStatus,
                                            projectId = selectedProjectId ?: 1,
                                            startDate = now,
                                            endDate = endDateLong ?: defaultEndDate,
                                            createdAt = now,
                                            completedAt = null
                                        )
                                        Log.d("debug save","task successfully created: {$newTask}")
                                        userTaskViewModel.insertTask(newTask)
                                        Log.d("debug save","task inserted")
                                    }
                                } else if (task != null) {
                                    if(selectedStatus.equals("Done") && task.completedAt==null){
                                        task.completedAt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                    }
                                    val updatedTask = task.copy(
                                        title = taskTitle,
                                        description = taskDescription,
                                        email = (if (selectedAssignee == "nobody") null else selectedAssignee).toString(),
                                        status = selectedStatus,
                                        endDate = endDateLong ?: task.endDate,
                                        completedAt = task.completedAt
                                    )
                                    userTaskViewModel.updateTask(updatedTask)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Failed to save task: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                        if(projectIdValid && selectedStatusValid){
                            navController.navigate("task")
                        }else if(selectedStatusValid){
                            Toast.makeText(context, "Project id not valid, please select a project}", Toast.LENGTH_LONG).show()
                        } else{
                            Toast.makeText(context, "You must select a status", Toast.LENGTH_LONG).show()
                        }

                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0277BD))
                ) {
                    Text("Save & Back", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ToDoListItem(todo: ToDo, onCheckedChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = todo.title,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String, Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val updatedOnDateSelected by rememberUpdatedState(onDateSelected)
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val dateStr = "${dayOfMonth.toString().padStart(2, '0')}/${
                    (month + 1).toString().padStart(2, '0')
                }/$year"
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0)
                selectedCalendar.set(Calendar.MILLISECOND, 0)
                val millis = selectedCalendar.timeInMillis
                updatedOnDateSelected(dateStr, millis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                datePickerDialog.show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                datePickerDialog.show()
            }
    )
}
