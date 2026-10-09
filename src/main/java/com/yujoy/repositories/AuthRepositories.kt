package com.yujoy.repositories

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.yujoy.models.User
import com.yujoy.utils.Result
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isUserLoggedIn: Boolean get() = currentUser != null

    // Register new user
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String
    ): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val user = User(
                    userId = firebaseUser.uid,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    username = username,
                    fullName = "$firstName $lastName",
                    role = "MEMBER",
                    isActive = true,
                    languagePreference = "EN",
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

                usersCollection.document(firebaseUser.uid).set(user).await()
                Result.Success(user)
            } else {
                Result.Error("User creation failed")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed")
        }
    }

    // Login user
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val userDoc = usersCollection.document(firebaseUser.uid).get().await()
                val user = userDoc.toObject(User::class.java)

                if (user != null) {
                    Result.Success(user)
                } else {
                    Result.Error("User data not found")
                }
            } else {
                Result.Error("Login failed")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }

    // Get current user data
    suspend fun getCurrentUserData(): Result<User> {
        return try {
            val firebaseUser = auth.currentUser

            if (firebaseUser != null) {
                val userDoc = usersCollection.document(firebaseUser.uid).get().await()
                val user = userDoc.toObject(User::class.java)

                if (user != null) {
                    Result.Success(user)
                } else {
                    Result.Error("User data not found")
                }
            } else {
                Result.Error("No user logged in")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch user data")
        }
    }

    // Reset password
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to send reset email")
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }

    // Update user profile
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
                "updatedAt" to Timestamp.now()
            )

            usersCollection.document(userId).update(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update profile")
        }
    }
}