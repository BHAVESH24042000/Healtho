package com.example.healtho.homescreens

import com.example.healtho.auth.AuthRepo
import com.example.healtho.firebase.FirestoreWaterDataSource
import com.example.healtho.homescreens.models.Water
import com.example.healtho.room.RoomWaterDataSource
import com.example.healtho.util.Objects.USER_DOES_NOT_EXIST
import com.example.healtho.util.Objects.getTimeOfLastWeek
import com.example.healtho.util.Objects.getTodayDayNo
import com.example.healtho.util.Objects.getTodaysTime
import com.example.healtho.util.Objects.waterFOTD
import com.example.healtho.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class WaterRepo @Inject constructor(
    private val waterRoomDbDataSource: RoomWaterDataSource,
    private val firebaseWaterDataSource: FirestoreWaterDataSource,
    private val authRepo: AuthRepo,

    ) {

    fun getTodaysWaterLogs() = waterRoomDbDataSource.getAllWaterLogsAfterTime(getTodaysTime())
        .flowOn(Dispatchers.IO)

    fun getAllWaterLogsOfLastWeek() =  waterRoomDbDataSource.getAllWaterLogsAfterTime(getTimeOfLastWeek())
        .flowOn(Dispatchers.IO)

    fun getFOTD(): String {
        val day = getTodayDayNo()
        return waterFOTD[day]
    }

    suspend fun fetchAllWaterLogs(): Resource<Unit> = withContext(Dispatchers.IO) {
        return@withContext authRepo.getCurrentUser()?.let {
            val resource = firebaseWaterDataSource.getAllWaterLogs(it.email)
            if (resource is Resource.Success<*>) {
                //dumpNewWaterLogsDataIntoDb(waterMapper.toEntityList(resource.data!!))
                dumpNewWaterLogsDataIntoDb((resource.data!!))
                Resource.Success<Unit>()
            } else Resource.Error(resource.message, errorType = resource.errorType)
        } ?: Resource.Error(USER_DOES_NOT_EXIST)
    }

    private suspend fun dumpNewWaterLogsDataIntoDb(water: List<Water>) {
        deleteAllWaterLogs()
        insertWaterIntoDb(water)
    }

    private suspend fun insertWaterIntoDb(water: List<Water>) {
        waterRoomDbDataSource.insertWater(water)
    }

    suspend fun deleteAllWaterLogs() = waterRoomDbDataSource.deleteAllWaterLogs()

    suspend fun insertIntoWaterLog(water: Water): Resource<Unit> = withContext(Dispatchers.IO) {
        return@withContext authRepo.getCurrentUser()?.let {
            //val waterDTO = waterMapper.toDTO(water)
            val waterDTO = water
            waterDTO.userEmail = it.email
            val resource = firebaseWaterDataSource.addWater(it.email, waterDTO)
            if (resource is Resource.Success) {
                insertWaterIntoDb(listOf(water))
                Resource.Success<Unit>()
            } else Resource.Error(resource.message, errorType = resource.errorType)
        } ?: Resource.Error(USER_DOES_NOT_EXIST)
    }
}

