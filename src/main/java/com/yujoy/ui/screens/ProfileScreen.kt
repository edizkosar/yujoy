package com.yujoy.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yujoy.R
import com.yujoy.models.User
import com.yujoy.ui.components.LogoutDialog
import com.yujoy.ui.theme.BackgroundLight
import com.yujoy.ui.theme.YujoyCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onLogout: () -> Unit,
    onNotificationClick: () -> Unit = {},
    onNavigateToClubs: () -> Unit = {},
    onNavigateToEvents: () -> Unit = {},
    onNavigateToReservations: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Logout Dialog
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            }
        )
    }
    Scaffold(
        topBar = {
            // Top Bar with decorative background
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
                ) {
                    // You can add decorative card patterns here if needed
                    // For now, simple dark background
                }

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
                        text = "Profile",
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

                // Profile picture (overlapping)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .offset(y = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Profile picture with white border
                    if (user?.profilePictureUrl != null) {
                        AsyncImage(
                            model = user.profilePictureUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                                .background(Color.White),
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
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Bottom Navigation
            BottomNavigationBar(
                onNavigateToClubs = onNavigateToClubs,
                onNavigateToEvents = onNavigateToEvents,
                onNavigateToReservations = onNavigateToReservations,
                onNavigateToHome = onNavigateToHome,
                onNavigateToProfile = { /* Already on profile */ },
                selectedItem = 3 // Profile selected
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer for overlapping profile picture
            Spacer(modifier = Modifier.height(60.dp))

            // Username
            Text(
                text = user?.username ?: "Username",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Full name
            Text(
                text = user?.fullName ?: "Full Name",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp
                ),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Items
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.History,
                    text = "History",
                    onClick = onHistoryClick
                )

                ProfileMenuItem(
                    icon = Icons.Default.Edit,
                    text = "Edit Profile",
                    onClick = onEditProfileClick
                )

                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    text = "Change Password",
                    onClick = onChangePasswordClick
                )

                ProfileMenuItem(
                    icon = Icons.Default.Help,
                    text = "Help",
                    onClick = onHelpClick
                )

                ProfileMenuItem(
                    icon = Icons.Default.Logout,
                    text = "Logout",
                    onClick = { showLogoutDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.DarkGray,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp
                ),
                color = Color.Black
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    onNavigateToClubs: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToReservations: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    selectedItem: Int = 0 // 0=Home, 1=Events, 2=Clubs, 3=Profile
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = YujoyCyan,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Icon
            IconButton(
                onClick = onNavigateToHome,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home_icon),
                    contentDescription = "Home",
                    modifier = Modifier.size(26.dp),
                    colorFilter = if (selectedItem == 0)
                        ColorFilter.tint(Color.White)
                    else
                        ColorFilter.tint(Color.White.copy(alpha = 0.6f))
                )
            }

            // Reservations/Ticket Icon
            IconButton(
                onClick = onNavigateToReservations,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ticket_icon),
                    contentDescription = "Reservations",
                    modifier = Modifier.size(26.dp),
                    colorFilter = if (selectedItem == 1)
                        ColorFilter.tint(Color.White)
                    else
                        ColorFilter.tint(Color.White.copy(alpha = 0.6f))
                )
            }

            // Clubs Icon
            IconButton(
                onClick = onNavigateToClubs,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.club_icon),
                    contentDescription = "Clubs",
                    modifier = Modifier.size(26.dp),
                    colorFilter = if (selectedItem == 2)
                        ColorFilter.tint(Color.White)
                    else
                        ColorFilter.tint(Color.White.copy(alpha = 0.6f))
                )
            }

            // Profile Icon
            IconButton(
                onClick = onNavigateToProfile,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_icon),
                    contentDescription = "Profile",
                    modifier = Modifier.size(26.dp),
                    colorFilter = if (selectedItem == 3)
                        ColorFilter.tint(Color.White)
                    else
                        ColorFilter.tint(Color.White.copy(alpha = 0.6f))
                )
            }
        }
    }
}