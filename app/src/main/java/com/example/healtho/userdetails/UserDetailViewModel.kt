package com.example.healtho.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.util.Objects
import com.example.healtho.util.Objects.USER_DETAILS_UPDATED
import com.example.healtho.util.Objects.USER_DETAILS_UPDATE_FAILED
import com.example.healtho.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserDetailViewModel @Inject constructor(private val userDetailRepo: UserDetailRepo) : ViewModel() {

    private val _usernameUiState = MutableStateFlow(GetUserNameScreenState())
    val usernameUiState: StateFlow<GetUserNameScreenState> = _usernameUiState

    private val _usernameEvents = MutableSharedFlow<GetUserNameScreenEvents>()
    val usernameEvents = _usernameEvents.asSharedFlow()

    fun onUserNameChange(username: String) = viewModelScope.launch {
        val isValid = username.isNotBlank() && username.isNotEmpty() && !usernameUiState.value.isLoading
        _usernameUiState.emit(_usernameUiState.value.copy(username = username, isNextButtonEnabled = isValid))
    }

    fun onUsernameNextButtonClicked() = viewModelScope.launch {
        _usernameUiState.emit(usernameUiState.value.copy(isLoading = true, isNextButtonEnabled = false))
        val resource = userDetailRepo.saveUserName(usernameUiState.value.username)
        _usernameUiState.emit(usernameUiState.value.copy(isLoading = false))
        if (resource is Resource.Success<*>)
            _usernameEvents.emit(GetUserNameScreenEvents.NavigateToNextScreen)
        else {
            _usernameUiState.emit(usernameUiState.value.copy(isNextButtonEnabled = true))
            usernameHandleError(resource as Resource.Error<Unit>)
        }
    }

    private fun usernameHandleError(resource: Resource.Error<Unit>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            Objects.ERROR_TYPE.NO_INTERNET -> GetUserNameScreenEvents.ShowNoInternetDialog
            Objects.ERROR_TYPE.UNKNOWN -> GetUserNameScreenEvents.ShowToast(resource.message)
        }
        _usernameEvents.emit(event)
    }

    val DEFAULT_WEIGHT = 40
    val weightList = (1..200).map { it.toString() }

    private val _userWeightUiState = MutableStateFlow(UserWeightScreenState())
    val userWeightUiState: StateFlow<UserWeightScreenState> = _userWeightUiState

    private val _userWeightEvents = MutableSharedFlow<UserWeightScreenEvents>()
    val userWeightEvents: SharedFlow<UserWeightScreenEvents> = _userWeightEvents


    fun onUserWeightNextButtonPressed(weight: String) = viewModelScope.launch {

        val weightint = weight.toIntOrNull()

        if(weightint != null) {
            val resource =
                weightint.let { userDetailRepo.saveUserWeight(weight = it) }
            _userWeightUiState.emit(
                _userWeightUiState.value.copy(
                    isLoading = false,
                    isButtonEnabled = true
                )
            )
            if (resource is Resource.Success) {
                _userWeightEvents.emit(UserWeightScreenEvents.ShowToast(USER_DETAILS_UPDATED))
                _userWeightEvents.emit(UserWeightScreenEvents.NavigateToNextScreen)
            } else {
                userWeighthandleError(resource as Resource.Error<Unit>)
            }
        }else{
            _userWeightEvents.emit(UserWeightScreenEvents.ShowToast("Please Write Weight In Integer Format"))
        }

    }

    private fun userWeighthandleError(resource: Resource.Error<Unit>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            Objects.ERROR_TYPE.NO_INTERNET -> UserWeightScreenEvents.ShowNoInternetDialog
            Objects.ERROR_TYPE.UNKNOWN -> UserWeightScreenEvents.ShowToast(resource.message)
        }
        _userWeightEvents.emit(event)
    }

    private val _userAgeUiState = MutableStateFlow(UserAgeScreenState())
    val userAgeUiState: StateFlow<UserAgeScreenState> = _userAgeUiState

    private val _userAgeEvents = MutableSharedFlow<UserAgeScreenEvents>()
    val userAgeEvents: SharedFlow<UserAgeScreenEvents> = _userAgeEvents

    fun onAgeChange(age: Int) = viewModelScope.launch {
        _userAgeUiState.emit(userAgeUiState.value.copy(age = age))
    }

    fun onContinueButtonPressed() = viewModelScope.launch {
        saveUserAge()
    }

    private suspend fun saveUserAge() {
        _userAgeUiState.emit(userAgeUiState.value.copy(isLoading = true, isButtonEnabled = false))
        val resource = userDetailRepo.saveUserAge(userAgeUiState.value.age)
        _userAgeUiState.emit(userAgeUiState.value.copy(isLoading = false))
        if (resource is Resource.Success) {
           userDetailRepo.saveUserDataEntryCompleted()
            _userAgeEvents.emit(UserAgeScreenEvents.ShowToast(USER_DETAILS_UPDATED))
            _userAgeEvents.emit(UserAgeScreenEvents.NavigateToHomeScreen)
        } else {
            _userAgeUiState.emit(userAgeUiState.value.copy(isButtonEnabled = true))
            userAgehandleError(resource as Resource.Error<Unit>)
        }
    }

    private fun userAgehandleError(resource: Resource.Error<Unit>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            Objects.ERROR_TYPE.NO_INTERNET -> UserAgeScreenEvents.ShowNoInternetDialog
            Objects.ERROR_TYPE.UNKNOWN -> {
                UserAgeScreenEvents.ShowToast(USER_DETAILS_UPDATE_FAILED)
            }
        }
        _userAgeEvents.emit(event)
    }


}

sealed class UserAgeScreenEvents {
    data class ShowToast(val message: String) : UserAgeScreenEvents()
    object NavigateToHomeScreen : UserAgeScreenEvents()
    object ShowNoInternetDialog : UserAgeScreenEvents()
}
data class UserAgeScreenState(
    val isLoading: Boolean = false,
    val age: Int = 40,
    val isButtonEnabled: Boolean = true
)
sealed class GetUserNameScreenEvents {
    object NavigateToNextScreen : GetUserNameScreenEvents()
    data class ShowToast(val message: String) : GetUserNameScreenEvents()
    object ShowNoInternetDialog : GetUserNameScreenEvents()
}
data class GetUserNameScreenState(
    val username: String = "",
    val isNextButtonEnabled: Boolean = false,
    val isLoading: Boolean = false
)
sealed class UserWeightScreenEvents {
    object NavigateToNextScreen : UserWeightScreenEvents()
    data class ShowToast(val message: String) : UserWeightScreenEvents()
    object ShowNoInternetDialog : UserWeightScreenEvents()
}
data class UserWeightScreenState(
    val isLoading: Boolean = false,
    val weight: Int = 40,
    val isButtonEnabled: Boolean = true
)
