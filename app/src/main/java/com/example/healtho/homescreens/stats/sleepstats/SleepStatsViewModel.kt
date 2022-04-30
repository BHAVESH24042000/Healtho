package com.example.healtho.homescreens.stats.sleepstats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.auth.AuthRepo
import com.example.healtho.homescreens.SleepRepo
import com.example.healtho.homescreens.models.Sleep
import com.example.healtho.user.User
import com.example.healtho.util.*
import com.example.healtho.util.Objects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SleepStatsViewModel  @Inject constructor(
    private val authRepo: AuthRepo,
    private val sleepRepo: SleepRepo,
    private val sleepChartOrganizer: ChartDataOrganizer<Sleep>
) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepStatsScreenState())
    val uiState: StateFlow<SleepStatsScreenState> = _uiState

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    init {
        getUser()
        collectUserData()
        viewModelScope.launch {
            sleepChartOrganizer.setUp()
            collectSleepLogs()
        }
    }

    fun getUser() = viewModelScope.launch(Dispatchers.IO){
        _user.value = authRepo.getCurrentUser()
    }

    private fun collectUserData() = viewModelScope.launch {
        user.collect { user ->
        }
    }

    private fun collectSleepLogs() = viewModelScope.launch {
        sleepRepo.getAllSleepsOfLastWeek().collect { logs ->
            logs.forEach {
                val index = it.timeStamp?.let { it1 -> sleepChartOrganizer.findCalendarInstance(it1) }
                sleepChartOrganizer.add(it, index)
            }
            sleepChartOrganizer.sortData()
            prepareDataForCharts(logs)
        }
    }


    private fun calculatePercentage(logs: List<Sleep>, days: Int) = viewModelScope.launch {
        user.value?.let {
            val percentage = if (days != 0) {
                var totalSlept = 0
                for(i in logs){
                    totalSlept += i.sleepDuration!!
                }
                ((totalSlept.toFloat() / (it.sleepLimit?.times(days)!!)) * 100f).roundOff()
            } else 0f
            _uiState.emit(uiState.value.copy(weeklyPercentage = percentage))
        }
    }

    private suspend fun prepareDataForCharts(logs: List<Sleep>) = viewModelScope.launch {
        val barList = mutableListOf<Pair<String, Float>>()
        val lineList = mutableListOf<Pair<String, Float>>()
        var startDate = ""
        var endDate = ""
        var numberOfDaysSlept = 0
        var index = 0
        val length = sleepChartOrganizer.data.size
        sleepChartOrganizer.data.forEach {
            index++
            val day = Objects.DAYS.getDayFromNumber(it.key[Calendar.DAY_OF_WEEK])
            var totalSlept = 0
            for(i in it.value){
                totalSlept += i.sleepDuration!!
            }
            if (index == 1)
                startDate = it.key.getFormattedDate()
            else if (index == length)
                endDate = it.key.getFormattedDate()
            var dailyPercentage = (totalSlept.toFloat() / user.value!!.sleepLimit!!) * 100f
            dailyPercentage = dailyPercentage.roundOff()
            if (it.value.size > 0)
                numberOfDaysSlept++
            val pair = Pair(day.getShortFormFromNumber(), totalSlept.getHoursFromMinutes())
            val lineChartPair = Pair(day.getShortFormFromNumber(), dailyPercentage)
            barList.add(pair)
            lineList.add(lineChartPair)
        }
        lineList.reverse()
        val weekDate = "$endDate - $startDate"
        calculatePercentage(logs, numberOfDaysSlept)
        _uiState.emit(
            uiState.value.copy(
                barChartData = barList,
                lineChartData = lineList,
                weekDate = weekDate
            )
        )
    }
}

data class SleepStatsScreenState(
    val weeklyPercentage: Float = 0F,
    val expGained: Long = 0L,
    val weekDate: String = "",
    val barChartData: List<Pair<String, Float>> = mutableListOf(),
    val lineChartData: List<Pair<String, Float>> = mutableListOf()
)
