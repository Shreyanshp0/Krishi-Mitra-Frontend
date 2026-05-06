package com.example.krishimitra.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.RecommendationHistoryItem
import com.example.krishimitra.ui.theme.*
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.presentation.navigation.KrishiMitraTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    history: List<RecommendationHistoryItem>,
    onDelete: (Long) -> Unit,
    onClearAll: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    val tabs = listOf(stringResource(R.string.recent), stringResource(R.string.archived))
    
    var showClearConfirm by remember { mutableStateOf(false) }

    val filteredHistory = remember(history, searchQuery) {
        history.filter {
            it.input.district?.contains(searchQuery, ignoreCase = true) == true ||
            it.input.state?.contains(searchQuery, ignoreCase = true) == true ||
            it.recommendations.any { crop -> crop.cropName.contains(searchQuery, ignoreCase = true) }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(stringResource(R.string.confirm_delete), style = MaterialTheme.typography.titleLarge) },
            text = { Text(stringResource(R.string.delete_all_confirm), style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.clear_all), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                KrishiMitraTopBar(
                    title = stringResource(R.string.history),
                    showBackButton = true,
                    onBackClick = onBack
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header: Tabs and Search
            Surface(
                color = Color.White,
                tonalElevation = 1.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimensions.SCREEN_PADDING, vertical = Dimensions.SMALL),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PrimaryTabRow(
                            selectedTab = selectedTab,
                            tabs = tabs,
                            onTabSelected = { selectedTab = it },
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (history.isNotEmpty()) {
                            IconButton(onClick = { showClearConfirm = true }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                            }
                        }
                    }
                    
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimensions.SCREEN_PADDING)
                            .padding(bottom = Dimensions.MEDIUM),
                        placeholder = { Text("Search districts or crops...", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DeepGreen) },
                        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            if (filteredHistory.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Event,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = DeepGreen.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
                        Text(
                            text = stringResource(R.string.no_records),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimensions.SCREEN_PADDING),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
                ) {
                    items(filteredHistory, key = { it.id }) { item ->
                        HistoryCard(
                            item = item,
                            onDelete = { onDelete(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryTabRow(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = DeepGreen,
        edgePadding = 0.dp,
        modifier = modifier,
        indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = DeepGreen,
                    height = 3.dp
                )
            }
        },
        divider = {}
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == index) DeepGreen else Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
private fun HistoryCard(
    item: RecommendationHistoryItem,
    onDelete: () -> Unit
) {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    
    var expanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.confirm_delete)) },
            text = { Text(stringResource(R.string.delete_record_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DeepGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(Dimensions.SMALL))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${item.input.district}, ${item.input.state}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatter.format(Date(item.createdAtMillis)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                val soilTypeDisplay = getTranslatedValue(item.input.soilType).let { if (it != 0) stringResource(it) else item.input.soilType ?: "" }
                val seasonDisplay = getTranslatedValue(item.input.season).let { if (it != 0) stringResource(it) else item.input.season ?: "" }
                
                AssistChip(
                    onClick = { },
                    label = { Text(soilTypeDisplay, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(labelColor = SoilBrown)
                )
                Spacer(modifier = Modifier.width(Dimensions.SMALL))
                AssistChip(
                    onClick = { },
                    label = { Text(seasonDisplay, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(labelColor = DeepGreen)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimensions.EXTRA_SMALL)) {
                item.recommendations.take(if (expanded) 5 else 2).forEach { crop ->
                    RecommendationItem(crop = crop, isExpanded = expanded)
                }
            }
            
            if (!expanded && item.recommendations.size > 2) {
                Text(
                    text = "+ ${item.recommendations.size - 2} more crops",
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepGreen,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun RecommendationItem(
    crop: com.example.krishimitra.domain.model.CropRecommendation,
    isExpanded: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Dimensions.SMALL)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = crop.cropName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
                Surface(
                    color = DeepGreen.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "${(crop.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = DeepGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = crop.reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

private fun getTranslatedValue(key: String?): Int {
    return when (key?.lowercase()) {
        "kharif" -> R.string.opt_kharif
        "rabi" -> R.string.opt_rabi
        "zaid" -> R.string.opt_zaid
        "annual" -> R.string.opt_annual
        "alluvial" -> R.string.opt_alluvial
        "black" -> R.string.opt_black
        "red" -> R.string.opt_red
        "laterite" -> R.string.opt_laterite
        "arid" -> R.string.opt_arid
        "forest" -> R.string.opt_forest
        "other" -> R.string.opt_other
        "low" -> R.string.opt_low
        "medium" -> R.string.opt_medium
        "high" -> R.string.opt_high
        else -> 0
    }
}
