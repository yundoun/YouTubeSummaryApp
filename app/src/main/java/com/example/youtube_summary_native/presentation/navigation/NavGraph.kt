package com.example.youtube_summary_native.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.youtube_summary_native.core.constants.AppRoutes
import com.example.youtube_summary_native.core.presentation.ui.home.HomeScreen
import com.example.youtube_summary_native.presentation.ui.summary.SummaryScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = AppRoutes.HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppRoutes.HOME_ROUTE) {
            HomeScreen(
                onNavigateToSummary = { videoId ->
                    navController.navigate(AppRoutes.createSummaryRoute(videoId))
                },
                onNavigateToAuth = {
                    // TODO: Implement auth navigation
                }
            )
        }

        composable(
            route = AppRoutes.SUMMARY_ROUTE,
            arguments = listOf(
                navArgument("videoId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val videoId = checkNotNull(backStackEntry.arguments?.getString("videoId"))
            SummaryScreen(
                videoId = videoId,
                onBackPress = {
                    navController.navigateUp()
                }
            )
        }
    }
}