package com.yujoy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yujoy.viewmodels.AuthViewModel
import com.yujoy.ui.theme.YujoyCyan
import com.yujoy.ui.theme.BackgroundLight

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onPasswordResetSent: (String) -> Unit, // Changed to pass email
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val authState by viewModel.authState.collectAsState()

    // Navigate to EmailSentScreen after successful password reset
    LaunchedEffect(authState.successMessage) {
        if (authState.successMessage != null) {
            viewModel.clearSuccessMessage()
            onPasswordResetSent(email) // Pass the email to the next screen
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight), // Light gray background
        contentAlignment = Alignment.Center
    ) {
        // Forgot Password Card
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    clip = false
                ),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Instruction Text
                Text(
                    text = buildAnnotatedString {
                        append("Please enter your ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("email")
                        }
                        append(" address to receive a password reset link")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field - Bottom border style
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        TextField(
                            value = email,
                            onValueChange = {
                                email = it
                                viewModel.clearError() // Clear error when user types
                            },
                            placeholder = {
                                Text(
                                    "Email",
                                    color = Color.Gray
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (email.isNotBlank()) {
                                        viewModel.resetPassword(email)
                                    }
                                }
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Send Button
                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            viewModel.resetPassword(email)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !authState.isLoading && email.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YujoyCyan // Yujoy brand color
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Send",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Error Message
                authState.error?.let { message ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE) // Light red
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = message,
                            color = Color(0xFFC62828), // Dark red
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}