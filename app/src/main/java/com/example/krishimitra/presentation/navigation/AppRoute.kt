package com.example.krishimitra.presentation.navigation

sealed class AppRoute(val route: String) {
    data object Root : AppRoute("root")
    data object Login : AppRoute("login")
    data object Signup : AppRoute("signup")
    data object Splash : AppRoute("splash")
    
    // Main Navigation (Bottom Nav)
    data object Home : AppRoute("home")
    data object Recommend : AppRoute("recommend")
    data object History : AppRoute("history")
    data object Insights : AppRoute("insights")
    data object Profile : AppRoute("profile")
    
    // Input Flows
    data object ModeSelection : AppRoute("mode_selection")
    data object InputForm : AppRoute("input_form")
    data object Upload : AppRoute("upload")
    
    // Recommendation Flow
    data object Loading : AppRoute("loading")
    data object Result : AppRoute("result")
}

