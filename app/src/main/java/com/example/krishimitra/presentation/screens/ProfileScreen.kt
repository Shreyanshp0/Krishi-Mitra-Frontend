package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
import com.example.krishimitra.data.network.api.UserData
import com.example.krishimitra.ui.theme.*
import com.example.krishimitra.ui.Dimensions

@Composable
fun ProfileScreen(
    user: UserData?,
    currentLanguageCode: String,
    onLanguageChange: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguageCode = currentLanguageCode,
            onLanguageSelected = {
                onLanguageChange(it)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.navigationBarsPadding()
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(Dimensions.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimensions.LARGE)
        ) {
            item {
                ProfileHeader(user)
            }

            item {
                SectionTitle(stringResource(R.string.account))
                Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        SettingItem(
                            icon = Icons.Default.Edit,
                            title = stringResource(R.string.edit_profile),
                            description = stringResource(R.string.update_info)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = Dimensions.MEDIUM), color = Color.LightGray.copy(alpha = 0.2f))
                        SettingItem(
                            icon = Icons.Default.Language,
                            title = stringResource(R.string.language),
                            description = getLanguageName(currentLanguageCode),
                            onClick = { showLanguageDialog = true }
                        )
                    }
                }
            }

            item {
                SectionTitle("App & Privacy")
                Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        SettingItem(
                            icon = Icons.Default.Info,
                            title = stringResource(R.string.about_app),
                            description = stringResource(R.string.app_version)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = Dimensions.MEDIUM), color = Color.LightGray.copy(alpha = 0.2f))
                        SettingItem(
                            icon = Icons.Default.Shield,
                            title = "Privacy Policy",
                            description = "How we protect your data"
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.logout),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun ProfileHeader(user: UserData?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.LARGE),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(DeepGreen, LeafGreen)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                val initials = user?.name?.split(" ")?.take(2)?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: "F"
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineLarge.copy(color = Color.White, fontSize = 36.sp)
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.MEDIUM))

            Text(
                text = user?.name ?: stringResource(R.string.farmer),
                style = MaterialTheme.typography.headlineSmall,
                color = DeepGreen,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${user?.district ?: "N/A"}, ${user?.state ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimensions.LARGE))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(label = "Crops", value = "12")
                VerticalDivider(modifier = Modifier.height(24.dp).align(Alignment.CenterVertically), color = Color.LightGray.copy(alpha = 0.5f))
                ProfileStat(label = "Queries", value = "48")
                VerticalDivider(modifier = Modifier.height(24.dp).align(Alignment.CenterVertically), color = Color.LightGray.copy(alpha = 0.5f))
                ProfileStat(label = "Rank", value = "Expert")
            }
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = DeepGreen, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(Dimensions.MEDIUM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DeepGreen.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(Dimensions.MEDIUM))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf(
        Pair("en", "English"),
        Pair("hi", "हिन्दी"),
        Pair("pa", "ਪੰਜਾਬੀ"),
        Pair("te", "తెలుగు")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                languages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (currentLanguageCode == code) DeepGreen else MaterialTheme.colorScheme.onSurface
                        )
                        if (currentLanguageCode == code) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = DeepGreen)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        containerColor = Color.White
    )
}

private fun getLanguageName(code: String): String {
    return when (code) {
        "hi" -> "हिन्दी"
        "pa" -> "ਪੰਜਾਬੀ"
        "te" -> "తెలుగు"
        else -> "English"
    }
}
