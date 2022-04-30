package com.example.healtho.homescreens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.auth.AuthRepo
import com.example.healtho.homescreens.SleepRepo
import com.example.healtho.homescreens.WaterRepo
import com.example.healtho.homescreens.WorkoutRepo
import com.example.healtho.user.User
import com.example.healtho.util.Objects
import com.example.healtho.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val sleepRepo: SleepRepo,
    private val waterRepo: WaterRepo,
    private val workoutRepo: WorkoutRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileScreenEvents>()
    val events = _events.asSharedFlow()

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    init {
        viewModelScope.launch {
            getUser()
            collectUserData()
        }
    }

    fun getUser() = viewModelScope.launch(Dispatchers.IO){
        _user.value = authRepo.getCurrentUser()
    }

    fun logoutUser(){

        viewModelScope.launch {
            waterRepo.deleteAllWaterLogs()
            sleepRepo.deleteAllSleepLogs()
            workoutRepo.deleteAllWorkoutLogs()
            authRepo.logoutUser()

        }

    }


    suspend fun collectUserData() {
        user.collect {
            it?.let { userData ->
                userData.age?.let { it1 ->
                    userData.weight?.let { it2 ->
                        uiState.value.copy(
                            username = userData.username,
                            profileImage = userData.profileImg,
                            age = it1,
                            weight = it2,
                            email = userData.email,
                        )
                    }
                }?.let { it2 ->
                    _uiState.emit(
                        it2
                    )
                }
            }
        }
    }



    private fun updateUserSleepLimit(limit: Int) = viewModelScope.launch {
        Timber.d("Editing sleep Limit in VM")
        _uiState.emit(uiState.value.copy(isLoading = true))
        val resource = authRepo.updateUserSleepLimit(limit)
        _uiState.emit(uiState.value.copy(isLoading = false))
        if (resource is Resource.Success<*>)
            _events.emit(ProfileScreenEvents.ShowToast("Updated successfully"))
        else
            handleError(resource = resource as Resource.Error<*>)
    }

    private fun updateUserWaterLimit(limit: Int) = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLoading = true))
        val resource = authRepo.updateUserWaterLimit(limit)
        _uiState.emit(uiState.value.copy(isLoading = false))
        if (resource is Resource.Success<*>)
            _events.emit(ProfileScreenEvents.ShowToast("Updated successfully"))
        else
            handleError(resource = resource as Resource.Error<*>)
    }

    fun onAboutPressed() = viewModelScope.launch {
        _events.emit(ProfileScreenEvents.NavigateToAboutScreen)
    }

    fun onLogoutPressed() = viewModelScope.launch {
        disableLogoutButton()
        _events.emit(
            ProfileScreenEvents.ShowLogoutDialog(
                "Confirm Logout",
                "Are you sure that you want to logout?"
            )
        )
    }

    fun onDialogClosed() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLogoutButtonEnabled = true))
    }

    fun onLogoutConfirmed() = viewModelScope.launch {
        _events.emit(ProfileScreenEvents.Logout)
    }

    fun onLogoutSuccess() = viewModelScope.launch {
        authRepo.logoutUser()
        _events.emit(ProfileScreenEvents.NavigateToAuthScreen)
    }

    fun disableLogoutButton() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLogoutButtonEnabled = false))
    }

    fun onLogoutFailed(exception: Exception) = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLogoutButtonEnabled = true))
        Timber.d(exception.toString())
        _events.emit(ProfileScreenEvents.ShowToast("Failed to logout"))
    }

    fun onLeaderBoardClicked() = viewModelScope.launch {
        _events.emit(ProfileScreenEvents.NavigateToLeaderBoardScreen)
    }

    fun editWaterLimitButtonState(isEnabled: Boolean) = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isEditWaterQuantityButtonEnabled = isEnabled))
    }

    fun editSleepLimitButtonState(isEnabled: Boolean) = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isEditSleepLimitButtonEnabled = isEnabled))
    }

    fun onEditWaterLimitPressed() = viewModelScope.launch {
        editWaterLimitButtonState(false)
        _events.emit(
            ProfileScreenEvents.OpenWaterLimitDialog(
                onQuantitySelected = {
                    updateUserWaterLimit(it)
                },
                onDismiss = {
                    editWaterLimitButtonState(true)
                }
            )
        )
    }

    fun onEditSleepLimitPressed() = viewModelScope.launch {
        editSleepLimitButtonState(false)
        _events.emit(
            ProfileScreenEvents.OpenSleepLimitDialog(
                onTimeSelected = {
                    updateUserSleepLimit(it)
                },
                onDismiss = {
                    editSleepLimitButtonState(true)
                }
            )
        )
    }

    private fun handleError(resource: Resource.Error<*>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            Objects.ERROR_TYPE.NO_INTERNET -> ProfileScreenEvents.ShowNoInternetDialog
            Objects.ERROR_TYPE.UNKNOWN -> ProfileScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }

}


data class ProfileScreenState(
    val username: String = "",
    val profileImage: String = "",
    val age: Int = 0,
    val weight: Int = 0,
    val email: String = "",
    val isLoading: Boolean = false,
    val isLogoutButtonEnabled: Boolean = true,
    val isEditSleepLimitButtonEnabled: Boolean = true,
    val isEditWaterQuantityButtonEnabled: Boolean = true
)
sealed class ProfileScreenEvents {
    object NavigateToAboutScreen : ProfileScreenEvents()
    data class ShowLogoutDialog(
        val title: String,
        val description: String
    ) : ProfileScreenEvents()
    data class ShowToast(val message: String) : ProfileScreenEvents()
    object ShowNoInternetDialog : ProfileScreenEvents()
    object Logout : ProfileScreenEvents()
    object NavigateToAuthScreen : ProfileScreenEvents()
    object NavigateToLeaderBoardScreen : ProfileScreenEvents()
    data class OpenSleepLimitDialog(
        val onTimeSelected: (Int) -> Unit,
        val onDismiss: () -> Unit
    ) : ProfileScreenEvents()
    data class OpenWaterLimitDialog(
        val onQuantitySelected: (Int) -> Unit,
        val onDismiss: () -> Unit
    ) : ProfileScreenEvents()
}
