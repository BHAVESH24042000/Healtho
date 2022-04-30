package com.example.healtho.homescreens

import com.example.healtho.auth.AuthRepo
import com.example.healtho.firebase.FirebaseWorkoutDataSource
import com.example.healtho.firebase.FirestoreWaterDataSource
import com.example.healtho.homescreens.models.Water
import com.example.healtho.homescreens.models.Workout
import com.example.healtho.room.RoomWaterDataSource
import com.example.healtho.room.RoomWorkoutDataSource
import com.example.healtho.util.Objects
import com.example.healtho.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkoutRepo @Inject constructor(
    private val workoutFirebaseDataSource: FirebaseWorkoutDataSource,
    private val workoutRoomDataSource: RoomWorkoutDataSource,
    private val authRepo: AuthRepo,

    ) {

    fun getTodaysWorkoutLogs() = workoutRoomDataSource.getAllWorkoutLogsAfterTime(Objects.getTodaysTime())
        .flowOn(Dispatchers.IO)

    fun getLastWeekWorkoutLogs() = workoutRoomDataSource.getAllWorkoutLogsAfterTime(Objects.getTimeOfLastWeek())
        .flowOn(Dispatchers.IO)

//    fun getFOTD(): String {
////        val day = Objects.getTodayDayNo()
////        return Objects.waterFOTD[day]
//    }
    suspend fun fetchAllWorkoutLogs(): Resource<Unit> = withContext(Dispatchers.IO) {
        return@withContext authRepo.getCurrentUser()?.let {
            val resource = workoutFirebaseDataSource.getAllWorkoutLogs(it.email)
            if (resource is Resource.Success<*>) {
                //dumpNewWaterLogsDataIntoDb(waterMapper.toEntityList(resource.data!!))
                dumpNewWaterLogsDataIntoDb((resource.data!!))
                Resource.Success<Unit>()
            } else Resource.Error(resource.message, errorType = resource.errorType)
        } ?: Resource.Error(Objects.USER_DOES_NOT_EXIST)
    }

    private suspend fun dumpNewWaterLogsDataIntoDb(workout: List<Workout>) {
        deleteAllWorkoutLogs()
        insertWorkoutIntoDb(workout)
    }

    private suspend fun insertWorkoutIntoDb(water: List<Workout>) {
        workoutRoomDataSource.insertWorkout(water)
    }

    suspend fun deleteAllWorkoutLogs() = workoutRoomDataSource.deleteAllWorkoutLogs()

    suspend fun insertIntoWorkoutLog(workout: Workout): Resource<Unit> = withContext(Dispatchers.IO) {
        return@withContext authRepo.getCurrentUser()?.let {
            //val waterDTO = waterMapper.toDTO(water)
            val workoutDTO = workout
            workoutDTO.userEmail = it.email
            val resource = workoutFirebaseDataSource.addWater(it.email, workoutDTO)
            if (resource is Resource.Success) {
                //deleteAllWorkoutLogs()
                //insertWorkoutIntoDb(listOf(workout))
                    fetchAllWorkoutLogs()
                Resource.Success<Unit>()
            } else Resource.Error(resource.message, errorType = resource.errorType)
        } ?: Resource.Error(Objects.USER_DOES_NOT_EXIST)
    }
}

