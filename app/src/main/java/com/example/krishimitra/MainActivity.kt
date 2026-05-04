package com.example.krishimitra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.krishimitra.presentation.auth.AuthViewModel
import com.example.krishimitra.presentation.navigation.AppNavGraph
import com.example.krishimitra.presentation.viewmodel.CropViewModel
import com.example.krishimitra.ui.theme.KrishiMitraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val cropViewModel: CropViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KrishiMitraTheme {
                AppNavGraph(
                    cropViewModel = cropViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}
