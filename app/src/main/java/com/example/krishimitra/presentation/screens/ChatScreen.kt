package com.example.krishimitra.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.krishimitra.R
import com.example.krishimitra.presentation.viewmodel.ChatViewModel
import com.example.krishimitra.ui.theme.*
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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(userState, userDistrict) {
        if (userState.isNotEmpty() || userDistrict.isNotEmpty()) {
            viewModel.setUserLocation(userState, userDistrict)
        }
    }

    val showSuggestions = messages.isEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        // Chat Header
        Surface(
            color = Color.White,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.MEDIUM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DeepGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = DeepGreen, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(Dimensions.MEDIUM))
                Column {
                    Text(
                        text = stringResource(R.string.insights),
                        style = MaterialTheme.typography.titleMedium,
                        color = DeepGreen
                    )
                    Text(
                        text = if (isLoading) stringResource(R.string.typing) else "Online",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLoading) DeepGreen else Color.Gray
                    )
                }
            }
        }

        // Chat messages area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                WelcomeView()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimensions.SCREEN_PADDING),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
                ) {
                    items(messages) { message ->
                        ChatBubble(message.text, message.isUser)
                    }

                    if (isLoading) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }
        }

        // Bottom area: Suggestions + Input
        Surface(
            color = Color.White,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.MEDIUM)
                    .padding(bottom = 8.dp)
            ) {
                AnimatedVisibility(visible = showSuggestions) {
                    QuickSuggestions(
                        suggestions = listOf(
                            stringResource(R.string.suggestion_best_crop),
                            stringResource(R.string.suggestion_prevent_pests),
                            stringResource(R.string.suggestion_govt_schemes)
                        ),
                        onSuggestionClick = { viewModel.sendMessage(it) }
                    )
                }
                
                Spacer(modifier = Modifier.height(Dimensions.SMALL))
                
                InputArea(
                    inputText = inputText,
                    onInputChange = { inputText = it },
                    onSendClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    isEnabled = !isLoading
                )
            }
        }
    }
}

@Composable
private fun WelcomeView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.EXTRA_LARGE),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = DeepGreen.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.height(Dimensions.LARGE))
        Text(
            text = stringResource(R.string.chat_welcome),
            style = MaterialTheme.typography.headlineSmall,
            color = DeepGreen,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimensions.SMALL))
        Text(
            text = stringResource(R.string.chat_intro),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ChatBubble(text: String, isUser: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Avatar(Icons.Default.AutoAwesome, DeepGreen)
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Surface(
            color = if (isUser) DeepGreen else LightGreen.copy(alpha = 0.3f),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 20.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 20.dp
            ),
            tonalElevation = if (isUser) 0.dp else 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                color = if (isUser) Color.White else Color.Black
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Avatar(Icons.Default.Person, SoilBrown)
        }
    }
}

@Composable
private fun Avatar(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.padding(start = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = LightGreen.copy(alpha = 0.3f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = stringResource(R.string.typing),
                    style = MaterialTheme.typography.labelSmall,
                    color = DeepGreen
                )
            }
        }
    }
}

@Composable
private fun QuickSuggestions(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SMALL)) {
        Text(
            text = stringResource(R.string.quick_questions),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
        ) {
            suggestions.forEach { suggestion ->
                AssistChip(
                    onClick = { onSuggestionClick(suggestion) },
                    label = { Text(suggestion, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    shape = CircleShape,
                    colors = AssistChipDefaults.assistChipColors(containerColor = LightGreen.copy(alpha = 0.2f))
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
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SMALL)
    ) {
        IconButton(onClick = { /* Voice Input Placeholder */ }) {
            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.Gray)
        }
        
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.ask_placeholder), style = MaterialTheme.typography.bodyMedium) },
            shape = CircleShape,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DeepGreen,
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            ),
            enabled = isEnabled
        )

        IconButton(
            onClick = onSendClick,
            enabled = isEnabled && inputText.isNotBlank(),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isEnabled && inputText.isNotBlank()) DeepGreen else Color.LightGray.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
