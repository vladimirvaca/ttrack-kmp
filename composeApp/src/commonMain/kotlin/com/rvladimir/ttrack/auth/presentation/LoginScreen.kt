package com.rvladimir.ttrack.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rvladimir.ttrack.core.isAndroid
import com.rvladimir.ttrack.core.ui.components.TTrackFormLabel
import com.rvladimir.ttrack.core.ui.components.TTrackTextField
import com.rvladimir.ttrack.ui.theme.BrandGreen
import com.rvladimir.ttrack.ui.theme.DarkBackground
import com.rvladimir.ttrack.ui.theme.DeepCharcoal
import com.rvladimir.ttrack.ui.theme.InputBorder

@Composable
@Preview
fun LoginScreen(
    viewModel: LoginViewModel = viewModel { LoginViewModelFactory.create() },
    onLoginSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is LoginUiState.Loading

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is LoginUiState.Success -> {
                onLoginSuccess()
                viewModel.resetState()
            }
            is LoginUiState.Error -> {
                snackbarHostState.showSnackbar(message = state.message)
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.White),
        ) {
            // ── Top Header ────────────────────────────────────────────────────
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(DarkBackground),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                ),
                            ),
                )
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier =
                                Modifier
                                    .size(32.dp)
                                    .background(BrandGreen, RoundedCornerShape(4.dp)),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TTRACK",
                            color = BrandGreen,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Welcome back",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Let's crush your goals today.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                    )
                }
            }

            // ── Form ─────────────────────────────────────────────────────────
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
            ) {
                TTrackFormLabel(label = "Email Address")
                TTrackTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "you@example.com",
                    keyboardType = KeyboardType.Email,
                )

                Spacer(modifier = Modifier.height(16.dp))

                TTrackFormLabel(label = "Password")
                TTrackTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "••••••••",
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordToggle = { passwordVisible = !passwordVisible },
                    keyboardType = KeyboardType.Password,
                )

                Text(
                    text = "Forgot Password?",
                    color = DeepCharcoal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            .align(Alignment.End)
                            .padding(top = 12.dp),
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.login(email, password) },
                    enabled = !isLoading,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = Color.Black,
                        ),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = "LOG IN",
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground,
                            fontSize = 16.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = InputBorder)
                    Text(
                        text = "OR CONTINUE WITH",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = DeepCharcoal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = InputBorder)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (isAndroid) {
                        SocialButton(modifier = Modifier.weight(1f), text = "GOOGLE")
                    } else {
                        SocialButton(modifier = Modifier.weight(1f), text = "APPLE")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(text = "New here? ", color = Color.Black)
                    Text(
                        text = "Create an account",
                        color = BrandGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToSignUp() },
                    )
                }
            }
        }

        // ── Error Snackbar ────────────────────────────────────────────────────
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF323232),
                contentColor = Color.White,
            )
        }
    }
}

@Composable
private fun SocialButton(
    modifier: Modifier = Modifier,
    text: String,
) {
    OutlinedButton(
        onClick = { /* TODO */ },
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, InputBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}
