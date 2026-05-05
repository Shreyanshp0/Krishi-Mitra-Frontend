package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.domain.model.WeatherData
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LeafGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.Dimensions

@Composable
fun HomeScreen(
    userName: String,
    location: String,
    weather: WeatherData?,
    onNavigateToRecommend: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LightBeige)
            .padding(Dimensions.SCREEN_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SECTION_SPACING)
    ) {
        item {
            // Welcome Section
            Column {
                Text(
                    text = "Welcome back, $userName 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Here's what's happening in $location today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        item {
            // Weather Card
            WeatherCard(weather)
        }

        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                color = DeepGreen,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            // Quick Actions Grid
            QuickActionsGrid(
                onRecommend = onNavigateToRecommend,
                onUpload = onNavigateToUpload,
                onHistory = onNavigateToHistory,
                onInsights = onNavigateToInsights
            )
        }

        item {
            // Advisory Card
            AdvisoryCard()
        }
    }
}

@Composable
private fun QuickActionsGrid(
    onRecommend: () -> Unit,
    onUpload: () -> Unit,
    onHistory: () -> Unit,
    onInsights: () -> Unit
) {
    val actions = listOf(
        QuickActionItem("Get Recommendation", Icons.Default.Spa, LeafGreen, onRecommend),
        QuickActionItem("Upload Soil Data", Icons.Default.CloudUpload, DeepGreen, onUpload),
        QuickActionItem("View History", Icons.Default.History, Color(0xFF8B4513), onHistory),
        QuickActionItem("Agri Insights", Icons.Default.Analytics, Color(0xFF2E8B57), onInsights)
    )

    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)) {
        Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)) {
            ActionCard(actions[0], Modifier.weight(1f))
            ActionCard(actions[1], Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)) {
            ActionCard(actions[2], Modifier.weight(1f))
            ActionCard(actions[3], Modifier.weight(1f))
        }
    }
}

data class QuickActionItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun ActionCard(item: QuickActionItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(
                enabled = true,
                onClickLabel = item.title,
                onClick = item.onClick
            ),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.color,
                modifier = Modifier.size(Dimensions.ICON_SIZE_LARGE)
            )
            Spacer(modifier = Modifier.height(Dimensions.SMALL))
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
private fun WeatherCard(weather: WeatherData?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(DeepGreen, LeafGreen)
                    )
                )
                .padding(Dimensions.LARGE)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    weather?.condition ?: "Sunny Day ☀️",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Perfect time for farming", color = Color.White.copy(alpha = 0.8f))
            }
            Text(
                if (weather != null) "${weather.temperature.toInt()}°C" else "32°C",
                modifier = Modifier.align(Alignment.CenterEnd),
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun AdvisoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
        colors = CardDefaults.cardColors(containerColor = LeafGreen.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(Dimensions.MEDIUM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Spa, contentDescription = null, tint = DeepGreen)
            Spacer(modifier = Modifier.width(Dimensions.MEDIUM))
            Column {
                Text(
                    "Daily Tip",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
                Text(
                    "Rice is best suited for this season in your region.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}
