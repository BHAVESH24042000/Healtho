package com.example.healtho.homescreens

import com.example.healtho.auth.AuthRepo
import com.example.healtho.firebase.FirestoreSleepDataSource
import com.example.healtho.homescreens.models.Sleep
import com.example.healtho.room.RoomSleepDataSource
import com.example.healtho.util.Objects.USER_DOES_NOT_EXIST
import com.example.healtho.util.Objects.getTimeOfLastWeek
import com.example.healtho.util.Objects.getTodayDayNo
import com.example.healtho.util.Objects.getTodaysTime
import com.example.healtho.util.Objects.sleepFOTD
import com.example.healtho.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SleepRepo @Inject constructor(
    private val sleepRoomDbDataSource: RoomSleepDataSource,
    private val fireStoreSleepDataSource: FirestoreSleepDataSource,
    private val authRepo: AuthRepo,

    ) {

    fun getTodaysSleepLogs() =
        sleepRoomDbDataSource.getAllSleepAfterTime(getTodaysTime()).flowOn(Dispatchers.IO)


    fun getAllSleepsOfLastWeek() = sleepRoomDbDataSource.getAllSleepAfterTime(getTimeOfLastWeek())
        .flowOn(Dispatchers.IO)


    suspend fun fetchAllSleepLogs(): Resource<Unit> = withContext(Dispatchers.IO) {
        return@withContext authRepo.getCurrentUser()?.let {
            val resource = fireStoreSleepDataSource.getAllSleepLogs(it.email)
            if (resource is Resource.Success<*>) {
                //dumpNewSleepLogsDataIntoDb(sleepMapper.toEntityList(resource.data!!))
                resource.data?.let { it1 -> dumpNewSleepLogsDataIntoDb(it1) }
                Resource.Success<Unit>()
            } else Resource.Error(resource.message, errorType = resource.errorType)
        } ?: Resource.Error(USER_DOES_NOT_EXIST)
    }

    private suspend fun insertSleepIntoDb(sleeps: List<Sleep>) {
        sleepRoomDbDataSource.insertSleep(sleeps)
    }

    private suspend fun dumpNewSleepLogsDataIntoDb(sleeps: List<Sleep>) {
        deleteAllSleepLogs()
        insertSleepIntoDb(sleeps)
    }

    suspend fun insertIntoSleepLog(sleep: Sleep): Resource<Unit> = withContext(Dispatchers.IO) {
        return@withContext authRepo.getCurrentUser()?.let {
            val sleepDTO = sleep
            sleepDTO.userEmail = it.email
            val resource = fireStoreSleepDataSource.addSleep(it.email, sleepDTO)
            if (resource is Resource.Success<*>) {
                insertSleepIntoDb(listOf(sleep))
                Resource.Success<Unit>()
            } else Resource.Error(resource.message, errorType = resource.errorType)
        } ?: Resource.Error(USER_DOES_NOT_EXIST)
    }
    suspend fun deleteAllSleepLogs() = sleepRoomDbDataSource.deleteAll()

    fun getFOTD(): String {
        val day = getTodayDayNo()
        return sleepFOTD[day]
    }
}
