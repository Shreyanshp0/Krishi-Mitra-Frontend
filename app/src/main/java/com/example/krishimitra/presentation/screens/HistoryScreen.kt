package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.domain.model.RecommendationHistoryItem
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightGray
import com.example.krishimitra.ui.theme.SoilBrown
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.presentation.navigation.KrishiMitraTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    history: List<RecommendationHistoryItem>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Recent", "Archived")

    Scaffold(
        topBar = {
            if (showTopBar) {
                KrishiMitraTopBar(
                    title = "History",
                    showBackButton = true,
                    onBackClick = onBack
                )
            }
        },
        containerColor = LightBeige,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = DeepGreen,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = DeepGreen
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) DeepGreen else LightGray,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No records found.", color = SoilBrown)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimensions.SCREEN_PADDING),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.CARD_SPACING)
                ) {
                    items(history) { item ->
                        HistoryCard(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(item: RecommendationHistoryItem) {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${item.input.soilType} Soil",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                    Text(
                        text = "${item.input.season} Season",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoilBrown
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatter.format(Date(item.createdAtMillis)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = timeFormatter.format(Date(item.createdAtMillis)),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
            ) {
                item.recommendations.take(3).forEach { crop ->
                    Surface(
                        color = DeepGreen.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimensions.SMALL),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = crop.cropName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepGreen,
                                maxLines = 1
                            )
                            Text(
                                text = "${(crop.confidence * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoilBrown
                            )
                        }
                    }
                }
            }
        }
    }
}
