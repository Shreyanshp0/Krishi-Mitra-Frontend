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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.krishimitra.presentation.viewmodel.CropUiState

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Getting best crops for your land...", style = MaterialTheme.typography.titleLarge)
        repeat(3) {
            ShimmerCardPlaceholder()
        }
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

