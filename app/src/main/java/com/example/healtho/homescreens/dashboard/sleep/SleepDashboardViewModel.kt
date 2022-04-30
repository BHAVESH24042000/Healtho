package com.example.healtho.homescreens.dashboard.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.auth.AuthRepo
import com.example.healtho.homescreens.SleepRepo

import com.example.healtho.homescreens.models.Sleep
import com.example.healtho.user.User
import com.example.healtho.util.Objects
import com.example.healtho.util.Objects.SLEEP_EXP
import com.example.healtho.util.Objects.getGreeting
import com.example.healtho.util.Objects.getMainGreeting
import com.example.healtho.util.Resource
import com.example.healtho.util.getHoursFromMinutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SleepDashboardViewModel  @Inject constructor(
    private val sleepRepo: SleepRepo,
    private val authRepo: AuthRepo
) :ViewModel() {

    private val _uiState = MutableStateFlow(SleepDashboardScreenState())
    val uiState: StateFlow<SleepDashboardScreenState> = _uiState

    private val _events = MutableSharedFlow<SleepDashboardScreenEvents>()
    val events: SharedFlow<SleepDashboardScreenEvents> = _events

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    init {
        getUser()
        collectUserData()
        collectSleepLogs()
        getFotd()
    }

    fun getUser() = viewModelScope.launch(Dispatchers.IO){
        _user.value = authRepo.getCurrentUser()
    }


    private fun getFotd() = viewModelScope.launch {
        val fotd = sleepRepo.getFOTD()
        _uiState.emit(uiState.value.copy(factOfTheDay = fotd))
    }

    fun onAddSleepPressed() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isAddSleepButtonEnabled = false))
        _events.emit(SleepDashboardScreenEvents.OpenAddSleepDialog)
    }

    private fun startLoading() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLoading = true))
    }

    private fun stopLoading() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLoading = false))
    }

    fun onDialogClosed() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isAddSleepButtonEnabled = true))
    }

    fun onSleepSelected(sleepDuration: Int) = viewModelScope.launch {
        addSleep(sleepDuration)

    }

    private suspend fun addSleep(sleepDuration: Int) {
        startLoading()
        val resource = sleepRepo.insertIntoSleepLog(getSleepModelClass(sleepDuration))
        stopLoading()
        if (resource is Resource.Error)
            handleError(resource)
    }



    private fun collectUserData() = viewModelScope.launch {
        user.collect { user ->
            user?.let {
                it.sleepLimit?.let { it1 ->
                    uiState.value.copy(
                        username = it.username,
                        totalAmount = it1.getHoursFromMinutes(),
                    )
                }?.let { it2 ->
                    _uiState.emit(
                        it2
                    )
                }
            }
        }
    }

    private fun collectSleepLogs() = viewModelScope.launch {
        sleepRepo.getTodaysSleepLogs().collect { sleepLogs ->
//            val totalSlept = sleepLogs.sumOf { sleep ->
//                sleep.sleepDuration
//            }

            var totalSlept : Long= 0L
            for( i in sleepLogs){
                if( i.sleepDuration!=null)
                totalSlept += i.sleepDuration
            }

            user.value?.let {
                val progress = (totalSlept.toFloat() / it.sleepLimit!!) * 100f
                _uiState.emit(
                    uiState.value.copy(
                        sleepLog = sleepLogs,
                        completedAmount = totalSlept.toInt().getHoursFromMinutes(),
                        progress = progress,
                        greeting = getGreeting(progress),
                        mainGreeting = getMainGreeting(progress)
                    )
                )
            }
        }
    }

    private fun getSleepModelClass(minutes: Int) = Sleep(
        sleepDuration = minutes,
        timeStamp = System.currentTimeMillis()
    )

    private fun handleError(resource: Resource.Error<*>) = viewModelScope.launch {
        val event = when (resource.errorType) {
            Objects.ERROR_TYPE.NO_INTERNET -> SleepDashboardScreenEvents.ShowNoInternetDialog
            Objects.ERROR_TYPE.UNKNOWN -> SleepDashboardScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }
}

data class SleepDashboardScreenState(
    val username: String = "",
    val mainGreeting: String = "",
    val greeting: String = "",
    val completedAmount: Float = 0F,
    val totalAmount: Float = 0F,
    val progress: Float = 0F,
    val factOfTheDay: String = "",
    val isLoading: Boolean = false,
    val isAddSleepButtonEnabled: Boolean = true,
    val sleepLog: List<Sleep> = emptyList()
)
sealed class SleepDashboardScreenEvents {
    data class ShowToast(val message: String) : SleepDashboardScreenEvents()
    object OpenAddSleepDialog : SleepDashboardScreenEvents()
    object ShowNoInternetDialog : SleepDashboardScreenEvents()
}
