package com.example.fit5046a2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fit5046a2.data.viewmodel.UserViewModel
import com.example.fit5046a2.ui.components.BottomBar
import com.example.fit5046a2.ui.components.TopBarAfterLogin
import com.example.fit5046a2.ui.navigation.MainNavigation
import androidx.compose.runtime.getValue
import com.example.fit5046a2.ui.components.TopBarBeforeLogin

class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val noTopBottomBarRoutes = listOf("login", "signup","forget")
        enableEdgeToEdge()
        setContent {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBars = currentRoute !in noTopBottomBarRoutes

                Scaffold(
                    topBar = { if (showBars) TopBarAfterLogin(navController = navController) else TopBarBeforeLogin() },
                    bottomBar = { if (showBars) BottomBar(navController) }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        MainNavigation(navController, userViewModel)
                    }
                }
            }
        }
    }


