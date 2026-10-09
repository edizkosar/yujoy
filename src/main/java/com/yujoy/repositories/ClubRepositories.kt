package com.yujoy.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.yujoy.models.Club
import com.yujoy.models.ClubMembership
import com.yujoy.utils.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ClubRepository {
    private val db = FirebaseFirestore.getInstance()

    // Get all active clubs (real-time)
    fun getAllClubs(): Flow<Result<List<Club>>> = callbackFlow {
        val listener = db.collection("clubs")
            .whereEqualTo("isActive", true)
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Failed to get clubs"))
                    return@addSnapshotListener
                }

                val clubs = snapshot?.documents?.mapNotNull {
                    it.toObject(Club::class.java)
                } ?: emptyList()

                trySend(Result.Success(clubs))
            }

        awaitClose { listener.remove() }
    }

    // Get club by ID
    suspend fun getClubById(clubId: String): Result<Club> {
        return try {
            val doc = db.collection("clubs")
                .document(clubId)
                .get()
                .await()

            val club = doc.toObject(Club::class.java)
                ?: return Result.Error("Club not found")

            Result.Success(club)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get club")
        }
    }

    // Check if user is member of club
    suspend fun isUserMember(clubId: String, userId: String): Result<Boolean> {
        return try {
            val doc = db.collection("clubs")
                .document(clubId)
                .collection("members")
                .document(userId)
                .get()
                .await()

            val membership = doc.toObject(ClubMembership::class.java)
            Result.Success(membership?.isActive == true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to check membership")
        }
    }

    // Join club
    suspend fun joinClub(clubId: String, userId: String): Result<Unit> {
        return try {
            // Check if already a member
            val isMember = isUserMember(clubId, userId)
            if (isMember is Result.Success && isMember.data) {
                return Result.Error("Already a member of this club")
            }

            db.runTransaction { transaction ->
                val clubRef = db.collection("clubs").document(clubId)
                val memberRef = clubRef.collection("members").document(userId)

                // Add membership
                transaction.set(memberRef, ClubMembership().toMap())

                // Increment member count
                transaction.update(clubRef, "memberCount", FieldValue.increment(1))
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to join club")
        }
    }

    // Leave club
    suspend fun leaveClub(clubId: String, userId: String): Result<Unit> {
        return try {
            db.runTransaction { transaction ->
                val clubRef = db.collection("clubs").document(clubId)
                val memberRef = clubRef.collection("members").document(userId)

                // Remove or deactivate membership
                transaction.update(memberRef, "isActive", false)

                // Decrement member count
                transaction.update(clubRef, "memberCount", FieldValue.increment(-1))
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to leave club")
        }
    }

    // Get user's clubs
    fun getUserClubs(userId: String): Flow<Result<List<Club>>> = callbackFlow {
        val listener = db.collectionGroup("members")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Failed to get clubs"))
                    return@addSnapshotListener
                }

                // Get club IDs from memberships
                val clubIds = snapshot?.documents?.mapNotNull {
                    it.reference.parent.parent?.id
                } ?: emptyList()

                // Fetch club details (could be optimized with batching)
                // For now, this is a simple implementation
                trySend(Result.Success(emptyList())) // Placeholder
            }

        awaitClose { listener.remove() }
    }

    // Create club (ADMIN only)
    suspend fun createClub(
        name: String,
        description: String,
        presidentId: String,
        presidentName: String,
        category: String?,
        coverImageUrl: String?,
        logoUrl: String?
    ): Result<String> {
        return try {
            val club = Club(
                name = name,
                description = description,
                presidentId = presidentId,
                presidentName = presidentName,
                category = category,
                coverImageUrl = coverImageUrl,
                logoUrl = logoUrl,
                memberCount = 0,
                isActive = true,
                createdAt = Timestamp.now()
            )

            val docRef = db.collection("clubs")
                .add(club.toMap())
                .await()

            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create club")
        }
    }

    // Update club info (PRESIDENT only)
    suspend fun updateClub(
        clubId: String,
        name: String,
        description: String,
        category: String?,
        coverImageUrl: String?,
        logoUrl: String?
    ): Result<Unit> {
        return try {
            db.collection("clubs")
                .document(clubId)
                .update(
                    mapOf(
                        "name" to name,
                        "description" to description,
                        "category" to category,
                        "coverImageUrl" to coverImageUrl,
                        "logoUrl" to logoUrl,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update club")
        }
    }
}