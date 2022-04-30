package com.example.healtho.homescreens.stats.waterstats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.auth.AuthRepo
import com.example.healtho.homescreens.WaterRepo
import com.example.healtho.homescreens.models.Water
import com.example.healtho.user.User
import com.example.healtho.util.ChartDataOrganizer
import com.example.healtho.util.Objects
import com.example.healtho.util.Objects.WATER_EXP
import com.example.healtho.util.getFormattedDate
import com.example.healtho.util.roundOff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WaterStatsViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val waterRepo: WaterRepo,
    private val waterChartDataOrganizer: ChartDataOrganizer<Water>
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaterStatsScreenState())
    val uiState: StateFlow<WaterStatsScreenState> = _uiState

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    init {
        getUser()
        collectUserData()
        viewModelScope.launch {
            waterChartDataOrganizer.setUp()
            collectWaterLogs()
        }
    }

    fun getUser() = viewModelScope.launch(Dispatchers.IO){
        _user.value = authRepo.getCurrentUser()
    }

    private fun collectUserData() = viewModelScope.launch {
        user.collect { user ->
        }
    }

    private fun collectWaterLogs() = viewModelScope.launch {
        waterRepo.getAllWaterLogsOfLastWeek().collect { logs ->
            logs.forEach {
                val index = waterChartDataOrganizer.findCalendarInstance(it.timeStamp)
                waterChartDataOrganizer.add(it, index)
            }
            waterChartDataOrganizer.sortData()
            //calculateExp(logs)
            prepareDataForCharts(logs)
        }
    }



    private fun calculatePercentage(logs: List<Water>, days: Int) = viewModelScope.launch {
        user.value?.let {
            val percentage = if (days != 0) {
                var totalAmountDrank = 0
                for( i in logs){
                    totalAmountDrank = totalAmountDrank+ i.quantity.toInt()
                }
                ((totalAmountDrank.toFloat() / (it.waterLimit?.times(days)!!)) * 100f).roundOff()
            } else 0f

            _uiState.emit(uiState.value.copy(weeklyPercentage = percentage))
        }
    }

    private suspend fun prepareDataForCharts(logs: List<Water>) = viewModelScope.launch {
        val barList = mutableListOf<Pair<String, Float>>()
        val lineList = mutableListOf<Pair<String, Float>>()
        var startDate = ""
        var endDate = ""
        var numberOfDaysDrank = 0
        var index = 0
        val length = waterChartDataOrganizer.data.size
        waterChartDataOrganizer.data.forEach {
            index++
            val day = Objects.DAYS.getDayFromNumber(it.key[Calendar.DAY_OF_WEEK])
//            val totalAmountDrank = it.value.sumOf { water ->
//                water.quantity.quantity
//            }

            var totalAmountDrank = 0
            for( i in it.value){
                totalAmountDrank = totalAmountDrank+ i.quantity.toInt()
            }

            if (index == 1)
                startDate = it.key.getFormattedDate()
            else if (index == length)
                endDate = it.key.getFormattedDate()
            var dailyPercentage = (totalAmountDrank.toFloat() / user.value!!.waterLimit!!) * 100f
            dailyPercentage = dailyPercentage.roundOff()
            if (it.value.size > 0)
                numberOfDaysDrank++
            val pair = Pair(day.getShortFormFromNumber(), totalAmountDrank.toFloat())
            val lineChartPair = Pair(day.getShortFormFromNumber(), dailyPercentage)
            barList.add(pair)
            lineList.add(lineChartPair)
        }
        lineList.reverse()
        val weekDate = "$endDate - $startDate"
        calculatePercentage(logs, numberOfDaysDrank)
        _uiState.emit(
            uiState.value.copy(
                barChartData = barList,
                lineChartData = lineList,
                weekDate = weekDate
            )
        )
    }
}


data class WaterStatsScreenState(
    val username : String = "",
    val weeklyPercentage: Float = 0F,
    val expGained: Long = 0L,
    val weekDate: String = "",
    val barChartData: List<Pair<String, Float>> = mutableListOf(),
    val lineChartData: List<Pair<String, Float>> = mutableListOf()
)
