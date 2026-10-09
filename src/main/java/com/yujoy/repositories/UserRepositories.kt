package com.yujoy.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yujoy.models.User
import com.yujoy.utils.Result
import kotlinx.coroutines.tasks.await

class UserRepositories {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun getUserData(userId: String): Result<User> {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    Result.Success(user)
                } else {
                    Result.Error("Failed to parse user data")
                }
            } else {
                Result.Error("User not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch user data")
        }
    }

    suspend fun getCurrentUserData(): Result<User> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            getUserData(currentUser.uid)
        } else {
            Result.Error("No user logged in")
        }
    }

    suspend fun updateUserProfile(
        userId: String,
        firstName: String,
        lastName: String,
        username: String
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "username" to username,
                "fullName" to "$firstName $lastName",
                "updatedAt" to com.google.firebase.Timestamp.now()
            )

            usersCollection.document(userId).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update profile")
        }
    }

    suspend fun updateProfilePicture(userId: String, profilePictureUrl: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "profilePictureUrl" to profilePictureUrl,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )

            usersCollection.document(userId).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update profile picture")
        }
    }
}