package com.example.krishimitra.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.WeatherData
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.ui.theme.*

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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Dimensions.SCREEN_PADDING),
        verticalArrangement = Arrangement.spacedBy(Dimensions.LARGE),
        contentPadding = PaddingValues(top = Dimensions.MEDIUM, bottom = Dimensions.EXTRA_LARGE)
    ) {
        item {
            // Header Section
            Column(modifier = Modifier.padding(top = Dimensions.SMALL)) {
                Text(
                    text = stringResource(R.string.welcome_msg, userName),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(Dimensions.EXTRA_SMALL))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.weather_status, location),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            WeatherCard(weather)
        }

        item {
            Column {
                Text(
                    text = stringResource(R.string.quick_actions),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
                QuickActionsGrid(
                    onRecommend = onNavigateToRecommend,
                    onUpload = onNavigateToUpload,
                    onHistory = onNavigateToHistory,
                    onInsights = onNavigateToInsights
                )
            }
        }

        item {
            AdvisoryCard()
        }
    }
}

@Composable
private fun WeatherCard(weather: WeatherData?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(DeepGreen, LeafGreen)
                    )
                )
                .padding(Dimensions.LARGE)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = weather?.condition ?: stringResource(R.string.sunny_day),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(R.string.farming_time),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = if (weather != null) "${weather.temperature.toInt()}°C" else "32°C",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 44.sp),
                    color = Color.White,
                    fontWeight = FontWeight.Light
                )
            }
            
            Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
            
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            
            Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WeatherInfoItem(Icons.Default.WaterDrop, stringResource(R.string.humidity, weather?.humidity ?: 65))
                    WeatherInfoItem(Icons.Default.Air, stringResource(R.string.wind_speed, weather?.windSpeed ?: 12.5))
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.MEDIUM))

            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_SMALL),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.irrigation_tip),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun WeatherInfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color.White)
    }
}

private data class QuickActionItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun QuickActionsGrid(
    onRecommend: () -> Unit,
    onUpload: () -> Unit,
    onHistory: () -> Unit,
    onInsights: () -> Unit
) {
    val actions = listOf(
        QuickActionItem(stringResource(R.string.get_recommendation), Icons.Default.Spa, LeafGreen, onRecommend),
        QuickActionItem(stringResource(R.string.upload_soil_data), Icons.Default.CloudUpload, DeepGreen, onUpload),
        QuickActionItem(stringResource(R.string.view_history), Icons.Default.History, HistoryBrown, onHistory),
        QuickActionItem(stringResource(R.string.agri_insights), Icons.Default.AutoAwesome, InsightGreen, onInsights)
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

@Composable
private fun ActionCard(item: QuickActionItem, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

    Card(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(),
                onClick = item.onClick
            ),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(item.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(Dimensions.SMALL_MEDIUM))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AdvisoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(Dimensions.MEDIUM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.TipsAndUpdates,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(Dimensions.MEDIUM))
            Column {
                Text(
                    text = stringResource(R.string.daily_tip),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.daily_tip_content),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
