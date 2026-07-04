package com.jaidensiu.quickMaths

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jaidensiu.quickMaths.ui.CountdownScreen
import com.jaidensiu.quickMaths.ui.GameScreen
import com.jaidensiu.quickMaths.ui.ResultsScreen
import com.jaidensiu.quickMaths.ui.StartScreen
import com.jaidensiu.quickMaths.ui.TOTAL_QUESTIONS

@Composable
fun QuickMathsApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoute.Start) {
        composable<AppRoute.Start> {
            StartScreen(onStartGame = { navController.navigate(route = AppRoute.Countdown) })
        }
        composable<AppRoute.Countdown> {
            CountdownScreen(
                onCountdownFinished = {
                    navController.navigate(route = AppRoute.Game) {
                        popUpTo<AppRoute.Countdown> { inclusive = true }
                    }
                },
            )
        }
        composable<AppRoute.Game> {
            GameScreen(
                onGameFinished = { elapsedTimeMs ->
                    navController.navigate(route = AppRoute.Results(elapsedTimeMs = elapsedTimeMs)) {
                        popUpTo<AppRoute.Game> { inclusive = true }
                    }
                },
            )
        }
        composable<AppRoute.Results> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoute.Results>()
            ResultsScreen(
                elapsedTimeMs = route.elapsedTimeMs,
                totalQuestions = TOTAL_QUESTIONS,
                onBackToStart = { navController.popBackStack() },
            )
        }
    }
}
