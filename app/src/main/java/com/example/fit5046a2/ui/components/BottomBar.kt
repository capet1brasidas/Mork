package com.example.fit5046a2.ui.components

import android.R.attr.label
import android.R.attr.x
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fit5046a2.data.viewmodel.UserViewModel

@Composable
fun BottomBar(navController: NavController,modifier: Modifier = Modifier) {

    val items = listOf("home", "task",  "analysis", "notice")
    val labels = listOf("Home","Task",  "Analysis", "Notice")
    val icons = listOf("🏠", "📋",  "📊", "🔔")
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route


    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color(0xFFE3F6FD)),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, route ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text(
                        text = icons[index],
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = labels[index],
                    fontSize = 12.sp,
                    color = if (currentRoute == route) Color(0xFF4E8AFF) else Color.Gray,
                    fontWeight = if (currentRoute == route) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}