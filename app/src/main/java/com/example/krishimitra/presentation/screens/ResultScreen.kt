package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.domain.model.CropRecommendation
import com.example.krishimitra.domain.model.RecommendationResult
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LeafGreen
import com.example.krishimitra.ui.theme.Amber
import com.example.krishimitra.ui.theme.Red
import com.example.krishimitra.ui.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: RecommendationResult?,
    onTryAgain: () -> Unit,
    onOpenHistory: () -> Unit,
    onBackHome: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recommendations", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepGreen)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (result == null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No data available.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimensions.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimensions.CARD_SPACING)
        ) {
            item {
                Text(
                    text = "🌾 Top Crop Recommendations",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
            }

            item {
                Text(
                    text = "Based on your soil and weather conditions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            if (result.isOffline) {
                item {
                    Surface(
                        color = Amber.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "📡 Offline: Showing cached result",
                            modifier = Modifier.padding(Dimensions.MEDIUM),
                            color = Amber,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            items(result.recommendations.take(3)) { recommendation ->
                RecommendationCard(recommendation)
            }

            item {
                Spacer(modifier = Modifier.height(Dimensions.SECTION_SPACING))
            }

            item {
                Button(
                    onClick = onTryAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.BUTTON_HEIGHT),
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
                ) {
                    Text("🔄 New Recommendation", fontWeight = FontWeight.Bold)
                }
            }

            item {
                OutlinedButton(
                    onClick = onOpenHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.BUTTON_HEIGHT),
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE)
                ) {
                    Text("📋 View History", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(item: CropRecommendation) {
    val (statusLabel, statusColor) = when {
        item.confidence >= 0.8 -> "High Confidence ✅" to LeafGreen
        item.confidence >= 0.6 -> "Medium Confidence ⚠️" to Amber
        else -> "Low Confidence ❌" to Red
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.LARGE),
            verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.cropName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = statusLabel,
                        modifier = Modifier.padding(horizontal = Dimensions.MEDIUM, vertical = Dimensions.SMALL),
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = item.reason,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )

            LinearProgressIndicator(
                progress = { item.confidence.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = statusColor,
                trackColor = statusColor.copy(alpha = 0.1f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            Text(
                text = "Confidence: ${(item.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
