package com.yujoy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yujoy.R
import com.yujoy.models.Club
import com.yujoy.ui.theme.BackgroundLight
import com.yujoy.ui.theme.YujoyCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubListScreen(
    clubs: List<Club>,
    isLoading: Boolean = false,
    userRole: String = "MEMBER",
    onClubClick: (Club) -> Unit,
    onCreateClub: () -> Unit,
    onNotificationClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToReservations: () -> Unit = {},
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            // Cyan top bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = YujoyCyan,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Empty space for symmetry
                    Spacer(modifier = Modifier.width(40.dp))
                    
                    // Title
                    Text(
                        text = "Clubs",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Notification Icon
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Bottom Navigation
            ClubsBottomNavigation(
                onNavigateToHome = onNavigateToHome,
                onNavigateToEvents = onNavigateToEvents,
                onNavigateToReservations = onNavigateToReservations,
                onNavigateToClubs = { /* Already here */ },
                onNavigateToProfile = onNavigateToProfile,
                selectedItem = 2 // Clubs selected
            )
        },
        floatingActionButton = {
            // FAB only for ADMIN
            if (userRole == "ADMIN") {
                FloatingActionButton(
                    onClick = onCreateClub,
                    containerColor = Color.White,
                    contentColor = YujoyCyan,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Club",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Loading state
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = YujoyCyan
                )
            } else if (clubs.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No clubs yet",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                // Club list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(clubs) { club ->
                        ClubCard(
                            club = club,
                            onClick = { onClubClick(club) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClubCard(
    club: Club,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(164.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Club image
            AsyncImage(
                model = club.coverImageUrl,
                contentDescription = club.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.club_icon)
            )
            
            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            
            // Club name at bottom left
            Text(
                text = club.name.uppercase(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ClubsBottomNavigation(
    onNavigateToHome: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToReservations: () -> Unit,
    onNavigateToClubs: () -> Unit,
    onNavigateToProfile: () -> Unit,
    selectedItem: Int = 2 // 0=Home, 1=Events, 2=Clubs, 3=Profile
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
