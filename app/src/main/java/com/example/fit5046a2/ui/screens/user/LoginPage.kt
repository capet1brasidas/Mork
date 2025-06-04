package com.example.fit5046a2.ui.screens.user

import android.util.Log.e
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavHostController
import com.example.fit5046a2.data.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
    fun Login_page(
        userViewModel: UserViewModel,
        onLoginSuccess: () -> Unit,
        onSignUp: () -> Unit,
        navController: NavHostController
    ) {

        val context = LocalContext.current
        var loginSuccess = false
        var allUsers = userViewModel.firebaseUsers.collectAsState(initial = emptyList())
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("178066863512-8cloahg8c7uln25f7e90ho5n76e7ngdt.apps.googleusercontent.com")  // 来自 google-services.json
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            println("start auth session")
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { authResult ->
                        if (authResult.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let {
                                val db = Firebase.firestore
                                val userEmail = it.email ?: it.uid
                                val userDocRef = db.collection("users").document(userEmail)
                                userDocRef.get()
                                    .addOnSuccessListener { document ->
                                        if (!document.exists()) {
                                            // 不存在，插入新用户
                                            val userMap = mapOf(
                                                "uid" to it.uid,
                                                "name" to it.displayName,
                                                "email" to it.email
                                            )
                                            userDocRef.set(userMap)
                                            CoroutineScope(Dispatchers.IO).launch {
                                                createSmtpUser(userEmail)
                                            }
                                        } else {
                                            println("User already exists in Firestore: ${document.data}")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        e("Firestore", "Failed to check user existence: ${e.message}")
                                    }
                            }
                            Toast.makeText(context, "Google login succeed", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context, "Google login failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                println(e.toString())
                Toast.makeText(context, "Google login issue: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }



        ConstraintLayout(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            val (
                AvatarBox, PasswordInput, EmailInput, LoginBtn,YourDivider, SignUpBtn,
                MailIconBtn,ForgotPassword, AppNameText, RememberMeRow
            ) = createRefs()


            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0.9f, 0.87f, 1f, 1f), CircleShape)
                    .constrainAs(AvatarBox) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.Black
                )
            }

            var email by remember { mutableStateOf("developer@example.com") }
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("developer@example.com") },
                modifier = Modifier
                    .width(280.dp)
                    .constrainAs(EmailInput) {
                        top.linkTo(AvatarBox.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(250,250,250),
                    unfocusedContainerColor = Color(250,250,250)
                ),
                shape = RoundedCornerShape(6.dp)
            )
            var password by remember { mutableStateOf("password123") }
            var passwordVisible by remember { mutableStateOf(false) }
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("password123") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val iconText = if (passwordVisible) "🙈" else "👁️"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(iconText)
                    }
                },
                modifier = Modifier
                    .width(280.dp)
                    .constrainAs(PasswordInput) {
                        top.linkTo(EmailInput.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(250,250,250),
                    unfocusedContainerColor = Color(250,250,250)
                ),
                shape = RoundedCornerShape(6.dp)
            )

            var rememberMeChecked by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .constrainAs(RememberMeRow) {
                        top.linkTo(PasswordInput.bottom, margin = 4.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMeChecked,
                    onCheckedChange = { rememberMeChecked = it }
                )
                Text("Remember me")
            }

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // 登录成功，读取所有 Firestore 用户
                                Firebase.firestore.collection("users").get()
                                    .addOnSuccessListener { result ->
                                        result.documents.forEach { doc ->
                                            val userData = doc.data
                                            println("User doc: $userData")
                                        }
                                        onLoginSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "failed to read user: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, "login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                          },
                modifier = Modifier
                    .width(200.dp)
                    .height(40.dp)
                    .constrainAs(LoginBtn) {
                        top.linkTo(RememberMeRow.bottom, margin = 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6EB5E1)),
                shape = RoundedCornerShape(50)
            ) {
                Text("Login")
            }

            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier
                    .width(100.dp)
                    .padding(top = 8.dp)
                    .constrainAs(YourDivider) {
                        top.linkTo(LoginBtn.bottom, margin = 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Button(
                onClick = { onSignUp() },
                modifier = Modifier
                    .width(200.dp)
                    .height(40.dp)
                    .constrainAs(SignUpBtn) {
                        top.linkTo(YourDivider.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6EB5E1)),
                shape = RoundedCornerShape(50)
            ) {
                Text("Sign Up")
            }

            // 信封按钮
            IconButton(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                          },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF6EB5E1), CircleShape)
                    .constrainAs(MailIconBtn) {
                        top.linkTo(SignUpBtn.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Icon(Icons.Default.Email, contentDescription = "Mail", tint = Color.White)
            }


            TextButton(
                onClick = { navController.navigate("forget") },
                modifier = Modifier
                    .constrainAs(ForgotPassword) {
                        top.linkTo(MailIconBtn.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                Text("Reset Password?", color = Color(0xFF6EB5E1), fontSize = 12.sp)
            }

            // App 名称
            Text(
                "Gork is brutal but cunning, Mork is cunning but brutal",
                modifier = Modifier.constrainAs(AppNameText) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                style = LocalTextStyle.current.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }

