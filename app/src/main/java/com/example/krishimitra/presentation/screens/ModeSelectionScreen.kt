package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.krishimitra.R
import com.example.krishimitra.ui.theme.*
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
                title = stringResource(R.string.select_mode_title),
                showBackButton = true,
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(Dimensions.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimensions.LARGE),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.how_to_provide),
                        style = MaterialTheme.typography.headlineLarge,
                        color = DeepGreen,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(Dimensions.SMALL))
                    Text(
                        text = stringResource(R.string.choose_method),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                ModeCard(
                    icon = Icons.Default.CloudUpload,
                    title = stringResource(R.string.upload_shc_title),
                    description = stringResource(R.string.upload_shc_desc),
                    onClick = onUploadMode,
                    accentColor = LeafGreen
                )
            }

            item {
                ModeCard(
                    icon = Icons.Default.Edit,
                    title = stringResource(R.string.manual_entry_title),
                    description = stringResource(R.string.manual_entry_desc),
                    onClick = onManualMode,
                    accentColor = DeepGreen
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
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.LARGE),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(Dimensions.ICON_SIZE_MEDIUM)
                )
            }

            Spacer(modifier = Modifier.width(Dimensions.LARGE))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
