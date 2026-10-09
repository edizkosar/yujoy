package com.yujoy.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yujoy.ui.screens.*
import com.yujoy.ui.screens.HistoryScreen
import com.yujoy.ui.screens.ChangePasswordScreen
import com.yujoy.ui.screens.ReservationsScreen
import com.yujoy.models.Event
import com.yujoy.models.Club
import com.yujoy.viewmodels.AuthViewModel
import com.yujoy.viewmodels.EventsViewModel
import com.yujoy.viewmodels.NotificationViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.yujoy.ui.theme.YujoyCyan

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object EmailSent : Screen("email_sent/{email}") {
        fun createRoute(email: String) = "email_sent/$email"
    }
    object Main : Screen("main")
    object Reservations : Screen("reservations")
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object Notifications : Screen("notifications")
    object CreateEvent : Screen("create_event")
    object ClubList : Screen("club_list")
    object ClubDetail : Screen("club_detail/{clubId}") {
        fun createRoute(clubId: String) = "club_detail/$clubId"
    }
    object CreateClub : Screen("create_club")
    object Profile : Screen("profile")
    object ProfileEdit : Screen("profile_edit")
    object History : Screen("history")
    object ChangePassword : Screen("change_password")
}

@Composable
fun YujoyNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn && navController.currentDestination?.route == Screen.Login.route) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) Screen.Main.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            SimpleLoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.Register.route) {
            SimpleRegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onPasswordResetSent = { email ->
                    navController.navigate(Screen.EmailSent.createRoute(email)) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.EmailSent.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailSentScreen(
                email = email,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Main.route)
            }
            val eventsViewModel: EventsViewModel = viewModel(viewModelStoreOwner = parentEntry)
            val eventsState by eventsViewModel.eventsState.collectAsState()

            LaunchedEffect(Unit) {
                eventsViewModel.startListeningIfNeeded()
            }

            EventMainScreen(
                events = eventsState.events,
                isLoading = eventsState.isLoading,
                user = authState.user,
                onEventClick = { event ->
                    navController.navigate(Screen.EventDetail.createRoute(event.eventId))
                },
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onShareClick = { event ->
                    // TODO: Implement share functionality
                },
                onAddFriendClick = { event ->
                    // TODO: Implement add friend functionality
                },
                onNavigateToClubs = {
                    navController.navigate(Screen.ClubList.route)
                },
                onNavigateToHome = {
                    eventsViewModel.refreshEvents()
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToReservations = {
                    navController.navigate(Screen.Reservations.route)
                },
                onCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                }
            )
        }

        composable(Screen.EventDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Main.route)
            }
            val eventsViewModel: EventsViewModel = viewModel(viewModelStoreOwner = parentEntry)
            val eventsState by eventsViewModel.eventsState.collectAsState()

            val event = eventsState.events.find { it.eventId == eventId }

            if (event != null) {
                EventDetailScreen(
                    event = event,
                    onBackClick = { navController.popBackStack() },
                    onRegisterClick = {
                        eventsViewModel.registerToEvent(eventId)
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Event not found")
                }
            }
        }

        composable(Screen.Reservations.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Main.route)
            }
            val eventsViewModel: EventsViewModel = viewModel(viewModelStoreOwner = parentEntry)
            val eventsState by eventsViewModel.eventsState.collectAsState()
            
            // Filter only user's registered events
            // TODO: Implement proper filtering based on EventRegistration collection
            val userRegisteredEvents: List<Event> = remember(eventsState.events, authState.user?.userId) {
                // Temporarily return empty list until backend filtering is implemented
                emptyList()
            }
            
            ReservationsScreen(
                registeredEvents = userRegisteredEvents,
                isLoading = eventsState.isLoading,
                user = authState.user,
                onEventClick = { event ->
                    navController.navigate(Screen.EventDetail.createRoute(event.eventId))
                },
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onShareClick = { event ->
                    // TODO: Implement share functionality
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onNavigateToClubs = {
                    navController.navigate(Screen.ClubList.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                userRole = authState.user?.role ?: "MEMBER"
            )
        }

        composable(Screen.Notifications.route) {
            val notificationViewModel: NotificationViewModel = viewModel()
            val notificationState by notificationViewModel.notificationState.collectAsState()

            NotificationScreen(
                notifications = notificationState.notifications,
                isLoading = notificationState.isLoading,
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { notification ->
                    notificationViewModel.markAsRead(notification.notificationId)
                },
                onDeleteClick = { notificationId ->
                    notificationViewModel.deleteNotification(notificationId)
                }
            )
        }

        composable(Screen.CreateEvent.route) {
            CreateEventScreen(
                onBackClick = { navController.popBackStack() },
                onEventCreated = { navController.popBackStack() },
                userId = authState.user?.userId ?: ""
            )
        }


        composable(Screen.ClubList.route) {
            val db = FirebaseFirestore.getInstance()
            var clubs by remember { mutableStateOf<List<Club>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var error by remember { mutableStateOf<String?>(null) }
            
            LaunchedEffect(Unit) {
                try {
                    val snapshot = db.collection("clubs")
                        .whereEqualTo("isActive", true)
                        .get()
                        .await()
                    
                    clubs = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Club::class.java)?.copy(clubId = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    isLoading = false
                } catch (e: Exception) {
                    error = e.message
                    isLoading = false
                }
            }
            
            ClubListScreen(
                clubs = clubs,
                isLoading = isLoading,
                userRole = authState.user?.role ?: "MEMBER",
                onClubClick = { club ->
                    if (club.clubId.isNotEmpty()) {
                        navController.navigate(Screen.ClubDetail.createRoute(club.clubId))
                    }
                },
                onCreateClub = {
                    navController.navigate(Screen.CreateClub.route)
                },
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route)
                },
                onNavigateToEvents = {
                    navController.navigate(Screen.Main.route)
                },
                onNavigateToReservations = {
                    navController.navigate(Screen.Reservations.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.CreateClub.route) {
            CreateClubScreen(
                onBackClick = { navController.popBackStack() },
                onClubCreated = { navController.popBackStack() },
                userId = authState.user?.userId ?: ""
            )
        }

        composable(Screen.ClubDetail.route) { backStackEntry ->
            val clubId = backStackEntry.arguments?.getString("clubId") ?: ""
            
            if (clubId.isEmpty()) {
                // Invalid club ID, go back
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
                Box(modifier = Modifier.fillMaxSize())
            } else {
                val db = FirebaseFirestore.getInstance()
                
                var club by remember { mutableStateOf<Club?>(null) }
                var isLoading by remember { mutableStateOf(true) }
                var error by remember { mutableStateOf<String?>(null) }
                
                LaunchedEffect(clubId) {
                    try {
                        val snapshot = db.collection("clubs")
                            .document(clubId)
                            .get()
                            .await()
                        
                        club = snapshot.toObject(Club::class.java)
                        isLoading = false
                    } catch (e: Exception) {
                        error = e.message
                        isLoading = false
                    }
                }
                
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = YujoyCyan)
                        }
                    }
                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Error: $error")
                                Button(onClick = { navController.popBackStack() }) {
                                    Text("Go Back")
                                }
                            }
                        }
                    }
                    club == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Club not found")
                                Button(onClick = { navController.popBackStack() }) {
                                    Text("Go Back")
                                }
                            }
                        }
                    }
                    else -> {
                        ClubDetailScreen(
                            club = club,
                            onBackClick = { navController.popBackStack() },
                            onNotificationClick = {
                                navController.navigate(Screen.Notifications.route)
                            }
                        )
                    }
                }
            }
        }


        composable(Screen.Profile.route) {
            ProfileScreen(
                user = authState.user,
                onBackClick = { navController.popBackStack() },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                },
                onEditProfileClick = {
                    navController.navigate(Screen.ProfileEdit.route)
                },
                onChangePasswordClick = {
                    navController.navigate(Screen.ChangePassword.route)
                },
                onHelpClick = {
                    // TODO: Navigate to help screen
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onNavigateToClubs = {
                    navController.navigate(Screen.ClubList.route)
                },
                onNavigateToEvents = {
                    navController.navigate(Screen.Main.route)
                },
                onNavigateToReservations = {
                    navController.navigate(Screen.Reservations.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route)
                }
            )
        }


        composable(Screen.ProfileEdit.route) {
            ProfileEditScreen(
                user = authState.user,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { username, selectedBackground ->
                    // TODO: Save username and background
                    navController.popBackStack()
                },
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onChangePhotoClick = { imageUri ->
                    // TODO: Upload photo
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                pastEvents = emptyList(),
                onBackClick = { navController.popBackStack() },
                onNotificationClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onEventClick = { event ->
                    navController.navigate(Screen.EventDetail.createRoute(event.eventId))
                }
            )
        }

        composable(Screen.ChangePassword.route) {
            var isSuccess by remember { mutableStateOf(false) }

            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onUpdatePassword = { currentPassword, newPassword ->
                    isSuccess = true
                },
                isLoading = false,
                isSuccess = isSuccess
            )
        }
    }
}