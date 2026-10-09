package com.yujoy.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yujoy.models.Event
import com.yujoy.utils.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EventsRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val eventsCollection = firestore.collection("events")

    /**
     * Get all active events, ordered by event date
     */
    suspend fun getAllEvents(): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection
                .whereEqualTo("isActive", true)
                .orderBy("eventDate", Query.Direction.ASCENDING)
                .get()
                .await()

            val events = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Event::class.java)
            }

            Result.Success(events)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch events")
        }
    }

    /**
     * Get events as a Flow (real-time updates)
     */
    fun getEventsFlow(): Flow<Result<List<Event>>> = callbackFlow {
        val listener = eventsCollection
            .whereEqualTo("isActive", true)
            .orderBy("eventDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(error.message ?: "Failed to fetch events"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val events = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Event::class.java)
                    }
                    trySend(Result.Success(events))
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Get event by ID
     */
    suspend fun getEventById(eventId: String): Result<Event> {
        return try {
            val document = eventsCollection.document(eventId).get().await()
            val event = document.toObject(Event::class.java)
            if (event != null) {
                Result.Success(event)
            } else {
                Result.Error("Event not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch event")
        }
    }

    /**
     * Get events by club ID
     */
    suspend fun getEventsByClubId(clubId: String): Result<List<Event>> {
        return try {
            val snapshot = eventsCollection
                .whereEqualTo("clubId", clubId)
                .whereEqualTo("isActive", true)
                .orderBy("eventDate", Query.Direction.ASCENDING)
                .get()
                .await()

            val events = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Event::class.java)
            }

            Result.Success(events)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch events")
        }
    }

    /**
     * Create new event (for club presidents/admins)
     */
    suspend fun createEvent(event: Event): Result<String> {
        return try {
            val docRef = eventsCollection.add(event.toMap()).await()
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create event")
        }
    }

    /**
     * Update event
     */
    suspend fun updateEvent(eventId: String, event: Event): Result<Unit> {
        return try {
            eventsCollection.document(eventId).set(event.toMap()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update event")
        }
    }

    /**
     * Delete event (soft delete - set isActive to false)
     */
    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            eventsCollection.document(eventId)
                .update("isActive", false)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete event")
        }
    }

    /**
     * Register user to event (increment currentRegistrations)
     */
    suspend fun registerToEvent(eventId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val eventRef = eventsCollection.document(eventId)
                val snapshot = transaction.get(eventRef)
                val event = snapshot.toObject(Event::class.java)

                if (event != null && event.canRegister()) {
                    val newCount = event.currentRegistrations + 1
                    val newAvailable = event.capacity - newCount

                    transaction.update(eventRef, mapOf(
                        "currentRegistrations" to newCount,
                        "availableSpots" to newAvailable
                    ))
                } else {
                    throw Exception("Cannot register to this event")
                }
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to register to event")
        }
    }

    /**
     * Unregister user from event (decrement currentRegistrations)
     */
    suspend fun unregisterFromEvent(eventId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val eventRef = eventsCollection.document(eventId)
                val snapshot = transaction.get(eventRef)
                val event = snapshot.toObject(Event::class.java)

                if (event != null && event.currentRegistrations > 0) {
                    val newCount = event.currentRegistrations - 1
                    val newAvailable = event.capacity - newCount

                    transaction.update(eventRef, mapOf(
                        "currentRegistrations" to newCount,
                        "availableSpots" to newAvailable
                    ))
                }
            }.await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to unregister from event")
        }
    }
}