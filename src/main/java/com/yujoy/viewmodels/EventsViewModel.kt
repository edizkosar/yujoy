package com.yujoy.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yujoy.models.Event
import com.yujoy.repositories.EventsRepository
import com.yujoy.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

data class EventsState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class EventsViewModel(
    private val eventsRepository: EventsRepository = EventsRepository()
) : ViewModel() {

    private val _eventsState = MutableStateFlow(EventsState())
    val eventsState: StateFlow<EventsState> = _eventsState.asStateFlow()

    private var listenerJob: Job? = null
    private var isListening = false

    fun startListeningIfNeeded() {
        if (!isListening && listenerJob == null) {
            listenToEvents()
        }
    }

    fun loadEvents() {
        viewModelScope.launch {
            _eventsState.value = _eventsState.value.copy(
                isLoading = true,
                error = null
            )

            when (val result = eventsRepository.getAllEvents()) {
                is Result.Success -> {
                    _eventsState.value = EventsState(
                        isLoading = false,
                        events = result.data,
                        error = null
                    )
                }
                is Result.Error -> {
                    _eventsState.value = EventsState(
                        isLoading = false,
                        events = emptyList(),
                        error = result.message
                    )
                }
                Result.Loading -> {
                    _eventsState.value = _eventsState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun listenToEvents() {
        if (isListening) return

        listenerJob = viewModelScope.launch {
            isListening = true

            eventsRepository.getEventsFlow()
                .distinctUntilChanged()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _eventsState.value = EventsState(
                                isLoading = false,
                                events = result.data,
                                error = null
                            )
                        }
                        is Result.Error -> {
                            _eventsState.value = EventsState(
                                isLoading = false,
                                events = _eventsState.value.events,
                                error = result.message
                            )
                        }
                        Result.Loading -> {
                            _eventsState.value = _eventsState.value.copy(isLoading = true)
                        }
                    }
                }
        }
    }

    fun stopListening() {
        listenerJob?.cancel()
        listenerJob = null
        isListening = false
    }

    fun refreshEvents() {
        loadEvents()
    }

    fun registerToEvent(eventId: String) {
        viewModelScope.launch {
            _eventsState.value = _eventsState.value.copy(isLoading = true)

            when (val result = eventsRepository.registerToEvent(eventId)) {
                is Result.Success -> {
                    _eventsState.value = _eventsState.value.copy(
                        isLoading = false,
                        successMessage = "Successfully registered to event!"
                    )
                    if (!isListening) {
                        loadEvents()
                    }
                }
                is Result.Error -> {
                    _eventsState.value = _eventsState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                Result.Loading -> {}
            }
        }
    }

    fun unregisterFromEvent(eventId: String) {
        viewModelScope.launch {
            _eventsState.value = _eventsState.value.copy(isLoading = true)

            when (val result = eventsRepository.unregisterFromEvent(eventId)) {
                is Result.Success -> {
                    _eventsState.value = _eventsState.value.copy(
                        isLoading = false,
                        successMessage = "Successfully unregistered from event!"
                    )
                    if (!isListening) {
                        loadEvents()
                    }
                }
                is Result.Error -> {
                    _eventsState.value = _eventsState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _eventsState.value = _eventsState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _eventsState.value = _eventsState.value.copy(successMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}