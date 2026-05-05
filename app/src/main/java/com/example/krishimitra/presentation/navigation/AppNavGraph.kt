package com.example.krishimitra.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.krishimitra.presentation.auth.AuthViewModel
import com.example.krishimitra.presentation.auth.LoginScreen
import com.example.krishimitra.presentation.auth.SignupScreen
import com.example.krishimitra.presentation.screens.InputFormScreen
import com.example.krishimitra.presentation.screens.LoadingScreen
import com.example.krishimitra.presentation.screens.ModeSelectionScreen
import com.example.krishimitra.presentation.screens.ResultScreen
import com.example.krishimitra.presentation.screens.SplashScreen
import com.example.krishimitra.presentation.screens.UploadScreen
import com.example.krishimitra.presentation.viewmodel.CropUiState
import com.example.krishimitra.presentation.viewmodel.CropViewModel
import com.example.krishimitra.presentation.viewmodel.SHCUploadUiState
import com.example.krishimitra.presentation.viewmodel.SHCUploadViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AppNavGraph(
    cropViewModel: CropViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val userProfile by authViewModel.userProfile.collectAsStateWithLifecycle()
    val token by authViewModel.token.collectAsStateWithLifecycle(initialValue = null)
    val rememberMe by authViewModel.rememberMe.collectAsStateWithLifecycle(initialValue = false)

    // Sync user location to cropViewModel
    LaunchedEffect(userProfile) {
        userProfile?.let {
            cropViewModel.setLocation(it.state, it.district)
        }
    }

    // Handle session expiration (token becomes null)
    LaunchedEffect(token) {
        if (token == null) {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != null && 
                currentRoute != AppRoute.Login.route && 
                currentRoute != AppRoute.Signup.route && 
                currentRoute != AppRoute.Splash.route) {
                navController.navigate(AppRoute.Login.route) {
                    popUpTo(AppRoute.Root.route) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route,
        route = AppRoute.Root.route
    ) {
        // Auth Screens
        composable(AppRoute.Login.route) {
            val loginViewModel: AuthViewModel = hiltViewModel()
            val loginForm by loginViewModel.loginForm.collectAsStateWithLifecycle()
            val authUiState by loginViewModel.uiState.collectAsStateWithLifecycle()

            LoginScreen(
                formState = loginForm,
                uiState = authUiState,
                onEmailChange = loginViewModel::updateLoginEmail,
                onPasswordChange = loginViewModel::updateLoginPassword,
                onRememberChange = loginViewModel::updateRememberMe,
                onLoginClick = {
                    loginViewModel.login()
                },
                onNavigateSignup = { 
                    navController.navigate(AppRoute.Signup.route) {
                        launchSingleTop = true
                    }
                },
                onAuthSuccess = {
                    loginViewModel.resetUiState()
                    loginViewModel.fetchUserProfile()
                    navController.navigate(AppRoute.Splash.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                },
                onErrorShown = loginViewModel::consumeError
            )
        }

        composable(AppRoute.Signup.route) {
            val signupViewModel: AuthViewModel = hiltViewModel()
            val signupForm by signupViewModel.signupForm.collectAsStateWithLifecycle()
            val authUiState by signupViewModel.uiState.collectAsStateWithLifecycle()
            val allStates by signupViewModel.allStates.collectAsStateWithLifecycle()
            val allDistricts by signupViewModel.districts.collectAsStateWithLifecycle()

            SignupScreen(
                formState = signupForm,
                uiState = authUiState,
                states = allStates,
                districtOptions = allDistricts,
                onFirstNameChange = signupViewModel::updateFirstName,
                onLastNameChange = signupViewModel::updateLastName,
                onEmailChange = signupViewModel::updateEmail,
                onPhoneChange = signupViewModel::updateSignupPhone,
                onPasswordChange = signupViewModel::updateSignupPassword,
                onStateChange = signupViewModel::updateState,
                onDistrictChange = signupViewModel::updateDistrict,
                onOtpChange = signupViewModel::updateOtp,
                onLocationDetected = { lat, lon ->
                    signupViewModel.updateLocation(context, lat, lon)
                },
                onSendOtpClick = signupViewModel::requestSignupOtp,
                onVerifyOtpClick = signupViewModel::verifySignupOtp,
                onNavigateLogin = { navController.popBackStack() },
                onAuthSuccess = {
                    signupViewModel.resetUiState()
                    signupViewModel.fetchUserProfile()
                    navController.navigate(AppRoute.Splash.route) {
                        popUpTo(AppRoute.Signup.route) { inclusive = true }
                    }
                },
                onErrorShown = signupViewModel::consumeError
            )
        }

        composable(AppRoute.Splash.route) {
            SplashScreen(
                onTimeout = {
                    val destination = if (token != null && rememberMe) {
                        AppRoute.Main.route
                    } else {
                        AppRoute.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(AppRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Navigation with Bottom Nav (nested shell)
        navigation(
            route = AppRoute.Main.route,
            startDestination = AppRoute.Home.route
        ) {
            val mainScreenContent: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit = {
                MainScreen(
                    navController = navController,
                    cropViewModel = cropViewModel,
                    authViewModel = authViewModel,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(AppRoute.Login.route) {
                            popUpTo(AppRoute.Main.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoute.Home.route, content = mainScreenContent)
            composable(AppRoute.Recommend.route, content = mainScreenContent)
            composable(AppRoute.History.route, content = mainScreenContent)
            composable(AppRoute.Insights.route, content = mainScreenContent)
            composable(AppRoute.Profile.route, content = mainScreenContent)
        }

        // Mode Selection
        composable(AppRoute.ModeSelection.route) {
            ModeSelectionScreen(
                onUploadMode = {
                    cropViewModel.clearResult()
                    navController.navigate(AppRoute.Upload.route) {
                        launchSingleTop = true
                    }
                },
                onManualMode = {
                    cropViewModel.clearResult()
                    navController.navigate(AppRoute.InputForm.route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Input Flows
        composable(AppRoute.InputForm.route) {
            val formState by cropViewModel.formState.collectAsStateWithLifecycle()
            val cropUiState by cropViewModel.uiState.collectAsStateWithLifecycle()

            InputFormScreen(
                formState = formState,
                uiState = cropUiState,
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
                        navController.navigate(AppRoute.Loading.route) {
                            launchSingleTop = true
                        }
                    }
                },
                onBack = { navController.popBackStack() },
                onConsumeError = cropViewModel::clearResult
            )
        }

        composable(AppRoute.Upload.route) {
            val shcViewModel: SHCUploadViewModel = hiltViewModel()
            val shcUiState by shcViewModel.uiState.collectAsStateWithLifecycle()
            
            // Listen for SHC Success to trigger recommendation
            LaunchedEffect(shcUiState) {
                if (shcUiState is SHCUploadUiState.Success) {
                    val data = (shcUiState as SHCUploadUiState.Success).data
                    val soilDataMap = mapOf(
                        "nitrogen" to data.nitrogen,
                        "phosphorus" to data.phosphorus,
                        "potassium" to data.potassium,
                        "ph" to data.ph
                    )
                    val canProceed = cropViewModel.submitShcRecommendation(soilDataMap)
                    if (canProceed) {
                        navController.navigate(AppRoute.Loading.route) {
                            launchSingleTop = true
                        }
                    }
                }
            }

            UploadScreen(
                onBack = { navController.popBackStack() },
                onNavigateToForm = { 
                    navController.navigate(AppRoute.InputForm.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Recommendation Flow
        composable(AppRoute.Loading.route) {
            val uiState by cropViewModel.uiState.collectAsStateWithLifecycle()

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
            val uiState by cropViewModel.uiState.collectAsStateWithLifecycle()
            val successState = uiState as? CropUiState.Success

            ResultScreen(
                result = successState?.data,
                onTryAgain = {
                    cropViewModel.clearResult()
                    navController.navigate(AppRoute.ModeSelection.route) {
                        popUpTo(AppRoute.Result.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onOpenHistory = {
                    navController.navigate(AppRoute.History.route) {
                        popUpTo(AppRoute.Result.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackHome = {
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Result.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
