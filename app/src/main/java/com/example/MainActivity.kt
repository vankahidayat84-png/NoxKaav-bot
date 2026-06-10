package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.DarkGrayBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ChatBotScreen
import com.example.ui.screens.SettingScreen
import com.example.ui.screens.UpdateScreen
import com.example.ui.screens.AboutScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkGrayBackground
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screen.Splash) {
                        composable<Screen.Splash> {
                            SplashScreen(
                                onNavigateToDashboard = {
                                    navController.navigate(Screen.Dashboard) {
                                        popUpTo(Screen.Splash) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable<Screen.Dashboard> {
                            DashboardScreen(navController = navController)
                        }
                        composable<Screen.ChatBot> {
                            ChatBotScreen(navController = navController)
                        }
                        composable<Screen.Setting> {
                            SettingScreen(navController = navController)
                        }
                        composable<Screen.Update> {
                            UpdateScreen(navController = navController)
                        }
                        composable<Screen.About> {
                            AboutScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
