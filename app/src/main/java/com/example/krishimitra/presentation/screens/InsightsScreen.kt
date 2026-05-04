package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.SoilBrown
import com.example.krishimitra.ui.Dimensions
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier
) {
    val tabs = listOf("News", "Prices", "Schemes")
    val selectedTabIndex = remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBeige)
    ) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex.intValue,
            containerColor = Color.White,
            contentColor = DeepGreen,
            indicator = { tabPositions ->
                if (selectedTabIndex.intValue < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.intValue]),
                        color = DeepGreen
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex.intValue == index,
                    onClick = { selectedTabIndex.intValue = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex.intValue == index) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimensions.CARD_SPACING)
        ) {
            when (selectedTabIndex.intValue) {
                0 -> {
                    // News Tab
                    item {
                        Text(
                            "Latest Agricultural News",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    }
                    items(4) { index ->
                        NewsCard(index)
                    }
                }

                1 -> {
                    // Prices Tab
                    item {
                        Text(
                            "Current Market Prices",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    }
                    items(5) { index ->
                        PriceCard(index)
                    }
                }

                2 -> {
                    // Schemes Tab
                    item {
                        Text(
                            "Government Schemes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    }
                    items(3) { index ->
                        SchemeCard(index)
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsCard(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.MEDIUM),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .background(DeepGreen.copy(alpha = 0.1f), RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM))
                    .padding(Dimensions.SMALL),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = DeepGreen,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
            ) {
                Text(
                    "News Headline $index",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
                Text(
                    "Important updates about farming and crop cultivation",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoilBrown,
                    maxLines = 2
                )
                Text(
                    "Today",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun PriceCard(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.SMALL),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = DeepGreen
                    )
                    Text(
                        "Crop Name ${index}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                }

                Text(
                    "₹${(100 + (index * 50))} / kg",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
            }

            Text(
                "Market: Mandi Name • Last updated 2 hours ago",
                style = MaterialTheme.typography.bodySmall,
                color = SoilBrown
            )
        }
    }
}

@Composable
private fun SchemeCard(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimensions.SMALL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = null,
                    tint = DeepGreen
                )
                Text(
                    "Government Scheme ${index}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
            }

            Text(
                "This scheme provides financial assistance and support to farmers engaged in sustainable agriculture and crop cultivation.",
                style = MaterialTheme.typography.bodySmall,
                color = SoilBrown
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Eligibility: Small & Marginal Farmers",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    "Learn More →",
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
