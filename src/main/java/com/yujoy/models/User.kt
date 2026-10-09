package com.yujoy.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val userId: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val fullName: String = "",
    val profilePictureUrl: String? = null,
    val role: String = "MEMBER", // "MEMBER", "PRESIDENT", "ADMIN"
    val languagePreference: String = "EN",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val isActive: Boolean = true
) {
    // Convert to map for Firestore
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "username" to username,
            "fullName" to fullName,
            "profilePictureUrl" to profilePictureUrl,
            "role" to role,
            "languagePreference" to languagePreference,
            "createdAt" to (createdAt ?: Timestamp.now()),
            "updatedAt" to Timestamp.now(),
            "isActive" to isActive
        )
    }

    fun isAdmin() = role == "ADMIN"
    fun isPresident() = role == "PRESIDENT"
    fun isMember() = role == "MEMBER"
}