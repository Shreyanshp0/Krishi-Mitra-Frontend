package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.LeafGreen

@Composable
fun RecommendScreen(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBeige)
            .padding(Dimensions.SCREEN_PADDING),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Spa,
            contentDescription = null,
            tint = DeepGreen,
            modifier = Modifier.size(100.dp)
        )
        
        Spacer(modifier = Modifier.height(Dimensions.LARGE))
        
        Text(
            text = "Get Crop Recommendations",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DeepGreen,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(Dimensions.MEDIUM))
        
        Text(
            text = "Fill in your soil details and receive personalized crop recommendations based on your location and conditions.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimensions.MEDIUM)
        )
        
        Spacer(modifier = Modifier.height(Dimensions.SECTION_SPACING))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.BUTTON_HEIGHT),
            shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
            colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
        ) {
            Text(
                "Start Crop Recommendation",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
