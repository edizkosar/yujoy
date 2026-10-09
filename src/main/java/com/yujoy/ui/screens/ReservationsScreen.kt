package com.yujoy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
fun ReservationsScreen(
    registeredEvents: List<Event>,
    isLoading: Boolean = false,
    user: User? = null,
    onEventClick: (Event) -> Unit,
    onNotificationClick: () -> Unit,
    onShareClick: (Event) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToClubs: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onCreateEvent: () -> Unit = {},
    userRole: String = "MEMBER"
) {
    var searchQuery by remember { mutableStateOf("") }

    // Filter registered events based on search
    val filteredEvents = remember(registeredEvents, searchQuery) {
        if (searchQuery.isBlank()) {
            registeredEvents
        } else {
            registeredEvents.filter { event ->
                event.title.contains(searchQuery, ignoreCase = true) ||
                        event.location.contains(searchQuery, ignoreCase = true) ||
                        event.clubName.contains(searchQuery, ignoreCase = true)
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
                    // Back button - Left aligned
                    IconButton(
                        onClick = onNavigateToHome,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Title - Centered
                    Text(
                        text = "Reservations",
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
            BottomNavigationBar(
                selectedItem = 1, // Reservations selected (ticket icon)
                onNavigateToHome = onNavigateToHome,
                onNavigateToReservations = { /* Already here */ },
                onNavigateToClubs = onNavigateToClubs,
                onNavigateToProfile = onNavigateToProfile,
                onCreateEvent = onCreateEvent,
                userRole = userRole
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
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontSize = 14.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    "Search Event",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp
                                    ),
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = YujoyCyan)
                    }
                }
                filteredEvents.isEmpty() -> {
                    // Empty State
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.Gray.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isBlank()) 
                                    "No registered events yet" 
                                else 
                                    "No events found",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                            if (searchQuery.isBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Browse events and register to see them here",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
                else -> {
                    // Event List - Same card design as EventMainScreen
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredEvents) { event ->
                            RegisteredEventCard(
                                event = event,
                                onClick = { onEventClick(event) },
                                onShareClick = { onShareClick(event) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisteredEventCard(
    event: Event,
    onClick: () -> Unit,
    onShareClick: () -> Unit
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

                // Event Date
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp
                    ),
                    color = Color.Gray
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
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selectedItem: Int,
    onNavigateToHome: () -> Unit,
    onNavigateToReservations: () -> Unit,
    onNavigateToClubs: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onCreateEvent: () -> Unit,
    userRole: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = YujoyCyan,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Events Icon (home_icon in design)
                IconButton(
                    onClick = onNavigateToHome,
                    modifier = Modifier.size(48.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.home_icon),
                        contentDescription = "Events",
                        modifier = Modifier.size(26.dp),
                        colorFilter = if (selectedItem == 0)
                            ColorFilter.tint(Color.White)
                        else
                            ColorFilter.tint(Color.White.copy(alpha = 0.6f))
                    )
                }

                // Reservations/Ticket Icon - SELECTED
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

        // Center FAB - Only for PRESIDENT
        if (userRole == "PRESIDENT") {
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
