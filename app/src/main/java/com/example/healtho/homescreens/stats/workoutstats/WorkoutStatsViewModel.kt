package com.example.healtho.homescreens.stats.workoutstats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healtho.auth.AuthRepo
import com.example.healtho.homescreens.WorkoutRepo
import com.example.healtho.homescreens.models.Workout
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
class WorkoutStatsViewModel @Inject constructor(private val authRepo: AuthRepo,
                                                private val workoutRepo: WorkoutRepo,
                                                private val workoutChartOrganizer: ChartDataOrganizer<Workout>
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutStatsScreenState())
    val uiState: StateFlow<WorkoutStatsScreenState> = _uiState

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    init {
        getUser()
        collectUserData()
        viewModelScope.launch {
            workoutRepo.fetchAllWorkoutLogs()
            workoutChartOrganizer.setUp()
            collectWorkoutLogs()
        }
    }

    fun getUser() = viewModelScope.launch(Dispatchers.IO){
        _user.value = authRepo.getCurrentUser()
    }

    private fun collectUserData() = viewModelScope.launch {
        user.collect { user ->
        }
    }

    private fun collectWorkoutLogs() = viewModelScope.launch {
        workoutRepo.getLastWeekWorkoutLogs().collect { logs ->
            logs.forEach {
                val index = it.timeStamp.let { it1 -> workoutChartOrganizer.findCalendarInstance(it1) }
                Log.i("prepareDataForCharts0", "$index " + " $it ")
                workoutChartOrganizer.add(it, index)
            }
            workoutChartOrganizer.sortData()
            prepareDataForCharts(logs)
        }
    }


    private fun calculatePercentage(logs: List<Workout>, days: Int) = viewModelScope.launch {
        user.value?.let {

            Log.i("prepareDataForCharts4", "${days}")
            val percentage = ((days.toFloat()/7f) * 100f).roundOff()
            Log.i("prepareDataForCharts4", "${percentage}")
            _uiState.emit(uiState.value.copy(weeklyPercentage = percentage))
        }
    }

    private suspend fun prepareDataForCharts(logs: List<Workout>) = viewModelScope.launch {

        Log.i("prepareDataForCharts1", "$logs")

        Log.i("prepareDataForCharts3", "${workoutChartOrganizer.data.size}")
        for(i in workoutChartOrganizer.data)
        Log.i("prepareDataForCharts2", "${i.value}")

        val barList = mutableListOf<Pair<String, Float>>()
        val lineList = mutableListOf<Pair<String, Float>>()
        var startDate = ""
        var endDate = ""
        var numberOfDaysWorkout = 0
        var index = 0
        val length = workoutChartOrganizer.data.size
        workoutChartOrganizer.data.forEach {
            index++
            val day = Objects.DAYS.getDayFromNumber(it.key[Calendar.DAY_OF_WEEK])
            var totalWorkoutDay = 0
            for(i in it.value){
                totalWorkoutDay++
            }
            if (index == 1)
                startDate = it.key.getFormattedDate()
            else if (index == length)
                endDate = it.key.getFormattedDate()

            var dailyPercentage = (totalWorkoutDay) * 100f
            dailyPercentage = dailyPercentage.roundOff()
            if (it.value.size > 0)
                numberOfDaysWorkout++
            val pair = Pair(day.getShortFormFromNumber(), totalWorkoutDay.toFloat())
            val lineChartPair = Pair(day.getShortFormFromNumber(), dailyPercentage)
            barList.add(pair)
            lineList.add(lineChartPair)
        }
        lineList.reverse()
        val weekDate = "$endDate - $startDate"
        Log.i("prepareDataForCharts4", "${numberOfDaysWorkout}")
        calculatePercentage(logs, numberOfDaysWorkout)
        _uiState.emit(
            uiState.value.copy(
                barChartData = barList,
                lineChartData = lineList,
                weekDate = weekDate
            )
        )
    }
}

data class WorkoutStatsScreenState(
    val weeklyPercentage: Float = 0F,
    val expGained: Long = 0L,
    val weekDate: String = "",
    val barChartData: List<Pair<String, Float>> = mutableListOf(),
    val lineChartData: List<Pair<String, Float>> = mutableListOf()
)
