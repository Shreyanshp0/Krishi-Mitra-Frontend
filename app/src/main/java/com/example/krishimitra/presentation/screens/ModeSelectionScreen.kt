package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.SoilBrown
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.presentation.navigation.KrishiMitraTopBar

@Composable
fun ModeSelectionScreen(
    onUploadMode: () -> Unit,
    onManualMode: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            KrishiMitraTopBar(
                title = "Select Mode",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        containerColor = LightBeige
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimensions.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SECTION_SPACING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "How would you like to provide your soil details?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = "Choose the method that works best for you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoilBrown,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ModeCard(
                    icon = Icons.Default.CloudUpload,
                    title = "📄 Upload Soil Health Card",
                    description = "Upload your SHC document to automatically extract soil data",
                    onClick = onUploadMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ModeCard(
                    icon = Icons.Default.Edit,
                    title = "✍️ Enter Details Manually",
                    description = "Fill in your soil and farming details step by step",
                    onClick = onManualMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ModeCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.LARGE),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DeepGreen,
                modifier = Modifier.size(Dimensions.ICON_SIZE_LARGE)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepGreen,
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = SoilBrown,
                textAlign = TextAlign.Center
            )
        }
    }
}
