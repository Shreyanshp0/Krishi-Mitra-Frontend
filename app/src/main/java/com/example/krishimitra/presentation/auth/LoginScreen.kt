package com.example.krishimitra.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.R
import com.example.krishimitra.ui.Dimensions
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LeafGreen

@Composable
fun LoginScreen(
    formState: LoginFormState,
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateSignup: () -> Unit,
    onAuthSuccess: () -> Unit,
    onErrorShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val emailFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            AuthUiState.Success -> onAuthSuccess()
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar(uiState.message)
                onErrorShown()
            }
            AuthUiState.Idle,
            AuthUiState.Loading -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(Dimensions.LARGE),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Logo
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(DeepGreen, LeafGreen)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "KM",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(Dimensions.LARGE))

                // Welcome text
                Text(
                    text = stringResource(R.string.login_welcome),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(R.string.login_instruction),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Dimensions.EXTRA_LARGE))

                // Login Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_LARGE),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(Dimensions.LARGE),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.MEDIUM)
                    ) {
                        EmailInputField(
                            value = formState.email,
                            onValueChange = onEmailChange,
                            error = formState.emailError,
                            modifier = Modifier.focusRequester(emailFocusRequester)
                        )

                        OutlinedTextField(
                            value = formState.password,
                            onValueChange = onPasswordChange,
                            label = { Text(stringResource(R.string.password), style = MaterialTheme.typography.bodyMedium) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            singleLine = true,
                            isError = formState.passwordError != null,
                            supportingText = {
                                formState.passwordError?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            shape = RoundedCornerShape(Dimensions.CORNER_RADIUS_MEDIUM),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = formState.rememberMe,
                                onCheckedChange = onRememberChange,
                                colors = CheckboxDefaults.colors(checkedColor = DeepGreen)
                            )
                            Text(
                                text = stringResource(R.string.remember_me),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AuthButton(
                            text = stringResource(R.string.login_btn),
                            onClick = onLoginClick,
                            isLoading = uiState is AuthUiState.Loading,
                            modifier = Modifier.padding(top = Dimensions.SMALL)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimensions.LARGE))

                TextButton(onClick = onNavigateSignup) {
                    Text(
                        text = stringResource(R.string.new_farmer_signup),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(Dimensions.MEDIUM))



            }
        }
    }
}
