package com.example.krishimitra.presentation.navigation

sealed class AppRoute(val route: String) {
    data object Root : AppRoute("root")
    data object Login : AppRoute("login")
    data object Signup : AppRoute("signup")
    data object Splash : AppRoute("splash")
    data object Home : AppRoute("home")
    data object InputForm : AppRoute("input_form")
    data object Upload : AppRoute("upload")
    data object Loading : AppRoute("loading")
    data object Result : AppRoute("result")
    data object History : AppRoute("history")
}

