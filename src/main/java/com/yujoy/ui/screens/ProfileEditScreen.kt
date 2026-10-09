package com.yujoy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yujoy.R
import com.yujoy.models.User
import com.yujoy.ui.theme.BackgroundLight
import com.yujoy.ui.theme.YujoyCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    user: User?,
    onBackClick: () -> Unit,
    onSaveClick: (username: String, selectedBackground: Int?) -> Unit,
    onNotificationClick: () -> Unit = {},
    onChangePhotoClick: (Uri) -> Unit = {}
) {
    var username by remember { mutableStateOf(user?.username ?: "") }
    var selectedBackground by remember { mutableStateOf<Int?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            onChangePhotoClick(it)
        }
    }

    Scaffold(
        topBar = {
            // Top Bar with dark background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Dark background with card pattern
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFF1A1A1A))
                )

                // Top bar content
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Title
                    Text(
                        text = "Edit",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Color.White
                    )

                    // Notification button
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Profile picture with camera icon (overlapping)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .offset(y = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(120.dp)
                    ) {
                        // Profile picture
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Color.White, CircleShape)
                                    .background(Color.White)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                contentScale = ContentScale.Crop
                            )
                        } else if (user?.profilePictureUrl != null) {
                            AsyncImage(
                                model = user.profilePictureUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Color.White, CircleShape)
                                    .background(Color.White)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.user_1),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, Color.White, CircleShape)
                                    .background(Color.White)
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Camera icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomCenter)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Picture",
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer for overlapping profile picture
            Spacer(modifier = Modifier.height(70.dp))

            // "Change Picture" text
            Text(
                text = "Change Picture",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Username Field
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Username",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Change Background Section
            Text(
                text = "Change Background",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Background options grid (2 rows x 3 columns)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BackgroundOption(
                        index = 0,
                        isSelected = selectedBackground == 0,
                        onClick = { selectedBackground = 0 },
                        hasCheckmark = selectedBackground == 0,
                        modifier = Modifier.weight(1f)
                    )
                    BackgroundOption(
                        index = 1,
                        isSelected = selectedBackground == 1,
                        onClick = { selectedBackground = 1 },
                        hasCheckmark = false,
                        modifier = Modifier.weight(1f)
                    )
                    BackgroundOption(
                        index = 2,
                        isSelected = selectedBackground == 2,
                        onClick = { selectedBackground = 2 },
                        hasCheckmark = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BackgroundOption(
                        index = 3,
                        isSelected = selectedBackground == 3,
                        onClick = { selectedBackground = 3 },
                        hasCheckmark = false,
                        modifier = Modifier.weight(1f)
                    )
                    BackgroundOption(
                        index = 4,
                        isSelected = selectedBackground == 4,
                        onClick = { selectedBackground = 4 },
                        hasCheckmark = false,
                        modifier = Modifier.weight(1f)
                    )
                    BackgroundOption(
                        index = 5,
                        isSelected = selectedBackground == 5,
                        onClick = { selectedBackground = 5 },
                        hasCheckmark = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save button (hidden but handles save)
            // In the design, there's no visible save button
            // You can add one or auto-save on back
        }
    }
}

@Composable
fun BackgroundOption(
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    hasCheckmark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) YujoyCyan else Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Background preview
        // TODO: Show actual background patterns here
        // For now, show placeholder

        if (hasCheckmark) {
            // Green checkmark for first option (example)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}