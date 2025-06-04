package com.example.fit5046a2.ui.screens.notice

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fit5046a2.data.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun AndroidPreview_Notice_Default_page() {

    NoticePageScreen()
}

@Composable
fun NoticePageScreen(){
    Scaffold (
//        topBar = {TopBarAfterLogin()},
//        bottomBar = {BottomBar()}
    ){ paddingValues ->
        // padding of the scaffold is enforced to be used
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ){
//            Notice_page()
        }
    }
}

data class Notice(
    val positionName: String,
    val message: String,
    val badgeCount: Int,
    val email: String?
)

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun Notice_page(navController: NavController,userViewModel: UserViewModel, modifier: Modifier = Modifier) {


    // Sample data (replace with your actual data source)
    userViewModel.loadCurrentUser()
    userViewModel.loadAllUsersFromFirebase()
    var allUsers = userViewModel.firebaseUsers.collectAsState(initial = emptyList())
    var currentUser = userViewModel.currentUser.value
    println("all users")
    println(allUsers.value.size)
    println("current user")
    println(currentUser.toString())

    val notices =
        allUsers.value
            .filter { !it.email.equals(currentUser?.email)  }
            .map {
                Notice(
                    positionName = "User email: ${it.email}",
                    message = "Name: ${it.first_name} ${it.last_name}",
                    badgeCount = (1..5).random(),
                    email = it.email
                )
            }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(notices) { notice ->
            NoticeItem(notice, onClick = {
                navController.navigate("chat/${notice.email}")
            })
            Spacer(Modifier.height(2.dp))
        }
    }
}

@Composable
fun NoticeItem(notice: Notice, onClick: () -> Unit) {
    Row(
        Modifier
            .width(375.dp)
            .padding(start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(25.dp)) // Use dp for consistency
            .border(1.dp, color = Color.LightGray, RoundedCornerShape(25.dp))
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        BadgedBox(
            badge = {
                if (notice.badgeCount > 0) {
                    Badge(
                        Modifier.size(16.dp),
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Text(
                            notice.badgeCount.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFEADDFF), shape = CircleShape)
                    .border(BorderStroke(1.dp, Color.Gray), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A", // Or a dynamic initial based on notice data
                    color = Color(0xff4f378b),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(Modifier
            .padding(10.dp)
            .fillMaxWidth()) {
            Text(notice.positionName, fontWeight = FontWeight.Bold)
            Text(notice.message)
        }
    }
}