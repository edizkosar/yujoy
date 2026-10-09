package com.yujoy.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yujoy.models.Notification
import com.yujoy.utils.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val notificationsCollection = firestore.collection("notifications")

    fun getNotificationsFlow(): Flow<Result<List<Notification>>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(Result.Error("User not authenticated"))
            close()
            return@callbackFlow
        }

        val listener = notificationsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Failed to fetch notifications"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notifications = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Notification::class.java)
                    }
                    trySend(Result.Success(notifications))
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            notificationsCollection.document(notificationId)
                .update("isRead", true)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to mark as read")
        }
    }

    suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            notificationsCollection.document(notificationId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete notification")
        }
    }
}

