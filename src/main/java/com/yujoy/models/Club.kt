package com.yujoy.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Club(
    @DocumentId
    val clubId: String = "",
    val name: String = "",
    val description: String = "",
    val coverImageUrl: String? = null,
    val logoUrl: String? = null,
    val presidentId: String? = null,
    val presidentName: String? = null, // Denormalized
    val category: String? = null,
    val memberCount: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "description" to description,
            "coverImageUrl" to coverImageUrl,
            "logoUrl" to logoUrl,
            "presidentId" to presidentId,
            "presidentName" to presidentName,
            "category" to category,
            "memberCount" to memberCount,
            "isActive" to isActive,
            "createdAt" to (createdAt ?: Timestamp.now()),
            "updatedAt" to Timestamp.now()
        )
    }
}