package com.yujoy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.yujoy.ui.theme.YujoyCyan
import com.yujoy.ui.theme.BackgroundLight

@Composable
fun EmailSentScreen(
    email: String,
    onNavigateToLogin: () -> Unit
) {
    // Auto-navigate back to login after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight), // Light gray background
        contentAlignment = Alignment.Center
    ) {
        // Success Card
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
                // Checkmark Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = YujoyCyan, // Yujoy brand color
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Success Message
                Text(
                    text = buildAnnotatedString {
                        append("A recovery link has been sent to your ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("email")
                        }
                        append(" address.")
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Show the email address (optional)
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}