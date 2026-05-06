package com.example.krishimitra

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.krishimitra.presentation.auth.AuthViewModel
import com.example.krishimitra.presentation.navigation.AppNavGraph
import com.example.krishimitra.presentation.viewmodel.CropViewModel
import com.example.krishimitra.presentation.viewmodel.MainViewModel
import com.example.krishimitra.ui.theme.KrishiMitraTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val cropViewModel: CropViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val langCode by mainViewModel.languageCode.collectAsStateWithLifecycle()
            
            // Apply language to configuration
            val locale = Locale(langCode)
            Locale.setDefault(locale)
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            
            val localizedContext = createConfigurationContext(config)
            
            // Wrap the context to preserve Activity reference for Hilt while using localized resources
            val wrappedContext = object : ContextWrapper(this@MainActivity) {
                override fun getResources() = localizedContext.resources
                override fun getAssets() = localizedContext.assets
            }
            
            CompositionLocalProvider(LocalContext provides wrappedContext) {
                KrishiMitraTheme {
                    AppNavGraph(
                        cropViewModel = cropViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}
