package com.example.krishimitra.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.krishimitra.presentation.screens.HistoryScreen
import com.example.krishimitra.presentation.screens.HomeScreen
import com.example.krishimitra.presentation.screens.InputFormScreen
import com.example.krishimitra.presentation.screens.LoadingScreen
import com.example.krishimitra.presentation.screens.ResultScreen
import com.example.krishimitra.presentation.screens.SplashScreen
import com.example.krishimitra.presentation.screens.UploadScreen
import com.example.krishimitra.presentation.viewmodel.CropUiState
import com.example.krishimitra.presentation.viewmodel.CropViewModel

@Composable
fun AppNavGraph(
    viewModel: CropViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route,
        route = AppRoute.Root.route
    ) {
        composable(AppRoute.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.Home.route) {
            HomeScreen(
                onStartRecommendation = {
                    viewModel.clearResult()
                    navController.navigate(AppRoute.InputForm.route)
                },
                onOpenUpload = { navController.navigate(AppRoute.Upload.route) },
                onOpenHistory = { navController.navigate(AppRoute.History.route) }
            )
        }

        composable(AppRoute.InputForm.route) {
            val formState by viewModel.formState.collectAsState()

            InputFormScreen(
                formState = formState,
                onSoilTypeChange = viewModel::updateSoilType,
                onSeasonChange = viewModel::updateSeason,
                onTemperatureChange = viewModel::updateTemperature,
                onRainfallChange = viewModel::updateRainfall,
                onNitrogenChange = viewModel::updateNitrogen,
                onPhosphorusChange = viewModel::updatePhosphorus,
                onPotassiumChange = viewModel::updatePotassium,
                onSubmit = {
                    val canProceed = viewModel.submitRecommendation()
                    if (canProceed) {
                        navController.navigate(AppRoute.Loading.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Upload.route) {
            UploadScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(AppRoute.InputForm.route) }
            )
        }

        composable(AppRoute.Loading.route) {
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState) {
                if (uiState is CropUiState.Success) {
                    navController.navigate(AppRoute.Result.route) {
                        popUpTo(AppRoute.Loading.route) { inclusive = true }
                    }
                }
            }

            LoadingScreen(
                uiState = uiState,
                onRetry = viewModel::retry,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Result.route) {
            val uiState by viewModel.uiState.collectAsState()

            val successState = uiState as? CropUiState.Success
            ResultScreen(
                result = successState?.data,
                onTryAgain = {
                    navController.navigate(AppRoute.InputForm.route)
                },
                onOpenHistory = { navController.navigate(AppRoute.History.route) },
                onBackHome = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.History.route) {
            val history by viewModel.history.collectAsState()

            HistoryScreen(history = history, onBack = { navController.popBackStack() })
        }
    }
}


