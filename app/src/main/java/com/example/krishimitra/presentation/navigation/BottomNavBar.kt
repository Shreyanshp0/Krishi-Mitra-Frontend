package com.example.krishimitra.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightGreen

data class BottomNavItem(
    val route: String,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val navItems = listOf(
        BottomNavItem(AppRoute.Home.route, R.string.home, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(AppRoute.Recommend.route, R.string.suggest, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
        BottomNavItem(AppRoute.History.route, R.string.history, Icons.Filled.History, Icons.Outlined.History),
        BottomNavItem(AppRoute.Insights.route, R.string.insights, Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb),
        BottomNavItem(AppRoute.Profile.route, R.string.profile, Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.NAV_BAR_HEIGHT),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        navItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.labelResId),
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelResId),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepGreen,
                    selectedTextColor = DeepGreen,
                    indicatorColor = LightGreen.copy(alpha = 0.5f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
