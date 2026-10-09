package com.yujoy.repositories

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.yujoy.utils.Result
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ImageRepository {
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadProfilePicture(userId: String, imageUri: Uri): Result<String> {
        return try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child("profile_pictures")
                .child(userId)
                .child(fileName)

            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.Success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to upload image")
        }
    }

    suspend fun uploadEventCover(eventId: String, imageUri: Uri): Result<String> {
        return try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child("event_covers")
                .child(eventId)
                .child(fileName)

            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.Success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to upload image")
        }
    }

    suspend fun uploadClubLogo(clubId: String, imageUri: Uri): Result<String> {
        return try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child("club_logos")
                .child(clubId)
                .child(fileName)

            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.Success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to upload image")
        }
    }
}