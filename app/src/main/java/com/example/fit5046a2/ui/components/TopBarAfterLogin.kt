package com.example.fit5046a2.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

@Composable
fun TopBarAfterLogin(
    title: String = "Welcome to your workplace!",
    navController: NavController,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(69.dp)
            .background(Color(0xFF78C5E7))
    ) {
        val (leftIcon, centerText, rightIcon) = createRefs()

        IconButton(
            onClick = {},
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFEAD6FF), CircleShape)
                .constrainAs(leftIcon) {
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Text("😊", fontSize = 20.sp)
        }


        Text(
            text = title,
            modifier = Modifier
                .constrainAs(centerText) {
                    start.linkTo(leftIcon.end, margin = 16.dp)
                    end.linkTo(rightIcon.start, margin = 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 14.sp
        )


        val expanded = remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .constrainAs(rightIcon) {
                    end.linkTo(parent.end, margin = 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            IconButton(
                onClick = { expanded.value = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFEAD6FF), CircleShape)
            ) {
                Text("👤", fontSize = 20.sp)
            }

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Profile") },
                    onClick = {
                        expanded.value = false
                        navController.navigate("profile")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        expanded.value = false
                        signOut(navController.context)
                        navController.navigate("login")
                    }
                )
            }
        }
    }
}

fun signOut(context: Context) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("178066863512-8cloahg8c7uln25f7e90ho5n76e7ngdt.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    googleSignInClient.signOut().addOnCompleteListener {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "logged out, see you", Toast.LENGTH_SHORT).show()
    }
}