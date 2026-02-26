package com.rvladimir.ttrack.registration.presentation

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rvladimir.ttrack.ui.theme.BrandGreen

/**
 * Screen that lets a new user register an account.
 *
 * Follows stateless-composable / unidirectional-data-flow conventions:
 * all mutable state lives in [RegisterViewModel] and is observed via [RegisterViewModel.uiState].
 *
 * @param viewModel The ViewModel that drives this screen.
 * @param onRegistered Callback invoked after a successful registration.
 * @param onLoginClick Callback invoked when the user taps "Log In".
 * @param onBack Callback invoked when the user taps the back arrow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel { RegisterViewModelFactory.create() },
    onRegistered: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is RegisterUiState.Loading

    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            onRegistered()
            viewModel.resetState()
        }
    }

    if (uiState is RegisterUiState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.resetState() },
            confirmButton = {
                TextButton(onClick = { viewModel.resetState() }) { Text("OK") }
            },
            title = { Text("Error") },
            text = { Text((uiState as RegisterUiState.Error).message) },
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                            tint = Color.Black,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFF8F9FA),
                    ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo / icon
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .background(BrandGreen.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(32.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Get Moving with Ttrack",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Text(
                text = "Create your profile to start tracking your daily fitness routines.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // First / Last name row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    RegisterFormFieldLabel("First Name")
                    RegisterTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "Jane",
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    RegisterFormFieldLabel("Last Name")
                    RegisterTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Doe",
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            RegisterFormFieldLabel("Nickname")
            RegisterTextField(
                value = nickname,
                onValueChange = { nickname = it },
                placeholder = "J-Doe",
                leadingIcon = Icons.Default.AlternateEmail,
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegisterFormFieldLabel("Email")
            RegisterTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "jane@example.com",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegisterFormFieldLabel("Password")
            RegisterTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Min. 8 characters",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible },
                keyboardType = KeyboardType.Password,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Terms checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = agreeToTerms,
                    onCheckedChange = { agreeToTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = BrandGreen),
                )
                Text(
                    text = "I agree to the Terms of Service and Privacy Policy.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up button
            Button(
                onClick = {
                    viewModel.register(
                        firstName = firstName,
                        lastName = lastName,
                        nickname = nickname,
                        email = email,
                        password = password,
                    )
                },
                enabled = agreeToTerms && !isLoading,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Sign Up",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.Black,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom link
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
                Text(
                    text = "Log In",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGreen,
                    modifier = Modifier.clickable { onLoginClick() },
                )
            }
        } // end Column
    } // end Scaffold
}

// ─── Local UI components ──────────────────────────────────────────────────────

/**
 * A label above a form field, aligned to the start.
 */
@Composable
fun RegisterFormFieldLabel(label: String) {
    Text(
        text = label,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = Color.Black,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        textAlign = TextAlign.Start,
    )
}

/**
 * A styled [OutlinedTextField] used throughout the Register form.
 */
@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color.LightGray) },
        leadingIcon =
            leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(20.dp),
                    )
                }
            },
        trailingIcon =
            if (isPassword) {
                {
                    IconButton(onClick = onPasswordToggle) {
                        Icon(
                            imageVector =
                                if (passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.Gray,
                        )
                    }
                }
            } else {
                null
            },
        visualTransformation =
            if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = BrandGreen,
            ),
        singleLine = true,
    )
}
