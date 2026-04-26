package com.example.krishimitra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.krishimitra.di.AppContainer
import com.example.krishimitra.presentation.navigation.AppNavGraph
import com.example.krishimitra.ui.theme.KrishiMitraTheme
import com.example.krishimitra.presentation.viewmodel.CropViewModel
import com.example.krishimitra.presentation.viewmodel.CropViewModelFactory

class MainActivity : ComponentActivity() {

    private val appContainer by lazy { AppContainer(applicationContext) }

    private val cropViewModel: CropViewModel by viewModels {
        CropViewModelFactory(
            getCropRecommendationUseCase = appContainer.getCropRecommendationUseCase,
            getHistoryUseCase = appContainer.getHistoryUseCase,
            retryRecommendationUseCase = appContainer.retryRecommendationUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KrishiMitraTheme {
                AppNavGraph(viewModel = cropViewModel)
            }
        }
    }
}
