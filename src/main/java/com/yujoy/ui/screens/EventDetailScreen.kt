package com.yujoy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yujoy.R
import com.yujoy.models.Event
import com.yujoy.ui.theme.YujoyCyan
import java.text.SimpleDateFormat
import java.util.*

private val dateFormatter = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
private val timeFormatter = SimpleDateFormat("HH:mm", Locale("tr"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val formattedDate = remember(event.eventDate) {
        event.eventDate?.toDate()?.let { dateFormatter.format(it) } ?: ""
    }
    val formattedTime = remember(event.eventDate) {
        event.eventDate?.toDate()?.let { timeFormatter.format(it) } ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = YujoyCyan,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Event Cover Image
            AsyncImage(
                model = event.coverImageUrl ?: "",
                contentDescription = event.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
                placeholder = null,
                error = null
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Event Title
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Club Name
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = YujoyCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.clubName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = YujoyCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date & Time
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Date",
                    value = formattedDate
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(
                    icon = Icons.Default.AccessTime,
                    label = "Time",
                    value = formattedTime
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Location
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Location",
                    value = event.location
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Capacity Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Capacity",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = "${event.currentRegistrations}/${event.capacity}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Available Spots",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = "${event.availableSpots}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (event.availableSpots > 10) Color(0xFF4CAF50) else Color(0xFFFF5252)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Registration Status
                val registrationStatus = event.getRegistrationStatus()
                val canRegister = event.canRegister()

                RegistrationStatusCard(registrationStatus)

                Spacer(modifier = Modifier.height(16.dp))

                // Register Button
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = canRegister,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YujoyCyan,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when {
                            !canRegister -> "Registration Closed"
                            else -> "Register Now"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RegistrationStatusCard(status: com.yujoy.models.RegistrationStatus) {
    val (backgroundColor, textColor, statusText) = when (status) {
        com.yujoy.models.RegistrationStatus.AVAILABLE -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF4CAF50),
            "Registration Open"
        )
        com.yujoy.models.RegistrationStatus.NOT_STARTED -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFFF9800),
            "Registration Not Started"
        )
        com.yujoy.models.RegistrationStatus.CLOSED -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFF44336),
            "Registration Closed"
        )
        com.yujoy.models.RegistrationStatus.FULL -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFF44336),
            "Event Full"
        )
        com.yujoy.models.RegistrationStatus.INACTIVE -> Triple(
            Color(0xFFF5F5F5),
            Color(0xFF9E9E9E),
            "Event Inactive"
        )
        com.yujoy.models.RegistrationStatus.INVALID -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFF44336),
            "Invalid Registration Period"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}