package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.data.network.api.UserData
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.Red
import com.example.krishimitra.ui.theme.SoilBrown
import com.example.krishimitra.ui.Dimensions

@Composable
fun ProfileScreen(
    user: UserData?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBeige)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(Dimensions.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SECTION_SPACING)
        ) {
            item {
                ProfileHeader(user)
            }

            item {
                ProfileInfoCard(user)
            }

            item {
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            }

            item {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
            }

            item {
                SettingItem(
                    icon = Icons.Default.Settings,
                    title = "App Settings",
                    description = "Language, notifications, units"
                )
            }

            item {
                SettingItem(
                    icon = Icons.Default.Info,
                    title = "About Krishi Mitra",
                    description = "Version 1.0 • Privacy Policy"
                )
            }

            item {
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            }

            item {
                Text(
                    "Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
            }

            item {
                SettingItem(
                    icon = Icons.Default.Edit,
                    title = "Edit Profile",
                    description = "Update your information"
                )
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.SCREEN_PADDING)
                .padding(bottom = Dimensions.SCREEN_PADDING),
            shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
            colors = ButtonDefaults.buttonColors(containerColor = Red)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.padding(end = Dimensions.SMALL)
            )
            Text("Logout", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileHeader(user: UserData?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.LARGE),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(DeepGreen.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val initials = user?.name?.split(" ")?.take(2)?.mapNotNull { it.firstOrNull() }?.joinToString("") ?: "F"
                Text(
                    initials,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
            }

            Text(
                user?.name ?: "Farmer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepGreen
            )

            Text(
                "${user?.email ?: ""} • ${user?.state ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = SoilBrown,
                modifier = Modifier.padding(horizontal = Dimensions.MEDIUM)
            )

            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Profile")
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(user: UserData?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.LARGE),
            verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
        ) {
            InfoRow(label = "Location", value = "${user?.district ?: "N/A"}, ${user?.state ?: "N/A"}")
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
            InfoRow(label = "Phone", value = user?.phone ?: "N/A")
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
            InfoRow(label = "User ID", value = user?.id?.takeLast(8) ?: "N/A")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = SoilBrown
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = DeepGreen
        )
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.MEDIUM),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DeepGreen,
                modifier = Modifier.size(Dimensions.ICON_SIZE_MEDIUM)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoilBrown
                )
            }
        }
    }
}
