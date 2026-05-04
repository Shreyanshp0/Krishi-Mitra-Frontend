package com.example.krishimitra.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.SoilBrown
import com.example.krishimitra.ui.Dimensions

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val navItems = listOf(
        BottomNavItem(AppRoute.Home.route, "Home", Icons.Default.Home),
        BottomNavItem(AppRoute.Recommend.route, "Recommend", Icons.Default.AutoAwesome),
        BottomNavItem(AppRoute.History.route, "History", Icons.Default.History),
        BottomNavItem(AppRoute.Insights.route, "Insights", Icons.Default.Lightbulb),
        BottomNavItem(AppRoute.Profile.route, "Profile", Icons.Default.AccountCircle)
    )

    NavigationBar(
        modifier = Modifier
            .height(Dimensions.TOP_BAR_HEIGHT)
            .background(Color.White),
        containerColor = Color.White,
        contentColor = DeepGreen
    ) {
        navItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) DeepGreen else SoilBrown
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) DeepGreen else SoilBrown,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepGreen,
                    selectedTextColor = DeepGreen,
                    indicatorColor = DeepGreen.copy(alpha = 0.1f),
                    unselectedIconColor = SoilBrown,
                    unselectedTextColor = SoilBrown
                )
            )
        }
    }
}

