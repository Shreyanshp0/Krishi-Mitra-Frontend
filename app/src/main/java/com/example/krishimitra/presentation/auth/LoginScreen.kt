package com.example.krishimitra.presentation.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.ui.theme.DarkCharcoal
import com.example.krishimitra.ui.theme.DeepGreen
import com.example.krishimitra.ui.theme.LightBeige
import com.example.krishimitra.ui.theme.SoilBrown

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
        containerColor = LightBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // App Logo
            Box(
                modifier = Modifier
                    .height(84.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(color = DeepGreen, shape = CircleShape)
                        .padding(horizontal = 24.dp, vertical = 18.dp)
                ) {
                    Text(text = "KM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            }

            // Welcome text
            Text(
                text = "Welcome Back 👋",
                style = MaterialTheme.typography.headlineSmall,
                color = DarkCharcoal,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Login with email and password",
                style = MaterialTheme.typography.bodyMedium,
                color = SoilBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Login Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EmailInputField(
                        value = formState.email,
                        onValueChange = onEmailChange,
                        error = formState.emailError,
                        isEnabled = true,
                        placeholder = "farmer@example.com",
                        modifier = Modifier
                            .focusRequester(emailFocusRequester)
                    )

                    androidx.compose.material3.OutlinedTextField(
                        value = formState.password,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        singleLine = true,
                        isError = formState.passwordError != null,
                        supportingText = {
                            formState.passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Remember me
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = formState.rememberMe,
                            onCheckedChange = { onRememberChange(it) },
                            colors = androidx.compose.material3.CheckboxDefaults.colors(checkedColor = DeepGreen)
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(text = "Remember me", color = SoilBrown)
                    }

                    AuthButton(
                        text = "Login / Continue",
                        onClick = onLoginClick,
                        isLoading = uiState is AuthUiState.Loading,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Sign Up Link
            TextButton(
                onClick = onNavigateSignup,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    "New farmer? Create Account",
                    color = SoilBrown,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Debug credentials hint
            Text(
                "Test: farmer@example.com + password: PASS11",
                style = MaterialTheme.typography.bodySmall,
                color = SoilBrown.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

