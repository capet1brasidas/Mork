package com.example.fit5046a2.ui.navigation

import android.os.Build

import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fit5046a2.data.viewmodel.ColleagueChatViewModel
import com.example.fit5046a2.data.viewmodel.UserViewModel
import com.example.fit5046a2.ui.screens.analysis_ai.AnalysisPage
import com.example.fit5046a2.ui.screens.analysis_ai.AIChatPage
import com.example.fit5046a2.ui.screens.notice.Chat_page
import com.example.fit5046a2.ui.screens.user.ForgetPasswordPage
import com.example.fit5046a2.ui.screens.home.Home_page
import com.example.fit5046a2.ui.screens.user.Login_page
import com.example.fit5046a2.ui.screens.notice.Notice_page
import com.example.fit5046a2.ui.screens.user.ProfileScreenCompose
import com.example.fit5046a2.ui.screens.user.SignUpPage
import com.example.fit5046a2.ui.screens.task.TaskDetail_page
import com.example.fit5046a2.ui.screens.task.Task_Board

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation(navController: NavHostController, userViewModel: UserViewModel) {

    userViewModel.loadCurrentUser()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            Login_page(
                userViewModel,
                onLoginSuccess = { navController.navigate("home") },
                onSignUp = { navController.navigate("signup") },
                navController
            )
        }

        composable("signup") {
            SignUpPage(navController, userViewModel,
                onSignUpSuccess = { navController.navigate("login") }
            )
        }

        composable("forget") {
            ForgetPasswordPage(navController)
        }

        composable("task") { Task_Board(navController) }
        composable("home") { Home_page(navController) }
        composable("notice") { Notice_page(navController, userViewModel) }
        composable("analysis") { AnalysisPage(navController) }

        composable("task_detail/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            TaskDetail_page(taskId = taskId, navController = navController)
        }

        composable("profile") { ProfileScreenCompose(navController, userViewModel) }
        composable("chat/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val colleagueChatViewModel: ColleagueChatViewModel = viewModel(
                viewModelStoreOwner = backStackEntry
            )
            Chat_page(navController, userViewModel, email, colleagueChatViewModel)
        }
        
        // AI Chat route for analysis
        composable("chat_ai") {
            AIChatPage(navController = navController)
        }
    }
}
