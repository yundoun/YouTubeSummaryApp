package com.example.youtube_summary_native.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.youtube_summary_native.core.constants.AppRoutes
import com.example.youtube_summary_native.presentation.ui.auth.AuthScreen
import com.example.youtube_summary_native.presentation.ui.home.HomeScreen
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
                    navController.navigate(AppRoutes.AUTH_ROUTE)
                }
            )
        }

        composable(AppRoutes.AUTH_ROUTE) {
            AuthScreen(
                onDismissRequest = {
                    navController.navigateUp()
                },
                onLoginSuccess = {
                    // 로그인 성공 시 이전 화면으로 돌아가고 백스택에서 Auth 화면 제거
                    navController.popBackStack()
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