package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige

@Composable
fun RecommendScreen(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimensions.SCREEN_PADDING),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
            colors = CardDefaults.cardColors(containerColor = LightBeige)
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.LARGE),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
            ) {
                Text("Get Crop Recommendations", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = DeepGreen)
                Text("Fill in your soil details and receive personalized crop recommendations.", style = MaterialTheme.typography.bodyMedium)

                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepGreen)
                ) {
                    Text("Start Crop Recommendation", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

