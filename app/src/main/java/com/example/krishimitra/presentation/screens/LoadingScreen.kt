package com.example.krishimitra.presentation.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.presentation.viewmodel.CropUiState
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LeafGreen
import com.example.krishimitra.ui.Dimensions
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    uiState: CropUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    when (uiState) {
        CropUiState.Loading, CropUiState.Idle -> LoadingContent()
        is CropUiState.Error -> ErrorContent(
            message = uiState.message,
            isOffline = uiState.isOffline,
            onRetry = onRetry,
            onBack = onBack
        )
        is CropUiState.Success -> LoadingContent()
    }
}

@Composable
private fun LoadingContent() {
    val currentStep = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (currentStep.value < 3) {
            delay(1500)
            currentStep.value += 1
        }
    }

    val steps = listOf(
        Pair("🔬 Analyzing soil...", Icons.Default.WaterDrop),
        Pair("🌡️ Fetching weather...", Icons.Default.Thermostat),
        Pair("💡 Generating recommendation...", Icons.Default.Lightbulb)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.SCREEN_PADDING),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Finding Best Crops for Your Land",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DeepGreen,
            modifier = Modifier.padding(bottom = Dimensions.SECTION_SPACING)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimensions.LARGE)
        ) {
            steps.forEachIndexed { index, (stepText, icon) ->
                StepIndicator(
                    step = index,
                    text = stepText,
                    icon = icon,
                    isActive = index <= currentStep.value,
                    isComplete = index < currentStep.value
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimensions.SECTION_SPACING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
        ) {
            if (currentStep.value < 3) {
                CircularProgressIndicator(
                    color = DeepGreen,
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                "Please wait...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun StepIndicator(
    step: Int,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    isComplete: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.MEDIUM)
            .background(
                color = if (isActive) DeepGreen.copy(alpha = 0.1f) else Color.White,
                shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM)
            )
            .padding(Dimensions.MEDIUM),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isComplete) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = LeafGreen,
                modifier = Modifier.size(Dimensions.ICON_SIZE_MEDIUM)
            )
        } else {
            Column(
                modifier = Modifier
                    .size(Dimensions.ICON_SIZE_MEDIUM)
                    .background(
                        if (isActive) DeepGreen else Color.LightGray,
                        CircleShape
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(Dimensions.ICON_SIZE_SMALL)
                )
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) DeepGreen else Color.Gray
        )
    }
}

@Composable
private fun ShimmerCardPlaceholder() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha = transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .alpha(alpha.value)
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(" ", modifier = Modifier.fillMaxWidth().height(18.dp).background(MaterialTheme.colorScheme.surface))
        Text(" ", modifier = Modifier.fillMaxWidth().height(14.dp).background(MaterialTheme.colorScheme.surface))
        Text(" ", modifier = Modifier.fillMaxWidth(0.4f).height(14.dp).background(MaterialTheme.colorScheme.surface))
    }
}

@Composable
private fun ErrorContent(
    message: String,
    isOffline: Boolean,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Unable to load recommendations", style = MaterialTheme.typography.titleLarge)
        if (isOffline) {
            Text("Offline mode: no cached result available.")
        }
        Text(message)
        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
            Text("Retry")
        }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
