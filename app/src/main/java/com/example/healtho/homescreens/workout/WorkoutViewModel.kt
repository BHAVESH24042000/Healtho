package com.example.healtho.homescreens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.auth.AuthRepo
import com.example.healtho.homescreens.WaterRepo
import com.example.healtho.homescreens.WorkoutRepo
import com.example.healtho.homescreens.dashboard.water.WaterDashboardScreenState
import com.example.healtho.homescreens.models.Water
import com.example.healtho.homescreens.models.Workout
import com.example.healtho.user.User
import com.example.healtho.util.Objects
import com.example.healtho.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepo: WorkoutRepo,
    private val authRepo: AuthRepo
):ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutDashboardScreenState())
    val uiState: StateFlow<WorkoutDashboardScreenState> = _uiState

    private val _events = MutableSharedFlow<WorkoutDashboardScreenEvents>()
    val events: SharedFlow<WorkoutDashboardScreenEvents> = _events

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    init {
        getUser()
        collectUserData()
        loadAllWorkoutLogs()
        collectWorkoutLogs()
        //getFotd()
    }
    fun getUser() = viewModelScope.launch(Dispatchers.IO){
        _user.value = authRepo.getCurrentUser()
    }

//    private fun getFotd() = viewModelScope.launch {
//        val fotd = waterRepo.getFOTD()
//        _uiState.emit(uiState.value.copy(factOfTheDay = fotd))
//    }
    private fun collectUserData() = viewModelScope.launch {
        _user.collect { user ->
            user?.let {
               _uiState.emit(
                   uiState.value.copy(
                       username = it.username
                   )
               )
            }
        }
    }

    private fun loadAllWorkoutLogs() = viewModelScope.launch {
        startLoading()
        val resource = workoutRepo.fetchAllWorkoutLogs()
        stopLoading()
        if (resource is Resource.Error<*>)
            handleError(resource)
    }


    private fun collectWorkoutLogs() = viewModelScope.launch {
        workoutRepo.getTodaysWorkoutLogs().collect { workoutLogs ->
            _user.value?.let {
                var progress : Float = 0f
                if(workoutLogs!=null) {
                    progress = 100f
                }
                _uiState.emit(
                    uiState.value.copy(
                        progress = progress,
                        greeting = Objects.getGreeting(progress),
                        mainGreeting = Objects.getMainGreeting(progress)
                    )
                )
            }
        }
    }



    fun onWorkoutCompleted() = viewModelScope.launch {
        user.value?.email?.let {
            Workout(
                userEmail = it,
                timeStamp = System.currentTimeMillis()
            )
        }?.let { addWorkout(it) }
    }

    private suspend fun addWorkout(workout: Workout) {
        startLoading()
        val resource = workoutRepo.insertIntoWorkoutLog(workout)
        stopLoading()
        if (resource is Resource.Error<*>)
            handleError(resource)
        else
            _events.emit(WorkoutDashboardScreenEvents.ShowToast("Workout Added Successfully"))
    }

    private fun startLoading() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLoading = true))
    }

    private fun stopLoading() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLoading = false))
    }


    private suspend fun handleError(resource: Resource.Error<*>) {
        val event = when (resource.errorType) {
            Objects.ERROR_TYPE.NO_INTERNET -> WorkoutDashboardScreenEvents.ShowNoInternetDialog
            Objects.ERROR_TYPE.UNKNOWN -> WorkoutDashboardScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }
}

sealed class WorkoutDashboardScreenEvents {
    data class ShowToast(val message: String) : WorkoutDashboardScreenEvents()
    object ShowNoInternetDialog : WorkoutDashboardScreenEvents()
}

data class WorkoutDashboardScreenState(
    var username: String = "",
    var mainGreeting: String = "",
    var greeting: String = "",
    var progress: Float = 0F,
    var factOfTheDay: String = "",
    var isLoading: Boolean = false,
    var workoutLog: List<Workout> = emptyList()
)
