package com.yujoy.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yujoy.models.User
import com.yujoy.repositories.AuthRepository
import com.yujoy.repositories.ImageRepository
import com.yujoy.repositories.UserRepositories
import com.yujoy.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepositories = UserRepositories(),
    private val imageRepository: ImageRepository = ImageRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            when (val result = authRepository.getCurrentUserData()) {
                is Result.Success -> {
                    _authState.value = AuthState(
                        isLoggedIn = true,
                        user = result.data
                    )
                }
                is Result.Error -> {
                    _authState.value = AuthState(
                        isLoggedIn = false,
                        error = null
                    )
                }
                Result.Loading -> {
                    _authState.value = AuthState(isLoading = true)
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    _authState.value = AuthState(
                        isLoggedIn = true,
                        user = result.data,
                        successMessage = "Login successful"
                    )
                }
                is Result.Error -> {
                    _authState.value = AuthState(
                        isLoading = false,
                        isLoggedIn = false,
                        error = result.message
                    )
                }
                Result.Loading -> {
                    _authState.value = _authState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        username: String
    ) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.register(email, password, firstName, lastName, username)) {
                is Result.Success -> {
                    _authState.value = AuthState(
                        isLoggedIn = true,
                        user = result.data,
                        successMessage = "Registration successful"
                    )
                }
                is Result.Error -> {
                    _authState.value = AuthState(
                        isLoading = false,
                        isLoggedIn = false,
                        error = result.message
                    )
                }
                Result.Loading -> {
                    _authState.value = _authState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.resetPassword(email)) {
                is Result.Success -> {
                    _authState.value = AuthState(
                        isLoading = false,
                        successMessage = "Password reset email sent"
                    )
                }
                is Result.Error -> {
                    _authState.value = AuthState(
                        isLoading = false,
                        error = result.message
                    )
                }
                Result.Loading -> {
                    _authState.value = _authState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _authState.value = AuthState(
            isLoggedIn = false,
            user = null
        )
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _authState.value = _authState.value.copy(successMessage = null)
    }

    fun updateProfile(firstName: String, lastName: String, username: String) {
        viewModelScope.launch {
            val userId = authState.value.user?.userId ?: return@launch
            _authState.value = _authState.value.copy(isLoading = true)

            when (val result = authRepository.updateUserProfile(userId, firstName, lastName, username)) {
                is Result.Success -> {
                    when (val userResult = authRepository.getCurrentUserData()) {
                        is Result.Success -> {
                            _authState.value = AuthState(
                                isLoggedIn = true,
                                user = userResult.data,
                                successMessage = "Profile updated successfully"
                            )
                        }
                        is Result.Error -> {
                            _authState.value = AuthState(
                                isLoggedIn = true,
                                user = authState.value.user,
                                error = userResult.message
                            )
                        }
                        Result.Loading -> {}
                    }
                }
                is Result.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                Result.Loading -> {}
            }
        }
    }

    fun uploadProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            val userId = authState.value.user?.userId ?: return@launch
            _authState.value = _authState.value.copy(isLoading = true)

            // 1. Upload image to Storage
            when (val uploadResult = imageRepository.uploadProfilePicture(userId, imageUri)) {
                is Result.Success -> {
                    val profilePictureUrl = uploadResult.data

                    // 2. Update Firestore with new URL
                    when (val updateResult = userRepository.updateProfilePicture(userId, profilePictureUrl)) {
                        is Result.Success -> {
                            // 3. Refresh user data
                            when (val userResult = authRepository.getCurrentUserData()) {
                                is Result.Success -> {
                                    _authState.value = AuthState(
                                        isLoggedIn = true,
                                        user = userResult.data,
                                        successMessage = "Profile picture updated successfully"
                                    )
                                }
                                is Result.Error -> {
                                    _authState.value = _authState.value.copy(
                                        isLoading = false,
                                        error = userResult.message
                                    )
                                }
                                Result.Loading -> {}
                            }
                        }
                        is Result.Error -> {
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                error = updateResult.message
                            )
                        }
                        Result.Loading -> {}
                    }
                }
                is Result.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = uploadResult.message
                    )
                }
                Result.Loading -> {}
            }
        }
    }
}