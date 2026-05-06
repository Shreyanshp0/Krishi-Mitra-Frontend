package com.example.krishimitra.presentation.navigation

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.krishimitra.R
import com.example.krishimitra.presentation.screens.HomeScreen
import com.example.krishimitra.presentation.screens.RecommendScreen
import com.example.krishimitra.presentation.screens.ChatScreen
import com.example.krishimitra.presentation.screens.ProfileScreen
import com.example.krishimitra.presentation.screens.HistoryScreen
import com.example.krishimitra.presentation.viewmodel.CropViewModel
import com.example.krishimitra.presentation.auth.AuthViewModel
import com.example.krishimitra.presentation.viewmodel.MainViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun MainScreen(
    navController: NavHostController,
    cropViewModel: CropViewModel,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppRoute.Home.route
    
    val history by cropViewModel.history.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    val weather by authViewModel.weatherState.collectAsState()
    val langCode by mainViewModel.languageCode.collectAsState()

    val screenTitle = when (currentRoute) {
        AppRoute.Home.route -> stringResource(R.string.home)
        AppRoute.Recommend.route -> stringResource(R.string.suggest)
        AppRoute.History.route -> stringResource(R.string.history)
        AppRoute.Insights.route -> stringResource(R.string.insights)
        AppRoute.Profile.route -> stringResource(R.string.profile)
        else -> stringResource(R.string.app_name)
    }

    fun navigateToTopLevel(route: String) {
        if (currentRoute != route) {
            navController.navigate(route) {
                // Standard navigation pattern for bottom nav
                // Pop up to the "Home" destination to avoid building up a large stack
                popUpTo(AppRoute.Home.route) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
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
                onNavigate = ::navigateToTopLevel
            )
        }
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)
        
        when (currentRoute) {
            AppRoute.Home.route -> {
                HomeScreen(
                    userName = userProfile?.name ?: stringResource(R.string.farmer),
                    location = userProfile?.district ?: stringResource(R.string.your_farm),
                    weather = weather,
                    onNavigateToRecommend = { navigateToTopLevel(AppRoute.Recommend.route) },
                    onNavigateToUpload = { navController.navigate(AppRoute.Upload.route) { launchSingleTop = true } },
                    onNavigateToHistory = { navigateToTopLevel(AppRoute.History.route) },
                    onNavigateToInsights = { navigateToTopLevel(AppRoute.Insights.route) },
                    modifier = modifier
                )
            }

            AppRoute.Recommend.route -> {
                RecommendScreen(
                    onStart = {
                        cropViewModel.clearResult()
                        navController.navigate(AppRoute.ModeSelection.route) {
                            launchSingleTop = true
                        }
                    },
                    modifier = modifier
                )
            }

            AppRoute.History.route -> {
                HistoryScreen(
                    history = history,
                    onDelete = cropViewModel::deleteHistory,
                    onClearAll = cropViewModel::clearAllHistory,
                    onBack = { /* No back on bottom nav screens */ },
                    modifier = modifier,
                    showTopBar = false
                )
            }

            AppRoute.Insights.route -> {
                ChatScreen(
                    userState = userProfile?.state ?: "",
                    userDistrict = userProfile?.district ?: "",
                    modifier = modifier
                )
            }

            AppRoute.Profile.route -> {
                ProfileScreen(
                    user = userProfile,
                    currentLanguageCode = langCode,
                    onLanguageChange = mainViewModel::updateLanguage,
                    onLogout = onLogout,
                    modifier = modifier
                )
            }
        }
    }
}
