package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LeafGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.Dimensions

@Composable
fun HomeScreen(
    onOpenRecommend: () -> Unit,
    onOpenUpload: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenInsights: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBeige),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SECTION_SPACING)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SECTION_SPACING)
        ) {
            // Welcome
            item {
                Text(
                    text = "Welcome back, Farmer 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            // Weather
            item { WeatherCard() }

            // Quick Actions grid
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                // two-by-two grid using Row
                Column(verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)) {
                        QuickActionCard(
                            title = "Get Recommendation",
                            subtitle = "Personalized crops",
                            onClick = onOpenRecommend,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(Dimensions.MEDIUM))
                        QuickActionCard(
                            title = "Upload Soil Data",
                            subtitle = "Use SHC to auto-fill",
                            onClick = onOpenUpload,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)) {
                        QuickActionCard(
                            title = "View History",
                            subtitle = "Past recommendations",
                            onClick = onOpenHistory,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(Dimensions.MEDIUM))
                        QuickActionCard(
                            title = "Agri Insights",
                            subtitle = "News & prices",
                            onClick = onOpenInsights,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Recent activity (optional)
            item {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
                ) {
                    Column(modifier = Modifier.padding(Dimensions.MEDIUM)) {
                        Text("Last recommendation: Wheat • 78%", color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(Dimensions.SMALL))
                        Text("Last upload: SHC_2024.pdf", color = Color.DarkGray)
                    }
                }
            }

            // Tip / Advisory
            item {
                Card(
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                    colors = CardDefaults.cardColors(containerColor = DeepGreen.copy(alpha = 0.05f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
                ) {
                    Column(modifier = Modifier.padding(Dimensions.MEDIUM)) {
                        Text("Tip: Best crop this season — Maize", fontWeight = FontWeight.Bold, color = DeepGreen)
                        Spacer(modifier = Modifier.height(Dimensions.SMALL))
                        Text("Consider NPK fertilizer in low doses for improved yield.", color = Color.DarkGray)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherCard() {
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
                Text("Sunny Day ☀️", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Perfect time for farming", color = Color.White.copy(alpha = 0.8f))
            }
            Text(
                "32°C",
                modifier = Modifier.align(Alignment.CenterEnd),
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(Dimensions.MEDIUM), verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = DeepGreen)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
