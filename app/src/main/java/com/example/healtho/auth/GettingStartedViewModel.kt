package com.example.healtho.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.user.User
import com.example.healtho.user.UserProfile
import com.example.healtho.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class GettingStartedScreenState(
    val isLoading: Boolean = false,
    val isButtonEnabled: Boolean = true
)

sealed class GettingStartedScreenEvents {
    data class ShowToast(val message: String) : GettingStartedScreenEvents()
    object NavigateToUserDetailsScreen : GettingStartedScreenEvents()
    object NavigateToHomeScreen : GettingStartedScreenEvents()
    object Logout : GettingStartedScreenEvents()
}

@HiltViewModel
class GettingStartedViewModel @Inject constructor(private val authRepo: AuthRepo) : ViewModel() {

    companion object {
        private const val FAILED_TO_LOGIN = "Failed to log you in"
        private const val LOGIN_SUCCESS = "User logged in successfully"
        private const val REGISTER_SUCCESS = "User registered successfully"
    }

    private val user = MutableStateFlow<UserProfile?>(null)

    private val _uiState = MutableStateFlow(GettingStartedScreenState())
    val uiState: StateFlow<GettingStartedScreenState> = _uiState

    private val _events = MutableSharedFlow<GettingStartedScreenEvents>()
    val events: SharedFlow<GettingStartedScreenEvents> = _events

    private var _userLoggedIn = MutableStateFlow<User?>(null)
    var userLoggedIn: StateFlow<User?> = _userLoggedIn

    fun getUser() = viewModelScope.launch(Dispatchers.IO){
        _userLoggedIn.emit( authRepo.getCurrentUser())
    }


    fun saveUser(userProfile: UserProfile) = viewModelScope.launch {
        user.emit(userProfile)
    }

    fun startLoading() = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(isLoading = true, isButtonEnabled = false))
    }

     fun stopLoading() = viewModelScope.launch {
        _uiState.emit(_uiState.value.copy(isLoading = false, isButtonEnabled = true))
    }

    fun sendError(message: String) = viewModelScope.launch {
        stopLoading()
        _events.emit(GettingStartedScreenEvents.ShowToast(message))
    }



     fun tryLogin( email: String, password: String) = viewModelScope.launch {
         val isUserAlreadyRegistered = authRepo.isUserRegistered(email, password)
         if (isUserAlreadyRegistered is Resource.Error) {
             sendError(isUserAlreadyRegistered.message)
             _events.emit(GettingStartedScreenEvents.Logout)
             return@launch
         } else if( isUserAlreadyRegistered is Resource.Success && isUserAlreadyRegistered.data == false){
             sendError("User Not Registered")
             _events.emit(GettingStartedScreenEvents.Logout)
             return@launch
         } else {
             loginComplete(email, password)
         }
     }


    private fun loginComplete(email: String, password: String) = viewModelScope.launch {
        val data = authRepo.continueAfterCheckingLogin(email, password)

        if (data is Resource.Error<*>) {
            sendError(data.message)
            _events.emit(GettingStartedScreenEvents.Logout)
        } else if (data is Resource.Success<*> && data.message == "User Details Fetched Success") {
            //saveUser(data as UserProfile)
            _events.emit(GettingStartedScreenEvents.ShowToast(LOGIN_SUCCESS))
            authRepo.saveUserDataEntryCompleted()
            stopLoading()
            _events.emit(GettingStartedScreenEvents.NavigateToHomeScreen)
        } else {
            sendError(data as String)
            _events.emit(GettingStartedScreenEvents.Logout)
        }
    }

    fun tryRegister(email: String, password: String) = viewModelScope.launch {
        val isUserAlreadyRegistered = authRepo.isUserRegistered(email, password)
        if (isUserAlreadyRegistered is Resource.Error) {

            if(isUserAlreadyRegistered.message == "Incorrect Email/Password")
                sendError("User Already Registered")
            else
            sendError(isUserAlreadyRegistered.message)

            _events.emit(GettingStartedScreenEvents.Logout)
            return@launch
        }else if( isUserAlreadyRegistered is Resource.Success && isUserAlreadyRegistered.data == false){
            registerComplete(email, password)
        } else{

            if(isUserAlreadyRegistered.message == "Success.")
                sendError("User Already Registered")
            else
            sendError(isUserAlreadyRegistered.message)

            _events.emit(GettingStartedScreenEvents.Logout)
            return@launch
        }
    }

    private fun registerComplete(email: String, password: String) = viewModelScope.launch{
        val data = authRepo.continueAfterCheckingRegister(email, password)

        if(data is Resource.Success<*>  && data.message == "Registration Success"){
            //saveUser(data.data as UserProfile)
            _events.emit(GettingStartedScreenEvents.ShowToast(REGISTER_SUCCESS))
            authRepo.saveUserDataEntryCompleted()
            stopLoading()
            _events.emit(GettingStartedScreenEvents.NavigateToUserDetailsScreen)
        }else if(data is Resource.Error<*>){
            sendError(data.message)
            _events.emit(GettingStartedScreenEvents.Logout)
        }else{
            sendError("Some Error Occured. Try Again After Some Time")
            _events.emit(GettingStartedScreenEvents.Logout)
        }
    }

    fun logoutFailed() = viewModelScope.launch {
        authRepo.logoutUser()
        _events.emit(GettingStartedScreenEvents.ShowToast(FAILED_TO_LOGIN))
    }

    fun logoutComplete() = viewModelScope.launch {
        authRepo.logoutUser()
        //_events.emit(GettingStartedScreenEvents.ShowToast(FAILED_TO_LOGIN))
    }

    private suspend fun isUserLoggedIn() = authRepo.isUserLoggedIn()
}
