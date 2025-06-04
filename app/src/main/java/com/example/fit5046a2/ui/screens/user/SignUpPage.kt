package com.example.fit5046a2.ui.screens.user

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.fit5046a2.data.entity.User
import com.example.fit5046a2.data.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpPage(navController: NavController,userViewModel: UserViewModel, onSignUpSuccess: () -> Unit) {
    val users by userViewModel.firebaseUsers.collectAsState(initial = emptyList())
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("employee") }
    var preferredName by remember { mutableStateOf("") }

    val roleOptions = listOf("employee", "manager", "director")
    var userAlreadyExist = false
    val isFirstNameValid = firstName.matches(Regex("^[A-Za-z]+\$"))
    val isLastNameValid = lastName.matches(Regex("^[A-Za-z]+\$"))
    val isEmailValid = email.contains("@")
    val isPasswordValid = password.length >= 8

    ConstraintLayout(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        val ( fNameInput, lNameInput,pNameInput, emailInput, passwordInput, dobInput, roleInput, signUpBtn, cancelBtn) = createRefs()

        fun textFieldStyle() = Modifier
            .width(280.dp)

        Column(modifier = textFieldStyle().constrainAs(fNameInput) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                placeholder = { Text("Please enter your first name") },
                label = { Text("First Name *") },
                trailingIcon = {
                    if (isFirstNameValid) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(250, 250, 250),
                    unfocusedContainerColor = if (isFirstNameValid) Color(0xFFE8F5E9) else Color(250, 250, 250),
                    focusedIndicatorColor = if (isFirstNameValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (isFirstNameValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(6.dp)
            )
            if (!isFirstNameValid && firstName.isNotEmpty()) {
                Text("Please enter valid first name", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, top = 2.dp))
            }
        }

        Column(modifier = textFieldStyle().constrainAs(lNameInput) {
            top.linkTo(fNameInput.bottom, margin = 16.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            TextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name *") },
                trailingIcon = {
                    if (isLastNameValid) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(250, 250, 250),
                    unfocusedContainerColor = if (isLastNameValid) Color(0xFFE8F5E9) else Color(250, 250, 250),
                    focusedIndicatorColor = if (isLastNameValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (isLastNameValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(6.dp)
            )
            if (!isLastNameValid && lastName.isNotEmpty()) {
                Text("Please enter valid first name", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, top = 2.dp))
            }
        }


        TextField(
            value = preferredName,
            onValueChange = { preferredName = it },
            placeholder = { Text("Please enter your last name.") },
            label = { Text("Preferred name(optional)") },
            modifier = textFieldStyle().constrainAs(pNameInput) {
                top.linkTo(lNameInput.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(250,250,250),
                unfocusedContainerColor = Color(250,250,250)
            ),
            shape = RoundedCornerShape(6.dp)
        )

        Column(modifier = textFieldStyle().constrainAs(emailInput) {
            top.linkTo(pNameInput.bottom, margin = 16.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email *") },
                trailingIcon = {
                    if (isEmailValid) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(250, 250, 250),
                    unfocusedContainerColor = if (isEmailValid) Color(0xFFE8F5E9) else Color(250, 250, 250),
                    focusedIndicatorColor = if (isEmailValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (isEmailValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(6.dp)
            )
            if (!isEmailValid && email.isNotEmpty()) {
                Text("Please enter a valid email", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp, top = 2.dp))
            }
        }

        Column(modifier = textFieldStyle().constrainAs(passwordInput) {
            top.linkTo(emailInput.bottom, margin = 16.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password *") },
                trailingIcon = {
                    if (isPasswordValid) Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4CAF50))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(250,250,250),
                    unfocusedContainerColor = if (isPasswordValid) Color(0xFFE8F5E9) else Color(250,250,250),
                    focusedIndicatorColor = if (isPasswordValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (isPasswordValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(6.dp)
            )
            if (!isPasswordValid && password.isNotEmpty()) {
                Text(
                    "Password must be at least 8 characters",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                )
            }
        }

        TextField(
            value = dob,
            onValueChange = {},
            placeholder = { Text("Date of Birth") },
            enabled = false,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Outlined.DateRange, contentDescription = "Pick date")
                }
            },
            modifier = textFieldStyle().constrainAs(dobInput) {
                top.linkTo(passwordInput.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(250,250,250),
                unfocusedContainerColor = Color(250,250,250)
            ),
            shape = RoundedCornerShape(6.dp)
        )

        if (showDatePicker) {
            val calendar = Calendar.getInstance()
            val dialog = DatePickerDialog(
                context,
                { _, year: Int, month: Int, day: Int ->
                    dob = "$day/${month + 1}/$year"
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            dialog.setOnCancelListener {
                showDatePicker = false
            }

            dialog.show()
        }



        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .constrainAs(roleInput) {
                    top.linkTo(dobInput.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .width(280.dp)
        ) {
            TextField(
                readOnly = true,
                value = selectedRole,
                onValueChange = {},
                label = { Text("your position") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                leadingIcon = {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Position", tint = Color(0xFF8B5CF6))
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFCAF0F8),
                    unfocusedContainerColor = Color(0xFFCAF0F8)
                ),
                shape = RoundedCornerShape(6.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                roleOptions.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {

                users.forEach { user->
                    if(user.email.equals(email) ){
                        userAlreadyExist = true
                    }
                }
                if(userAlreadyExist){
                    Toast.makeText(context, "User Already exists, please use another email", Toast.LENGTH_SHORT).show()
                } else if(!isFirstNameValid){
                    Toast.makeText(context, "Invalid first name", Toast.LENGTH_SHORT).show()
                } else if(!isLastNameValid){
                    Toast.makeText(context, "Invalid last name", Toast.LENGTH_SHORT).show()
                } else if(!isEmailValid){
                    Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
                } else if(!isPasswordValid){
                    Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show()
                } else if(dob.isEmpty() ){
                    Toast.makeText(context, "dob can not be empty", Toast.LENGTH_SHORT).show()
                } else{
                    var dobMillis = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .parse(dob)?.time ?: System.currentTimeMillis()
                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser = FirebaseAuth.getInstance().currentUser
                                val uid = firebaseUser?.uid ?: return@addOnCompleteListener

                                val user = User(
                                    first_name = firstName,
                                    last_name = lastName,
                                    email = email,
                                    password = password,
                                    position = selectedRole,
                                    DOB = dobMillis,
                                    image = null,
                                    joinDate = System.currentTimeMillis()
                                )

                                userViewModel.insertUser(user)
                                onSignUpSuccess()
                            } else {
                                Toast.makeText(context, "sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }


                    CoroutineScope(Dispatchers.IO).launch {
                        createSmtpUser(email)
                    }
                }
            },
            modifier = Modifier
                .width(200.dp)
                .height(40.dp)
                .constrainAs(signUpBtn) {
                    top.linkTo(roleInput.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6EB5E1)),
            shape = RoundedCornerShape(50)
        ) {
            Text("Sign up")
        }

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .width(200.dp)
                .height(40.dp)
                .constrainAs(cancelBtn) {
                    top.linkTo(signUpBtn.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4E0EA)),
            shape = RoundedCornerShape(50)
        ) {
            Text("Cancel", color = Color.Gray)
        }
    }
}

fun CoroutineScope.createSmtpUser(email: String) {

    val username = "a${email.split("@")[0]}"
    val password = "1234"
    println("mail username: $username")

    val requestBody = JSONObject().apply {
        put("username", username)
        put("password", password)
    }

    val url = "http://4.147.153.183:8080/create_user"

    val client = OkHttpClient()
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val body = RequestBody.create(mediaType, requestBody.toString())

    val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

    try {
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            println("SMTP user creation failed: ${response.code}")
        } else {
            println("SMTP user created successfully.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
