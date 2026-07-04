package com.jaidensiu.quickMaths

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jaidensiu.quickMaths.ui.GameScreen
import com.jaidensiu.quickMaths.ui.StartScreen

@Composable
fun QuickMathsApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoute.Start) {
        composable<AppRoute.Start> {
            StartScreen(onStartGame = { navController.navigate(route = AppRoute.Game) })
        }
        composable<AppRoute.Game> {
            GameScreen(onBackToStart = { navController.popBackStack() })
        }
    }
}
