package com.example.krishimitra.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.krishimitra.presentation.viewmodel.ChatViewModel
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.LightGreen
import com.example.krishimitra.ui.Dimensions

@Composable
fun ChatScreen(
    userState: String = "",
    userDistrict: String = "",
    viewModel: ChatViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Set user location for context
    LaunchedEffect(userState, userDistrict) {
        if (userState.isNotEmpty() || userDistrict.isNotEmpty()) {
            viewModel.setUserLocation(userState, userDistrict)
        }
    }

    // Show initial suggestions if no messages
    val showSuggestions = messages.isEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBeige)
    ) {
        // Chat messages area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty() && !showSuggestions) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimensions.SCREEN_PADDING),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Hello! 👋",
                        modifier = Modifier.padding(Dimensions.MEDIUM),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "I'm your Agri Assistant. Ask me about crops, farming tips, government schemes, and more!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(Dimensions.MEDIUM)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimensions.SCREEN_PADDING),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL),
                    reverseLayout = false
                ) {
                    items(messages) { message ->
                        if (message.isUser) {
                            UserBubble(message.text)
                        } else {
                            BotBubble(message.text)
                        }
                    }

                    // Loading indicator
                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = DeepGreen,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(Dimensions.SMALL))
                                Text("Typing...", color = DeepGreen)
                            }
                        }
                    }
                }
            }
        }

        // Quick suggestions or input area
        if (showSuggestions && messages.isEmpty()) {
            QuickSuggestions(
                suggestions = listOf(
                    "Best crop for my soil?",
                    "How to prevent pests?",
                    "Government schemes available",
                    "Water conservation tips"
                ),
                onSuggestionClick = { suggestion ->
                    viewModel.sendMessage(suggestion)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.SCREEN_PADDING)
            )
        }

        // Input area
        InputArea(
            inputText = inputText,
            onInputChange = { inputText = it },
            onSendClick = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessage(inputText)
                    inputText = ""
                }
            },
            isEnabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.SCREEN_PADDING)
        )
    }
}

@Composable
private fun UserBubble(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.SMALL),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = text,
            modifier = Modifier
                .background(DeepGreen, RoundedCornerShape(12.dp))
                .padding(Dimensions.MEDIUM)
                .fillMaxWidth(0.8f),
            color = Color.White,
            maxLines = Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BotBubble(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.SMALL),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            modifier = Modifier
                .background(LightGreen, RoundedCornerShape(12.dp))
                .padding(Dimensions.MEDIUM)
                .fillMaxWidth(0.8f),
            color = Color.Black,
            maxLines = Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun QuickSuggestions(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
    ) {
        Text(
            "Quick Questions:",
            modifier = Modifier.padding(bottom = Dimensions.SMALL)
        )
        suggestions.forEach { suggestion ->
            Button(
                onClick = { onSuggestionClick(suggestion) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_SMALL),
                colors = ButtonDefaults.buttonColors(containerColor = LightGreen)
            ) {
                Text(
                    suggestion,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InputArea(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = { if (it.length <= 200) onInputChange(it) },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            placeholder = { Text("Ask about crops, schemes...") },
            singleLine = true,
            shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
            enabled = isEnabled
        )

        IconButton(
            onClick = onSendClick,
            enabled = isEnabled && inputText.isNotBlank(),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM))
                .background(if (isEnabled && inputText.isNotBlank()) DeepGreen else Color.LightGray.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}




