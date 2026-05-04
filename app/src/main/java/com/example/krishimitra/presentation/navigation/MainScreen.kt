package com.example.krishimitra.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.krishimitra.presentation.screens.HomeScreen
import com.example.krishimitra.presentation.screens.RecommendScreen
import com.example.krishimitra.presentation.screens.InsightsScreen
import com.example.krishimitra.presentation.screens.ProfileScreen
import com.example.krishimitra.presentation.screens.HistoryScreen
import com.example.krishimitra.presentation.viewmodel.CropViewModel

@Composable
internal fun MainScreen(
    navController: NavHostController,
    cropViewModel: CropViewModel,
    onLogout: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppRoute.Home.route
    
    val history by cropViewModel.history.collectAsState()

    val screenTitle = when (currentRoute) {
        AppRoute.Home.route -> "Dashboard"
        AppRoute.Recommend.route -> "Recommendation"
        AppRoute.History.route -> "History"
        AppRoute.Insights.route -> "Insights"
        AppRoute.Profile.route -> "Profile"
        else -> "Krishi Mitra"
    }

    Scaffold(
        topBar = {
            KrishiMitraTopBar(
                title = screenTitle,
                showBackButton = false,
                onBackClick = {}
            )
        },
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(AppRoute.Home.route) { 
                                saveState = true 
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)
        
        when (currentRoute) {
            AppRoute.Home.route -> {
                HomeScreen(
                    onNavigateToRecommend = { navController.navigate(AppRoute.Recommend.route) },
                    onNavigateToUpload = { navController.navigate(AppRoute.Upload.route) },
                    onNavigateToHistory = { navController.navigate(AppRoute.History.route) },
                    onNavigateToInsights = { navController.navigate(AppRoute.Insights.route) },
                    modifier = modifier
                )
            }

            AppRoute.Recommend.route -> {
                RecommendScreen(
                    onStart = {
                        cropViewModel.clearResult()
                        navController.navigate(AppRoute.ModeSelection.route)
                    },
                    modifier = modifier
                )
            }

            AppRoute.History.route -> {
                HistoryScreen(
                    history = history,
                    onBack = { /* No back on bottom nav screens */ },
                    modifier = modifier,
                    showTopBar = false
                )
            }

            AppRoute.Insights.route -> {
                InsightsScreen(modifier = modifier)
            }

            AppRoute.Profile.route -> {
                ProfileScreen(
                    onLogout = onLogout,
                    modifier = modifier
                )
            }
        }
    }
}
