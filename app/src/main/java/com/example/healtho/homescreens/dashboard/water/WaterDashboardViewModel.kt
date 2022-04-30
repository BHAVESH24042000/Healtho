package com.example.healtho.homescreens.dashboard.water

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.auth.AuthRepo
import com.example.healtho.homescreens.WaterRepo
import com.example.healtho.homescreens.models.Water
import com.example.healtho.user.User
import com.example.healtho.util.Objects
import com.example.healtho.util.Objects.WATER_EXP
import com.example.healtho.util.Objects.getGreeting
import com.example.healtho.util.Objects.getMainGreeting
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
class WaterDashboardViewModel @Inject constructor(
    private val waterRepo: WaterRepo,
    private val authRepo: AuthRepo
):ViewModel() {

    private val _uiState = MutableStateFlow(WaterDashboardScreenState())
    val uiState: StateFlow<WaterDashboardScreenState> = _uiState

    private val _events = MutableSharedFlow<WaterDashboardScreenEvents>()
    val events: SharedFlow<WaterDashboardScreenEvents> = _events

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    init {
        getUser()
        collectUserData()
        collectWaterLogs()
        getFotd()
    }
    fun getUser() = viewModelScope.launch(Dispatchers.IO){
        _user.value = authRepo.getCurrentUser()
    }

    private fun getFotd() = viewModelScope.launch {
        val fotd = waterRepo.getFOTD()
        _uiState.emit(uiState.value.copy(factOfTheDay = fotd))
    }
    private fun collectUserData() = viewModelScope.launch {
        _user.collect { user ->
            user?.let {
                it.waterLimit?.let { it1 ->
                    uiState.value.copy(
                        username = it.username.substringBefore(' ', it.username),
                        totalAmount = it1,
                    )
                }?.let { it2 ->
                    _uiState.emit(
                        it2
                    )
                }
            }
        }
    }

    private fun collectWaterLogs() = viewModelScope.launch {
        waterRepo.getTodaysWaterLogs().collect { waterLogs ->

            var totalDrinked = 0
            for( i in waterLogs){
                totalDrinked = totalDrinked + i.quantity.toInt()
            }
            _user.value?.let {
                val progress = totalDrinked.toFloat() / _user.value!!.waterLimit!! * 100f
                _uiState.emit(
                    uiState.value.copy(
                        waterLog = waterLogs,
                        completedAmount = totalDrinked,
                        progress = progress,
                        greeting = getGreeting(progress),
                        mainGreeting = getMainGreeting(progress)
                    )
                )
            }
        }
    }

    fun onAddWaterPressed() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isAddWaterButtonEnabled = false))
        _events.emit(WaterDashboardScreenEvents.OpenAddWaterDialog)
    }

    fun onWaterSelected(water: Objects.WATER) = viewModelScope.launch {
        addWater(water)
    }

    private suspend fun addWater(water: Objects.WATER) {
        startLoading()
        val resource = waterRepo.insertIntoWaterLog(getWaterModelClass(water))
        stopLoading()
        if (resource is Resource.Error<*>)
            handleError(resource)
        else
            _events.emit(WaterDashboardScreenEvents.CreateAlarm)
    }


    private fun getWaterModelClass(water: Objects.WATER) = Water(
        quantity = water.quantity.toString(),
        timeStamp = System.currentTimeMillis()
    )

    private fun startLoading() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLoading = true))
    }

    private fun stopLoading() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLoading = false))
    }

    fun onDialogClosed() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isAddWaterButtonEnabled = true))
    }


    private suspend fun handleError(resource: Resource.Error<*>) {
        val event = when (resource.errorType) {
            Objects.ERROR_TYPE.NO_INTERNET -> WaterDashboardScreenEvents.ShowNoInternetDialog
            Objects.ERROR_TYPE.UNKNOWN -> WaterDashboardScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }
}

sealed class WaterDashboardScreenEvents {
    data class ShowToast(val message: String) : WaterDashboardScreenEvents()
    object OpenAddWaterDialog : WaterDashboardScreenEvents()
    object CreateAlarm : WaterDashboardScreenEvents()
    object ShowNoInternetDialog : WaterDashboardScreenEvents()
}
data class WaterDashboardScreenState(
    val username: String = "",
    val mainGreeting: String = "",
    val greeting: String = "",
    val completedAmount: Int = 0,
    val totalAmount: Int = 0,
    val progress: Float = 0F,
    val factOfTheDay: String = "",
    val isLoading: Boolean = false,
    val isAddWaterButtonEnabled: Boolean = true,
    val waterLog: List<Water> = emptyList()
)

