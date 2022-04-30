package com.example.healtho.homescreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.util.Objects
import com.example.healtho.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val waterRepo: WaterRepo,
    private val sleepRepo: SleepRepo,
) : ViewModel() {

    private val _events = MutableStateFlow<MainActivityScreenEvents>(MainActivityScreenEvents.Empty)
    val events: StateFlow<MainActivityScreenEvents> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadAllWaterLogs()
        loadAllSleepLogs()
    }

    private fun loadAllWaterLogs() = viewModelScope.launch {
        startLoading()
        val resource = waterRepo.fetchAllWaterLogs()
        stopLoading()
        if (resource is Resource.Error<*>)
            handleError(resource)
    }

    private fun loadAllSleepLogs() = viewModelScope.launch {
        startLoading()
        val resource = sleepRepo.fetchAllSleepLogs()
        stopLoading()
        if (resource is Resource.Error<*>)
            handleError(resource)
    }

    private suspend fun stopLoading() {
        _isLoading.emit(false)
    }

    private suspend fun startLoading() {
        _isLoading.emit(true)
    }

    private suspend fun handleError(resource: Resource.Error<*>) {
        val event = when (resource.errorType) {
            Objects.ERROR_TYPE.NO_INTERNET -> MainActivityScreenEvents.ShowNoInternetDialog
            Objects.ERROR_TYPE.UNKNOWN -> MainActivityScreenEvents.ShowToast(resource.message)
        }
        _events.emit(event)
    }
}
sealed class MainActivityScreenEvents {
    data class ShowToast(val message: String) : MainActivityScreenEvents()
    object ShowNoInternetDialog : MainActivityScreenEvents()
    object Empty : MainActivityScreenEvents()
}
