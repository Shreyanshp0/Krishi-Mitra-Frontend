package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.CropRecommendation
import com.example.krishimitra.domain.model.RecommendationResult
import com.example.krishimitra.ui.theme.*
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.presentation.navigation.KrishiMitraTopBar

@Composable
fun ResultScreen(
    result: RecommendationResult?,
    onTryAgain: () -> Unit,
    onOpenHistory: () -> Unit,
    onBackHome: () -> Unit
) {
    Scaffold(
        topBar = {
            KrishiMitraTopBar(
                title = stringResource(R.string.recommendations_title),
                showBackButton = true,
                onBackClick = onBackHome
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (result == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_data), style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(Dimensions.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimensions.LARGE)
        ) {
            item {
                Column {
                    Text(
                        text = stringResource(R.string.top_recommendations),
                        style = MaterialTheme.typography.headlineLarge,
                        color = DeepGreen
                    )
                    Text(
                        text = stringResource(R.string.based_on_conditions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (result.isOffline) {
                item {
                    Surface(
                        color = Amber.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Amber.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(Dimensions.MEDIUM),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Amber, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(Dimensions.SMALL))
                            Text(
                                stringResource(R.string.offline_cached),
                                color = Amber,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            items(result.recommendations.take(3)) { recommendation ->
                RecommendationCard(recommendation)
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
                ) {
                    Button(
                        onClick = onTryAgain,
                        modifier = Modifier.fillMaxWidth().height(Dimensions.BUTTON_HEIGHT),
                        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
                    ) {
                        Text(stringResource(R.string.new_recommendation), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.fillMaxWidth().height(Dimensions.BUTTON_HEIGHT),
                        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DeepGreen)
                    ) {
                        Text(stringResource(R.string.view_history_btn), style = MaterialTheme.typography.titleMedium, color = DeepGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(item: CropRecommendation) {
    val (statusLabel, statusColor) = when {
        item.confidence >= 0.8 -> stringResource(R.string.high_confidence) to LeafGreen
        item.confidence >= 0.6 -> stringResource(R.string.medium_confidence) to Amber
        else -> stringResource(R.string.low_confidence) to Red
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = statusLabel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = item.reason,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.confidence_label, (item.confidence * 100).toInt()),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LinearProgressIndicator(
                    progress = { item.confidence.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.1f)
                )
            }
        }
    }
}
