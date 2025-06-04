package com.example.fit5046a2.ui.screens.user

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fit5046a2.data.viewmodel.UserViewModel
import com.example.fit5046a2.ui.theme.FIT5046A2Theme


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileScreenCompose(navController: NavController, userViewModel: UserViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    userViewModel.loadCurrentUser()
    val currentUser = userViewModel.currentUser.collectAsState(initial = null)
    println("current user {$currentUser}")
    val user = currentUser.value
    if (user == null) {
        Toast.makeText(context, "Loading user info...", Toast.LENGTH_SHORT).show()
        return
    }
    println("user {$user}")
    val firstName = remember { mutableStateOf(user.first_name ?: "") }
    val lastName = remember { mutableStateOf(user.last_name ?: "") }
    val emailField = remember { mutableStateOf(user.email) }
    val positionOptions = listOf("employee", "manager", "director")
    val expanded = remember { mutableStateOf(false) }
    val selectedPosition = remember { mutableStateOf(user.position ?: "employee") }

    Scaffold(
//        topBar = { TopBarAfterLogin() },
//        bottomBar = { BottomBar() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                Text(text = "Email: ${emailField.value}")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = firstName.value, onValueChange = { firstName.value = it }, label = { Text("First Name") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = lastName.value, onValueChange = { lastName.value = it }, label = { Text("Last Name") })
                Spacer(modifier = Modifier.height(8.dp))

                Text("Position")
                Box {
                    Button(
                        onClick = { expanded.value = true },
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFADD8E6))
                    ) {
                        Text(selectedPosition.value)
                    }
                    DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                        positionOptions.forEach { option ->
                            DropdownMenuItem(onClick = {
                                selectedPosition.value = option
                                expanded.value = false
                            }) {
                                Text(option)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        user.first_name = firstName.value
                        user.last_name = lastName.value
                        user.position = selectedPosition.value
                        userViewModel.updateUser(user)
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFADD8E6))
                ) {
                    Text("Save")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFADD8E6))
                ) {
                    Text("Back")
                }

                Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom

            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    FIT5046A2Theme {
//        ProfileScreenCompose(Modifier.padding(20.dp))
    }
}