package com.example.krishimitra.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.krishimitra.presentation.auth.AuthViewModel
import com.example.krishimitra.presentation.auth.LoginScreen
import com.example.krishimitra.presentation.auth.SignupScreen
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
    cropViewModel: CropViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = AppRoute.Login.route,
        route = AppRoute.Root.route
    ) {
        composable(AppRoute.Login.route) {
            val loginForm by authViewModel.loginForm.collectAsState()
            val authUiState by authViewModel.uiState.collectAsState()

            LoginScreen(
                formState = loginForm,
                uiState = authUiState,
                onEmailChange = authViewModel::updateLoginEmail,
                onPasswordChange = authViewModel::updateLoginPassword,
                onRememberChange = authViewModel::updateRememberMe,
                onLoginClick = {
                    authViewModel.login()
                },
                onNavigateSignup = { navController.navigate(AppRoute.Signup.route) },
                onAuthSuccess = {
                    authViewModel.resetUiState()
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
                onErrorShown = authViewModel::consumeError
            )
        }

        composable(AppRoute.Signup.route) {
            val signupForm by authViewModel.signupForm.collectAsState()
            val authUiState by authViewModel.uiState.collectAsState()

            SignupScreen(
                formState = signupForm,
                uiState = authUiState,
                states = authViewModel.states,
                districtOptions = authViewModel.districtsFor(signupForm.state),
                onFirstNameChange = authViewModel::updateFirstName,
                onLastNameChange = authViewModel::updateLastName,
                onEmailChange = authViewModel::updateEmail,
                onPhoneChange = authViewModel::updateSignupPhone,
                onPasswordChange = authViewModel::updateSignupPassword,
                onStateChange = authViewModel::updateState,
                onDistrictChange = authViewModel::updateDistrict,
                onOtpChange = authViewModel::updateOtp,
                onLocationDetected = { lat, lon ->
                    authViewModel.updateLocation(context, lat, lon)
                },
                onSendOtpClick = authViewModel::requestSignupOtp,
                onVerifyOtpClick = authViewModel::verifySignupOtp,
                onNavigateLogin = { navController.popBackStack() },
                onAuthSuccess = {
                    authViewModel.resetUiState()
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
                onErrorShown = authViewModel::consumeError
            )
        }

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
                    cropViewModel.clearResult()
                    navController.navigate(AppRoute.InputForm.route)
                },
                onOpenUpload = {
                    cropViewModel.clearResult()
                    navController.navigate(AppRoute.Upload.route)
                },
                onOpenHistory = { navController.navigate(AppRoute.History.route) },
                onLogout = {
                    authViewModel.resetUiState()
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoute.InputForm.route) {
            val formState by cropViewModel.formState.collectAsState()

            InputFormScreen(
                uiState = formState,
                onSeasonChange = cropViewModel::updateSeason,
                onSoilTypeChange = cropViewModel::updateSoilType,
                onSoilFertilityChange = cropViewModel::updateSoilFertility,
                onWaterAvailabilityChange = cropViewModel::updateWaterAvailability,
                onIrrigationSourceChange = cropViewModel::updateIrrigationSource,
                onPriorityChange = cropViewModel::updatePriority,
                onPreviousCropChange = cropViewModel::updatePreviousCrop,
                onFarmSizeChange = cropViewModel::updateFarmSize,
                onSubmit = {
                    val canProceed = cropViewModel.submitRecommendation()
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
                onNavigateToForm = { navController.navigate(AppRoute.InputForm.route) }
            )
        }

        composable(AppRoute.Loading.route) {
            val uiState by cropViewModel.uiState.collectAsState()

            LaunchedEffect(uiState) {
                if (uiState is CropUiState.Success) {
                    navController.navigate(AppRoute.Result.route) {
                        popUpTo(AppRoute.Loading.route) { inclusive = true }
                    }
                }
            }

            LoadingScreen(
                uiState = uiState,
                onRetry = cropViewModel::retry,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoute.Result.route) {
            val uiState by cropViewModel.uiState.collectAsState()

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
            val history by cropViewModel.history.collectAsState()

            HistoryScreen(history = history, onBack = { navController.popBackStack() })
        }
    }
}
