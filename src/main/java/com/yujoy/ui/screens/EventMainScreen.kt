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
import androidx.compose.ui.draw.shadow
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
import com.yujoy.models.Event
import com.yujoy.models.User
import com.yujoy.ui.theme.YujoyCyan
import com.yujoy.ui.theme.BackgroundLight
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventMainScreen(
    events: List<Event>,
    isLoading: Boolean = false,
    user: User? = null,
    onEventClick: (Event) -> Unit,
    onNotificationClick: () -> Unit,
    onShareClick: (Event) -> Unit,
    onAddFriendClick: (Event) -> Unit,
    onNavigateToClubs: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToReservations: () -> Unit,
    onCreateEvent: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    // Filter events based on search
    val filteredEvents = remember(events, searchQuery) {
        if (searchQuery.isBlank()) {
            events
        } else {
            events.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.location.contains(searchQuery, ignoreCase = true) ||
                        it.clubName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            // Top Bar - Cyan with title and notification
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = YujoyCyan,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Title - Centered
                    Text(
                        text = "Events",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        ),
                        color = Color.White
                    )

                    // Notification Icon - Right aligned
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
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
            // Bottom Navigation with centered FAB
            BottomNavigationWithCenterFAB(
                onNavigateToClubs = onNavigateToClubs,
                onNavigateToHome = onNavigateToHome,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToReservations = onNavigateToReservations,
                onCreateEvent = onCreateEvent,
                selectedItem = 1,
                userRole = user?.role ?: "MEMBER"
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(25.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search Event",
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Loading indicator
            if (isLoading && events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YujoyCyan)
                }
            } else {
                // Events List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredEvents, key = { it.eventId }) { event ->
                        EventCard(
                            event = event,
                            onClick = { onEventClick(event) },
                            onShareClick = { onShareClick(event) },
                            onAddFriendClick = { onAddFriendClick(event) }
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    onShareClick: () -> Unit,
    onAddFriendClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
    val formattedDate = event.eventDate?.toDate()?.let { dateFormat.format(it) } ?: ""

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Event Image - Left side
            AsyncImage(
                model = event.coverImageUrl ?: "",
                contentDescription = event.title,
                modifier = Modifier
                    .size(95.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.user_1),
                error = painterResource(id = R.drawable.user_1)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Event Details - Right side
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Event Title
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Club Name
                Text(
                    text = event.clubName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = YujoyCyan
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Event Location
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Event Date
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = Color.Gray
                )

                // Bottom Row: Progress Bar and Share Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(18.dp)
                    ) {
                        // Background bar (dark gray)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(18.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Color(0xFF424242))
                        )

                        // Progress fill (colored based on percentage)
                        val progress = if (event.capacity > 0) {
                            (event.currentRegistrations.toFloat() / event.capacity.toFloat())
                        } else 0f

                        val progressColor = when {
                            progress < 0.5f -> Color(0xFFFFD700) // Yellow for low
                            progress < 0.8f -> Color(0xFF4CAF50) // Green for medium
                            else -> Color(0xFFFF5252) // Red for almost full
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .height(18.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(progressColor)
                        )

                        // Registration count text (white, inside bar)
                        Text(
                            text = "${event.currentRegistrations}/${event.capacity}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Share/External Link Icon
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Share",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationWithCenterFAB(
    onNavigateToClubs: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToReservations: () -> Unit,
    onCreateEvent: () -> Unit,
    selectedItem: Int = 1,
    userRole: String = "MEMBER"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        // Bottom Navigation Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
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
                            ColorFilter.tint(Color.White.copy(alpha = 0.7f))
                    )
                }

                // Reservations/Ticket Icon
                IconButton(
                    onClick = onNavigateToReservations,
                    modifier = Modifier.size(48.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ticket_icon),
                        contentDescription = "Events",
                        modifier = Modifier.size(26.dp),
                        colorFilter = if (selectedItem == 1)
                            ColorFilter.tint(Color.White)
                        else
                            ColorFilter.tint(Color.White.copy(alpha = 0.6f))
                    )
                }

                // Spacer for center FAB - Only if PRESIDENT
                if (userRole == "PRESIDENT") {
                    Spacer(modifier = Modifier.width(80.dp))
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
                            ColorFilter.tint(Color.White.copy(alpha = 0.7f))
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
                            ColorFilter.tint(Color.White.copy(alpha = 0.7f))
                    )
                }
            }
        }

        // Center FAB - Elevated above bottom bar
        // Sadece PRESIDENT veya ADMIN görebilir
        // Sadece PRESIDENT gorebilir (Event yaratmak icin)
        val canCreateEvent = userRole == "PRESIDENT"
        if (canCreateEvent) {
            FloatingActionButton(
                onClick = onCreateEvent,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp)
                    .size(56.dp),
                containerColor = Color.White,
                contentColor = YujoyCyan,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}